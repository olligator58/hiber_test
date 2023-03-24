package model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

// именованные запросы
@org.hibernate.annotations.NamedQueries({
    @org.hibernate.annotations.NamedQuery(name = "FindById", query = "from User where id = :id"),
    @org.hibernate.annotations.NamedQuery(name = "FindAllUsers", query = "from User"),
    @org.hibernate.annotations.NamedQuery(name = "UpdateUserLevel",
    query = "update User set level = level + 1 where id = :id"),
})

// именованные Native запросы
@org.hibernate.annotations.NamedNativeQueries({
    @org.hibernate.annotations.NamedNativeQuery(name = "GetAllNamedNative", query = "select * from user")
})

@Entity
@Table(name="user")
public class User {
    private int id;
    private String name;
    private int level;
    private Date createdDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Column(name = "created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", createdDate=" + createdDate +
                '}';
    }
}
