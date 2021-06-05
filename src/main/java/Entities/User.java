package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

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
     * Image of user
     */
    @Lob
    private String image;
    /**
     *  Phone number of user
     */
    int phoneNumber;
    /**
     * NFC tag
     */
    String nfcTag;
    /**
     * Eco Point
     */
    float ecoPointsAvg;
    /**
     * Average combustion
     */
    float combustionAVG;
    /**
     * Average  speed
     */
    int speedAVG;
    /**
     * number of tracks
     */
    int tracksNumber;
    /**
     * Average  revolutions
     */
    int revolutionsAVG;
    /**
     * Distance Travelled;
     */
    long distanceTravelled;
    /**
     * Number of samples
     */
    int samples;


    public User(){
       super();
    }

    public User(String nick, String name, String surname, String password, UserPrivileges userPrivileges, String image, int phoneNumber, String nfcTag) {
        this.nick = nick;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.userPrivileges = userPrivileges;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.nfcTag = nfcTag;
        this.samples = 0;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    public int getTracksNumber() {
        return tracksNumber;
    }

    public void setTracksNumber(int tracksNumber) {
        this.tracksNumber = tracksNumber;
    }

    public float getCombustionAVG() {
        return combustionAVG;
    }

    public void setCombustionAVG(float combustionAVG) {
        this.combustionAVG = combustionAVG;
    }

    public int getSpeedAVG() {
        return speedAVG;
    }

    public void setSpeedAVG(int speedAVG) {
        this.speedAVG = speedAVG;
    }

    public int getRevolutionsAVG() {
        return revolutionsAVG;
    }

    public void setRevolutionsAVG(int revolutionsAVG) {
        this.revolutionsAVG = revolutionsAVG;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getNfcTag() {
        return nfcTag;
    }

    public void setNfcTag(String nfcTag) {
        this.nfcTag = nfcTag;
    }

    public float getEcoPointsAvg() {
        return ecoPointsAvg;
    }

    public void setEcoPointsAvg(float ecoPointsAvg) {
        this.ecoPointsAvg = ecoPointsAvg;
    }

    public long getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(long distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public void addDistanceTravelled(long distanceTravelled) {
        this.distanceTravelled += distanceTravelled;
    }
}
