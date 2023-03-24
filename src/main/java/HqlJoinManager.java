import model.Employee;
import model.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.Arrays;
import java.util.List;

public class HqlJoinManager {
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }

    public List<Task> getKirienkosTasks() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Task where employee.name like 'Кириенко%'";
            return session.createQuery(hql, Task.class).list();
        }
    }

    public List<Employee> getEmployeesWithExpiredTasks() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select distinct employee from Task where deadline < CURDATE()";
            return session.createQuery(hql, Employee.class).list();
        }
    }

    public List<Task> getTaskByEmployeeName(String name) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Task where employee.name = :name";
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("name", name);
            return query.list();
        }
    }

    public List<Task> getTaskByEmployeesOccupations(List<String> occupations) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Task where employee.occupation in (:occupation_list)";
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameterList("occupation_list", occupations);
            return query.list();
        }
    }

    public List<Task> getTasksWithLimitAndOffset(int limit, int offset) {
        try (Session session = sessionFactory.openSession()) {
            Query<Task> query = session.createQuery("from Task", Task.class);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.list();
        }
    }

    public List<Employee> getOrderedEmployees() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Employee order by salary, occupation desc";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            return query.list();
        }
    }

    public int getEmployeesCount() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select count(*) from Employee";
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.uniqueResult().intValue();
        }
    }

    public double getAvgSalary() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select avg(salary) from Employee";
            Query<Double> query = session.createQuery(hql, Double.class);
            return query.uniqueResult();
        }
    }

    public static void main(String[] args) {
        HqlJoinManager manager = new HqlJoinManager();
        manager.init();
        System.out.println("Все задачи Кириенко:");
        for (Task task : manager.getKirienkosTasks()) {
            System.out.println(task);
        }

        System.out.println("Сотрудники с просроченными задачами:");
        for (Employee employee : manager.getEmployeesWithExpiredTasks()) {
            System.out.println(employee);
        }

        String name = "Иванов Иван";
        System.out.println(String.format("Задачи для сотрудника %s:", name));
        for (Task task : manager.getTaskByEmployeeName(name)) {
            System.out.println(task);
        }

        List<String> occupations = Arrays.asList("Программист", "Тестировщик");
        System.out.println(String.format("Задачи для сотрудников с профессиями %s:", occupations));
        for (Task task : manager.getTaskByEmployeesOccupations(occupations)) {
            System.out.println(task);
        }

        int limit = 5;
        int offset = 2; // сколько записей пропускаем
        System.out.println(String.format("Выводим %d задач начиная с %d-ой:", limit, offset + 1));
        for (Task task : manager.getTasksWithLimitAndOffset(limit, offset)) {
            System.out.println(task);
        }

        System.out.println("Сотрудники, упорядоченные по убыванию зарплаты и по имени:");
        for (Employee employee : manager.getOrderedEmployees()) {
            System.out.println(employee);
        }

        int totalEmployees = manager.getEmployeesCount();
        System.out.println(String.format("Всего записей в таблице employee: %d", totalEmployees));

        double avgSalary = manager.getAvgSalary();
        System.out.println(String.format("Средняя зарплата сотрудников: %.2f", avgSalary));
    }
}
