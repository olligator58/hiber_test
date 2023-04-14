import jakarta.persistence.criteria.*;
import model.Employee;
import model.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class CriteriaManager {
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }

    public List<Employee> getAllEmployees() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = builder.createQuery(Employee.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);
            criteriaQuery.select(root);

            Query<Employee> query = session.createQuery(criteriaQuery);
            return query.list();
        }
    }

    public List<Employee> getEmployeesWithMoreSalary(int salary) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = builder.createQuery(Employee.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);

            criteriaQuery.select(root)
                    .where(builder.gt(root.get("salary"), salary));

            return session.createQuery(criteriaQuery).list();
        }
    }

    public List<Employee> getEmployeesWithLessSalary(int salary) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = builder.createQuery(Employee.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);
            criteriaQuery.select(root)
                    .where(builder.lt(root.get("salary"), salary));

            return session.createQuery(criteriaQuery).list();
        }
    }

    public List<Employee> getEmployeesWithOccupationLike(String occupation) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = builder.createQuery(Employee.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);
            criteriaQuery.select(root)
                    .where(builder.like(root.get("occupation"), occupation));

            return session.createQuery(criteriaQuery).list();
        }
    }

    public List<Employee> getEmployeesWithSalaryBetween(int minSalary, int maxSalary) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = builder.createQuery(Employee.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);
            criteriaQuery.select(root)
                    .where(builder.between(root.get("salary"), minSalary, maxSalary));

            return session.createQuery(criteriaQuery).list();
        }
    }

    public List<Task> getTasksWithNullDeadline() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Task> criteriaQuery = builder.createQuery(Task.class);

            Root<Task> root = criteriaQuery.from(Task.class);
            criteriaQuery.select(root)
                    .where(builder.isNull(root.get("deadline")));

            return session.createQuery(criteriaQuery).list();
        }
    }

    public List<Employee> getEmployeesWithSalaryAndAgeBetween(int minSalary, int maxSalary, int minAge, int maxAge) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = builder.createQuery(Employee.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);
            Predicate salaryBetween = builder.between(root.get("salary") , minSalary, maxSalary);
            Predicate ageBetween = builder.between(root.get("age"), minAge, maxAge);

            criteriaQuery.select(root)
                    .where(builder.and(salaryBetween, ageBetween));

            return session.createQuery(criteriaQuery).list();
        }
    }

    public List<Employee> getEmployeesOrderedByAgeAndSalary() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> criteriaQuery = builder.createQuery(Employee.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);
            criteriaQuery.select(root)
                    .orderBy(builder.asc(root.get("age")), builder.desc(root.get("salary")));

            return session.createQuery(criteriaQuery).list();
        }
    }

    public int getEmployeesCount() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);
            criteriaQuery.select(builder.count(root));

            return session.createQuery(criteriaQuery).getSingleResult().intValue();
        }
    }

    public double getAvgSalary() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Double> criteriaQuery = builder.createQuery(Double.class);

            Root<Employee> root = criteriaQuery.from(Employee.class);
            criteriaQuery.select(builder.avg(root.get("salary")));

            return session.createQuery(criteriaQuery).getSingleResult();
        }
    }

    public void addSalaryToEmployeesOlderThan(int salaryIncrement, int age) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Employee> criteriaUpdate = builder.createCriteriaUpdate(Employee.class);

            Root<Employee> root = criteriaUpdate.from(Employee.class);
            criteriaUpdate.set("salary", "salary+" + salaryIncrement); // не работает, т.к второй аргумент д.б. int
            criteriaUpdate.where(builder.gt(root.get("age"), age));

            Transaction transaction = session.beginTransaction();
            session.createQuery(criteriaUpdate).executeUpdate();
            transaction.commit();
        }
    }

    public static void main(String[] args) {
        CriteriaManager manager = new CriteriaManager();
        manager.init();
        System.out.println("Список всех сотрудников:");
        for (Employee employee : manager.getAllEmployees()) {
            System.out.println(employee);
        }

        int salary = 10000;
        System.out.println(String.format("Сотрудники с зарплатой выше %d:", salary));
        for (Employee employee : manager.getEmployeesWithMoreSalary(salary)) {
            System.out.println(employee);
        }

        salary = 50000;
        System.out.println(String.format("Сотрудники с зарплатой ниже %d:", salary));
        for (Employee employee : manager.getEmployeesWithLessSalary(salary)) {
            System.out.println(employee);
        }

        String occupation = "%тест%";
        System.out.println(String.format("Сотрудники с должностью по маске %s", occupation));
        for (Employee employee : manager.getEmployeesWithOccupationLike(occupation)) {
            System.out.println(employee);
        }

        int minSalary = 10000;
        int maxSalary = 50000;
        System.out.println(String.format("Сотрудники с зарплатой от %d до %d:", minSalary, maxSalary));
        for (Employee employee : manager.getEmployeesWithSalaryBetween(minSalary, maxSalary)) {
            System.out.println(employee);
        }

        System.out.println("Задачи с пустой датой окончания:");
        for (Task task : manager.getTasksWithNullDeadline()) {
            System.out.println(task);
        }

        minSalary = 20000;
        maxSalary = 80000;
        int minAge = 20;
        int maxAge = 30;
        System.out.println(String.format("Сотрудники с зарплатой от %d до %d и возрастом от %d до %d",
                                         minSalary, maxSalary, minAge, maxAge));
        for (Employee employee : manager.getEmployeesWithSalaryAndAgeBetween(minSalary, maxSalary, minAge, maxAge)) {
            System.out.println(employee);
        }

        System.out.println("Сотрудники, упорядоченные по возрасту и убыванию зарплаты:");
        for (Employee employee : manager.getEmployeesOrderedByAgeAndSalary()) {
            System.out.println(employee);
        }

        System.out.println(String.format("Количество сотрудников: %d", manager.getEmployeesCount()));

        System.out.println(String.format("Средняя зарплата сотрудников: %.2f", manager.getAvgSalary()));


        int salaryIncrement = 2000;
        minAge = 24;
        System.out.println(String.format("Прибавляем зарплату на %d сотрудникам старше %d лет:",
                                         salaryIncrement, minAge));
        // метод не работает
        //  manager.addSalaryToEmployeesOlderThan(salaryIncrement, minAge);
    }
}
