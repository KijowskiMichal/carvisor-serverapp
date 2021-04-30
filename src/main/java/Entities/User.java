package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class User
{
    /**
     * Identification number
     */
    @Id
    @GeneratedValue
    private int id;
    /**
     * User's nickname for login
     */
    private String nick;
    /**
     * User name
     */
    private String name;
    /**
     * User surname
     */
    private String surname;
    /**
     * Hash SHA256 from user password
     */
    private String password;
    /**
     * RBAC value
     */
    private UserPrivileges userPrivileges;
    /**
     * Current track
     */
    @OneToOne
    private Track track;


    public User(){
       super();
    }

    public User(String nick, String name, String surname, String password, UserPrivileges userPrivileges) {
        this.nick = nick;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.userPrivileges = userPrivileges;
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

    public UserPrivileges getUserPrivileges() {
        return userPrivileges;
    }

    public void setUserPrivileges(UserPrivileges userPrivileges) {
        this.userPrivileges = userPrivileges;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}
