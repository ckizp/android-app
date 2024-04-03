package uqac.dim.eventmatch.models;

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
    //TODO : Finir les attributs de chaque user
    private String email;
    private String password;
    private String username;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
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
}