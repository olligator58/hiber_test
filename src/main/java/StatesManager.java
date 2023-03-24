import model.Employee;
import model.Task;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class StatesManager {
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }

    public int insertEmployeeSave(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            int id = (Integer) session.save(employee);
            transaction.commit();
            return id;
        }
    }

    public void insertEmployeePersist(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(employee);
            transaction.commit();
        }
    }

    public Employee insertEmployeeMerge(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Employee result = session.merge(employee);
            transaction.commit();
            return result;
        }
    }

    public void insertEmployeeSaveOrUpdate(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(employee);
            transaction.commit();
        }
    }

    public Employee updateEmployeeMerge(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Employee result = session.merge(employee);
            transaction.commit();
            return result;
        }
    }

    public void updateEmployeeUpdate(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(employee);
            transaction.commit();
        }
    }

    public void updateEmployeeSaveOrUpdate(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(employee);
            transaction.commit();
        }
    }

    // для объектов в статусе Persistent все изменения записываются в базу автоматически после закрытия сессии
    public List<Employee> addSalaryToEmployeesByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String hql = "from Employee where name like :name";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("name", name);
            List<Employee> persistentEmployees = query.list();
            for (Employee persistentEmployee : persistentEmployees) {
                persistentEmployee.setSalary(persistentEmployee.getSalary() + 1000);
            }
            transaction.commit();
            return persistentEmployees;
        }
    }

    public Employee setEmployeeAgeById(int id, int age) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Employee persistentEmployee = session.find(Employee.class, id);
            persistentEmployee.setAge(age);
            transaction.commit();
            return persistentEmployee;
        }
    }

    public void deleteEmployee(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(employee);
            transaction.commit();
        }
    }

    public int deleteEmployeesStartingFromId(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String hql = "from Employee where id >= :id";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("id", id);
            int result = query.list().size();
            for (Employee employee : query.list()) {
                session.remove(employee);
            }
            transaction.commit();
            return result;
        }
    }

    private Employee buildEmployee() {
        Employee employee = new Employee();
        employee.setName("Христофоров Глеб");
        employee.setOccupation("Тестировщик");
        employee.setSalary(40000);
        employee.setAge(25);
        employee.setJoinDate(new Date());
        return employee;
    }

    private int getEmployeeMaxId() {
        try (Session session = sessionFactory.openSession()) {
            ScrollableResults<Employee> results = session.createQuery("from Employee", Employee.class).scroll();
            results.last();
            return results.get().getId();
        }
    }


    public static void main(String[] args) {
        StatesManager manager = new StatesManager();
        manager.init();

        Employee transientEmployee = manager.buildEmployee();
        int id = manager.insertEmployeeSave(transientEmployee);
        System.out.println(String.format("Добавлен сотрудник с id = %d:", id));
        System.out.println(transientEmployee);

        transientEmployee = manager.buildEmployee();
        manager.insertEmployeePersist(transientEmployee);
        System.out.println("Добавлен сотрудник:");
        System.out.println(transientEmployee);

        transientEmployee = manager.buildEmployee();
        Employee persistentEmployee = manager.insertEmployeeMerge(transientEmployee);
        System.out.println("Изначальный сотрудник:");
        System.out.println(transientEmployee);
        System.out.println("Добавленный сотрудник:");
        System.out.println(persistentEmployee);
        System.out.println("Класс добавленного сотрудника:");
        System.out.println(persistentEmployee.getClass().getName());

        transientEmployee = manager.buildEmployee();
        manager.insertEmployeeSaveOrUpdate(transientEmployee);
        System.out.println("Добавлен сотрудник:");
        System.out.println(transientEmployee);

        id = manager.getEmployeeMaxId();
        transientEmployee = manager.buildEmployee();
        transientEmployee.setId(id - 3);
        transientEmployee.setName("Хрюндель");
        transientEmployee.setSalary(10);
        transientEmployee.setOccupation("Дворник");
        persistentEmployee = manager.updateEmployeeMerge(transientEmployee);
        System.out.println("Изначальный сотрудник:");
        System.out.println(transientEmployee);
        System.out.println("Измененный сотрудник:");
        System.out.println(persistentEmployee);

        id = manager.getEmployeeMaxId();
        transientEmployee = manager.buildEmployee();
        transientEmployee.setId(id - 2);
        transientEmployee.setName("Водкин");
        manager.updateEmployeeUpdate(transientEmployee);
        System.out.println("Изменен сотрудник:");
        System.out.println(transientEmployee);

        id = manager.getEmployeeMaxId();
        transientEmployee = manager.buildEmployee();
        transientEmployee.setId(id - 1);
        transientEmployee.setName("Авнюков");
        transientEmployee.setOccupation("Ассенизатор");
        transientEmployee.setAge(50);
        transientEmployee.setSalary(20000);
        manager.updateEmployeeSaveOrUpdate(transientEmployee);
        System.out.println("Изменен сотрудник:");
        System.out.println(transientEmployee);

        String name = "Хр%";
        System.out.printf("Прибавлены зарплаты сотрудникам с именем %s\n", name);
        for (Employee employee : manager.addSalaryToEmployeesByName(name)) {
            System.out.println(employee);
        }

        id = manager.getEmployeeMaxId() - 1;
        int age = 100;
        System.out.printf("Сотруднику с id = %d установлен возраст %d\n", id, age);
        System.out.println(manager.setEmployeeAgeById(id, age));

        id = manager.getEmployeeMaxId();
        transientEmployee = manager.buildEmployee();
        transientEmployee.setId(id);
        manager.deleteEmployee(transientEmployee);
        System.out.printf("Удален сотрудник с id = %d\n", id);

        id = 7; // удаляем сотрудников, начиная с этого id
//        System.out.printf("Удалено %d сотрудников", manager.deleteEmployeesStartingFromId(id));
    }
}
