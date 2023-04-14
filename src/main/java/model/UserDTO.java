package model;

import java.util.Date;

// просто класс без аннотаций, чтобы вытащить данные из таблицы User
// используется в NativeQueryManager
public class UserDTO {
    private int id;
    private String name;
    private int level;
    private Date joinDate;

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

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", joinDate=" + joinDate +
                '}';
    }
}
