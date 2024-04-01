package uqac.dim.eventmatch.models;

/**
 * La classe {@link User} représente un utilisateur de l'application.
 *
 * @version 1.0 30 Mar 2024
 * @author Elouan Tailliez
 */
public class User {
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}