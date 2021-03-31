package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User
{
    /**
     * Identification number
     */
    @Id
    @GeneratedValue
    int id;
    /**
     * User's nickname for login
     */
    String nick;
    /**
     * User name
     */
    String name;
    /**
     * User surname
     */
    String surname;
    /**
     * Hash SHA256 from user password
     */
    String password;

    public User(){
       super();
    }

    public User(String nick, String name, String surname, String password) {
        this.nick = nick;
        this.name = name;
        this.surname = surname;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
