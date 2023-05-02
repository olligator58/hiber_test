import dao.EmployeeDao;
import jakarta.persistence.Query;
import model.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.io.InputStream;

public class TestDataManager {
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
    }

    public void runSqlScriptFile(String fileName) {
        try (Session session = sessionFactory.openSession();
             InputStream is = this.getClass().getResourceAsStream(fileName)) {

            Transaction transaction = session.beginTransaction();
            String sqlScript = new String(is.readAllBytes());
            Query query = session.createNativeQuery(sqlScript);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteEmployeesFromId(int id) {
        EmployeeDao employeeDao = new EmployeeDao();
        for (Employee employee : employeeDao.findAll()) {
            if (employee.getId() >= id) {
                employeeDao.deleteById(employee.getId());
            }
        }
    }

    public static void main(String[] args) {
        TestDataManager manager = new TestDataManager();
        manager.init();
        manager.runSqlScriptFile("test-data.sql");

        int id = 10;
//        manager.deleteEmployeesFromId(id);
    }
}
