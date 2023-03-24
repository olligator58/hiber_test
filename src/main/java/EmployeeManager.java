import model.Employee;
import model.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class EmployeeManager {
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }

    public Employee getEmployeeById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Employee.class, id);
        }
    }

    public List<Employee> getAllEmployees() {
        try (Session session = sessionFactory.openSession()) {
            Query<Employee> query = session.createQuery("FROM Employee", Employee.class);
            return query.list();
        }
    }

    public boolean insertEmployee(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(employee);
            transaction.commit();
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(employee);
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(employee);
            transaction.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        EmployeeManager manager = new EmployeeManager();
        manager.init();

        Employee employee = manager.getEmployeeById(5);
        System.out.println("Сотрудник с id = 5: ");
        System.out.println(employee);

        employee = new Employee();
//        employee.setId(101); // поле id генерируется автоматически
        employee.setName("Хохряков Петр");
        employee.setOccupation("Завхоз");
        employee.setSalary(50000);
        employee.setAge(53);
        employee.setJoinDate(new Date());
        // добавление отработает только с 3-го раза, когда увеличится счетчик в employee_seq
        if (manager.insertEmployee(employee)) {
            System.out.println("Добавлен новый сотрудник: ");
            System.out.println(employee);
        } else {
            System.out.println(String.format("Добавить сотрудника с id = %d не удалось", employee.getId()));
        }

        // всем хохряковым прибавим зарплату
        for (Employee e : manager.getAllEmployees()) {
            if (e.getName().startsWith("Хохряков")) {
                e.setSalary(e.getSalary() + 1000);
                if (manager.updateEmployee(e)) {
                    System.out.println(String.format("Прибавлена зарплата сотруднику с id=%d", e.getId()));
                } else {
                    System.out.println(String.format("Прибавить зарплату сотруднику с id=%d не удалось", e.getId()));
                }
            }
        }

        System.out.println("Список всех сотрудников: ");
        for (Employee e : manager.getAllEmployees()) {
            System.out.println(e);
        }

        //удаляем всех хохряковых
        /*for (Employee e : manager.getAllEmployees()) {
            if (e.getName().startsWith("Хохряков")) {
                boolean removeSuccess = manager.deleteEmployee(e);
                System.out.println((removeSuccess) ? "Удален сотрудник: " : "Не удалось удалить сотрудника: ");
                System.out.println(e);
            }
        }*/
    }
}
