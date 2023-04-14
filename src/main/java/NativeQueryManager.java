import model.Employee;
import model.Task;
import model.UserDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NativeQueryManager {
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }

    public void printEmployeesIdsNamesAndJoinDates() {
        try (Session session = sessionFactory.openSession()) {
            List<Object[]> employees = session.createNativeQuery("SELECT id, name, join_date FROM employee").list();
            for (Object[] employee : employees) {
                int id = (int) employee[0];
                String name = (String) employee[1];
                Date joinDate = (Date) employee[2];
                System.out.println(String.format("Сотрудник: id=%d, name=%s, join_date=%s", id, name, joinDate));
            }
        }
    }

    public List<Employee> getAllEmployees() {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT * from employee";
            Query<Employee> query = session.createNativeQuery(sql, Employee.class);
            return query.list();
        }
    }

    public void printTasksOwnersNames() {
        try (Session session = sessionFactory.openSession()) {
            List<Task> tasks = session.createNativeQuery(
                    "SELECT {t.*}, {e.*} " +
                       "FROM task t " +
                       "JOIN employee e ON t.employee_id = e.id")
                    .addEntity("t", Task.class)
                    .addJoin("e", "t.employee")
                    .list();

            for (Task task : tasks) {
                System.out.println(String.format("Задача: id=%d, name=%s", task.getId(), task.getEmployee().getName()));
            }
        }
    }

    public List<UserDTO> getAllUserDTOs() {
        try (Session session = sessionFactory.openSession()) {
            List<UserDTO> result = new ArrayList<>();
            String sql = "SELECT * FROM user";
            List<Object[]> users = session.createNativeQuery(sql)
                    .list();

            for (Object[] user : users) {
                UserDTO dto = new UserDTO();
                dto.setId((int) user[0]);
                dto.setName((String) user[1]);
                dto.setLevel((int) user[2]);
                dto.setJoinDate((Date) user[3]);
                result.add(dto);
            }

            return result;
        }
    }

    public static void main(String[] args) {
        NativeQueryManager manager = new NativeQueryManager();
        manager.init();
        manager.printEmployeesIdsNamesAndJoinDates();

        System.out.println("Список всех сотрудников:");
        for (Employee employee : manager.getAllEmployees()) {
            System.out.println(employee);
        }

        manager.printTasksOwnersNames();

        System.out.println("Данные из таблицы user:");
        for (UserDTO dto : manager.getAllUserDTOs()) {
            System.out.println(dto);
        }
    }
}
