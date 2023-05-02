import dao.EmployeeDao;
import dao.TaskDao;
import model.Employee;
import model.Task;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DaoManager {

    public static void main(String[] args) {
        EmployeeDao employeeDao = new EmployeeDao();
        TaskDao taskDao = new TaskDao();

        int id = 3;
        System.out.println(String.format("Сотрудник с id = %d", id));
        System.out.println(employeeDao.getById(id));

        id = 5;
        System.out.println(String.format("Задача с id = %d", id));
        System.out.println(taskDao.getById(id));

        int from = 2;
        int count = 3;
        System.out.println(String.format("%d сотрудников, начиная с %d-го", count, from + 1));
        for (Employee employee : employeeDao.getItems(from, count)) {
            System.out.println(employee);
        }

        System.out.println("Список всех задач:");
        for (Task task : taskDao.findAll()) {
            System.out.println(task);
        }

        Employee employee = new Employee();
        employee.setName("Безхозный Глеб");
        employee.setOccupation("Дворник");
        employee.setSalary(30000);
        employee.setAge(53);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2020, 1, 12);
        employee.setJoinDate(calendar.getTime());
        Task task = new Task();
        task.setName("Подмести двор");
        task.setEmployee(employee);
        employee.getTasks().add(task);
        employeeDao.create(employee);
        System.out.println("Добавлен сотрудник:");
        System.out.println(employee);

        employee.setName("Жарикофф Петр");
        employeeDao.update(employee);
        System.out.println("Изменен сотрудник:");
        System.out.println(employee);

        taskDao.delete(task);
        System.out.println("Удалена задача:");
        System.out.println(task);

        id = 10;
        System.out.println();
        for (Employee e : employeeDao.findAll()) {
            if (e.getId() >= id) {
                System.out.println("Удален сотрудник:");
                System.out.println(e);
                employeeDao.deleteById(e.getId());
            }
        }
    }
}
