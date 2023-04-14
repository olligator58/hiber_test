import model.Employee;
import model.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class TaskManager {
    private static final int PAGE_SIZE = 2;
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }

    public List<Task> getTasksPage(int employeeId, int pageIndex) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select tasks from Employee where id = :id";
            Query<Task> query = session.createQuery(hql, Task.class);
            query.setParameter("id", employeeId);
            query.setFirstResult(pageIndex * PAGE_SIZE);
            query.setMaxResults(PAGE_SIZE);
            return query.list();
        }
    }

    public int getTasksPageCount(int employeeId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Employee where id = :id";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("id", employeeId);
            return (int) Math.ceil(query.getSingleResult().getTasks().size() * 1.0 / PAGE_SIZE);
        }
    }

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        manager.init();
        int id = 5;
        int pageCount = manager.getTasksPageCount(id);
        System.out.println(String.format("Всего страниц: %d", pageCount));
        for (int i = 0; i < pageCount; i++) {
            System.out.println(String.format("Страница %d:", i + 1));
            for (Task task : manager.getTasksPage(id, i)) {
                System.out.println(task);
            }
        }
    }
}
