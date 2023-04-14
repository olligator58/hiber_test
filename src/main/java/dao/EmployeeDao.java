package dao;

import model.Employee;

public class EmployeeDao extends AbstractHibernateDao<Employee> {

    public EmployeeDao(){
        super(Employee.class );
    }
}
