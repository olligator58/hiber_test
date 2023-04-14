import dao.EmployeeDao;
import model.Employee;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DaoManager {

    public static void main(String[] args) {
        EmployeeDao employeeDao = new EmployeeDao();

        Employee employee = new Employee();
        employee.setName("Безхозный Глеб");
        employee.setOccupation("Дворник");
        employee.setSalary(30000);
        employee.setAge(53);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2020, 1, 12);
        employee.setJoinDate(calendar.getTime());
        employeeDao.create(employee);

    }
}
