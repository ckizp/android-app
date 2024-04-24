package uqac.dim.eventmatch.models;

import com.google.firebase.firestore.DocumentReference;

/**
 * La classe {@link Feedback} représente un feedback d'un utilisateur.
 *
 * @version 1.0 23 Apr 2024
 */
public class Feedback {
    /* *************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private DocumentReference user;
    private String feedback;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public Feedback() {

    }

    public Feedback(DocumentReference user, String feedback) {
        this.user = user;
        this.feedback = feedback;
    }

    /* *************************************************************************
     *                                                                         *
     * GETTERS AND SETTERS for the fields                                      *
     *                                                                         *
     **************************************************************************/

    public DocumentReference getUser() {
        return user;
    }

    public void setUser(DocumentReference user) {
        this.user = user;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}