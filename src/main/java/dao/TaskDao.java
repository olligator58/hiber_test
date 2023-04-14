package dao;

import model.Task;

public class TaskDao extends AbstractHibernateDao<Task> {

    public TaskDao() {
        super(Task.class);
    }
}
