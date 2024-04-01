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

    private String email;
    private String password;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public User(String email, String password) {
        this.email = email;
        this.password = password;
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
}