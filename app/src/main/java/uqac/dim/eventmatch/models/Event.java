package uqac.dim.eventmatch.models;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * La classe {@link Event} représente un événement d'EventMatch.
 *
 * @version 1.0 30 Mar 2024
 * @author Kyllian Hot
 */
public class Event {
    /* *************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private String name;
    private Timestamp endDate;
    private Timestamp startDate;
    private int participantsCount;
    private String tags;
    private List<DocumentReference> participants;
    private String imageDataUrl;
    private DocumentReference owner;
    public DocumentReference reference;


    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public Event() {

    }

    public Event(String name, Timestamp endDate, Timestamp startDate, int participantsCount, String tags, List<DocumentReference> participants, String imageDataUrl, DocumentReference owner) {
        this.name = name;
        this.endDate = endDate;
        this.startDate = startDate;
        this.participantsCount = participantsCount;
        this.tags = tags;
        this.participants = participants;
        this.imageDataUrl = imageDataUrl;
        this.owner = owner;
    }

    /* *************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    public String endDateToString() {
        return convertTimestampToString(endDate);
    }

    public void EndDateTabSet(int[] dateTime) {
        this.endDate = convertDateTimeToTimestamp(dateTime);
    }

    public String startDateToString() {
        return convertTimestampToString(startDate);
    }

    public void StartDateTabSet(int[] dateTime) {
        this.startDate = convertDateTimeToTimestamp(dateTime);
    }

    public static String convertTimestampToString(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sfd = new SimpleDateFormat("'le' dd/MM/yyyy 'à' HH:mm", Locale.CANADA);
        return sfd.format(date);
    }

    public static Timestamp convertDateTimeToTimestamp(int[] dateTime) {
        // Créer un objet Calendar et initialiser ses champs avec les valeurs du tableau
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, dateTime[0]);
        calendar.set(Calendar.MONTH, dateTime[1] - 1); // Les mois commencent à 0 (janvier est 0)
        calendar.set(Calendar.DAY_OF_MONTH, dateTime[2]);
        calendar.set(Calendar.HOUR_OF_DAY, dateTime[3]);
        calendar.set(Calendar.MINUTE, dateTime[4]);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Convertir le Calendar en timestamp
        long timestamp = calendar.getTimeInMillis();

        return new Timestamp(timestamp / 1000,0);
    }



    public interface UserListCallback {
        void onUserListReady(ArrayList<User> userList);
    }

    public void generateUserList(UserListCallback callback) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        ArrayList<User> result = new ArrayList<>();
        AtomicInteger documentsProcessed = new AtomicInteger(0);

        for (DocumentReference RefDocument : participants) {
            String documentPath = RefDocument.getPath();
            String[] parts = documentPath.split("/");
            String collectionName = parts[0]; // Le premier élément est le nom de la collection
            String documentId = parts[1];

            database.collection("users").document(documentId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d("DIM", "dans la requete"+documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            String email = documentSnapshot.getString("email");
                            String password = documentSnapshot.getString("password");
                            String username = documentSnapshot.getString("username");

                            User currentUser = new User(email, password, username, null, null, null, null, null);
                            result.add(currentUser);
                        } else {
                            Log.d(TAG, "No such document");
                        }

                        // Incrémentez le compteur de documents traités
                        int processedDocumentsCount = documentsProcessed.incrementAndGet();

                        // Si tous les documents ont été traités, appelez le rappel avec la liste finale
                        if (processedDocumentsCount == participants.size()) {
                            callback.onUserListReady(result);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "Error getting documents: ", e);

                        // Incrémentez le compteur de documents traités même en cas d'échec
                        int processedDocumentsCount = documentsProcessed.incrementAndGet();

                        // Si tous les documents ont été traités, appelez le rappel avec la liste finale
                        if (processedDocumentsCount == participants.size()) {
                            callback.onUserListReady(result);
                        }
                    });
        }
    }


    /* *************************************************************************
     *                                                                         *
     * GETTERS AND SETTERS for the fields                                      *
     *                                                                         *
     **************************************************************************/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<DocumentReference> getParticipants() {
        return participants;
    }

    public void setParticipants(List<DocumentReference> participants) {
        this.participants = participants;
    }

    public String getImageDataUrl() {
        return imageDataUrl;
    }

    public void setImageDataUrl(String path) {
        imageDataUrl = path;
    }

    public DocumentReference getOwner() {
        return owner;
    }

    public void setOwner(DocumentReference owner) {
        this.owner = owner;
    }
}