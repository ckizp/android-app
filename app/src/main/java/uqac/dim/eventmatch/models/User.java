package uqac.dim.eventmatch.models;
import com.google.firebase.Timestamp;

/**
 * La classe {@link User} représente un utilisateur de l'application.
 *
 * @version 1.0 30 Mar 2024
 * @author Elouan Tailliez
 */
public class User {
    /* *************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/
    private String email;
    private String password;
    private String username;
    private String firstname;
    private String lastname;
    private Timestamp birthdate;
    private String adress;
    private String city;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public User(String email, String password, String username, String firstname, String lastname, Timestamp birthdate, String adress, String city) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.adress = adress;
        this.city = city;
    }

    /* *************************************************************************
     *                                                                         *
     * GETTERS AND SETTERS for the fields                                      *
     *                                                                         *
     **************************************************************************/

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Timestamp getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Timestamp birthdate) {
        this.birthdate = birthdate;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /* *************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/
}