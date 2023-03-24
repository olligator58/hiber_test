import model.Employee;
import model.Task;
import model.User;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class HqlManager {
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User", User.class).list();
        }
    }

    public List<Employee> getAllEmployees() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Employee", Employee.class).getResultList();// то же, что и list()
        }
    }

    public List<Task> getAllTasks() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Task", Task.class).list();
        }
    }

    public List<String> getUsersNames() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select distinct u.name from User u where createdDate > '2022-06-01'";
            Query<String> query = session.createQuery(hql, String.class);
            return query.list();
        }
    }

    public Employee getEmployeeById(int id) {
        String hql = String.format("from Employee where id = %d", id);
        try (Session session = sessionFactory.openSession()) {
            Query<Employee> query = session.createQuery(hql, Employee.class);
            Employee employee = query.uniqueResult(); // или getSingleResult()
            return employee;
        }
    }

    public Employee getLastEmployee() {
        try (Session session = sessionFactory.openSession()) {
            Query<Employee> query = session.createQuery("from Employee", Employee.class);
            ScrollableResults<Employee> scroll = query.scroll();
            scroll.last();
            Employee employee = scroll.get();
            return employee;
        }
    }

    public static void main(String[] args) {
        HqlManager manager = new HqlManager();
        manager.init();

        System.out.println("Список всех пользователей:");
        for (User user : manager.getAllUsers()) {
            System.out.println(user);
        }

        System.out.println("Список имен:");
        for (String name : manager.getUsersNames()) {
            System.out.println(name);
        }

        System.out.println("Сотрудники и их задачи:");
        for (Employee employee : manager.getAllEmployees()) {
            System.out.println("Сотрудник:");
            System.out.println(employee);
            System.out.println("Задачи:");
            for (Task task : employee.getTasks()) {
                System.out.println(task);
            }
        }

        System.out.println("Сотрудник с id=3:");
        System.out.println(manager.getEmployeeById(3));

        System.out.println("Последний сотрудник в списке:");
        System.out.println(manager.getLastEmployee());
    }
}
