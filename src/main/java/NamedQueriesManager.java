import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.util.List;

public class NamedQueriesManager {
    private SessionFactory sessionFactory;

    public void init() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }

    public User getUserById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createNamedQuery("FindById", User.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        }
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createNamedQuery("FindAllUsers", User.class);
            return query.list();
        }
    }

    public int UpdateUserLevelById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Query query = session.createNamedQuery("UpdateUserLevel");
            query.setParameter("id", id);
            int result = query.executeUpdate();
            transaction.commit();
            return result;
        }
    }

    public List<String> getAllUsersNative() {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT name FROM employee";
            NativeQuery<String> query = session.createNativeQuery(sql, String.class);
            return query.list();
        }
    }

    public static void main(String[] args) {
        NamedQueriesManager manager = new NamedQueriesManager();
        manager.init();

        int id = 3;
        System.out.println(String.format("Пользователь с id = %d:", id));
        System.out.println(manager.getUserById(id));

        System.out.println("Список всех пользователей:");
        for (User user : manager.getAllUsers()) {
            System.out.println(user);
        }

        id = 5;
        if (manager.UpdateUserLevelById(id) > 0) {
            System.out.println(String.format("Пользователь с id = %d успешно обновлен:", id));
            System.out.println(manager.getUserById(id));
        } else {
            System.out.println(String.format("Пользователя с id = %d обновить не удалось:", id));
        }

        System.out.println("Список имен всех пользователей:");
        for (String name : manager.getAllUsersNative()) {
            System.out.println(name);
        }
    }
}
