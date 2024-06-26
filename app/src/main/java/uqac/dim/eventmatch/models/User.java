package uqac.dim.eventmatch.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

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
    private String address;
    private String city;
    private ArrayList<DocumentReference> favorites;
    private boolean isAdmin;

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
        this.address = adress;
        this.city = city;
        this.favorites = new ArrayList<>();
        setIsAdmin(false);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ArrayList<DocumentReference> getFavorites() {
        return favorites;
    }
    public boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /* *************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/
}