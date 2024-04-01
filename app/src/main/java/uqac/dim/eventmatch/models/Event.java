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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * La classe {@link Event} représente un événement d'EventMatch.
 *
 * @version 1.0 30 Mar 2024
 * @author Kyllian Hot
 */
public class Event {
    private String name;
    private Timestamp endDate;
    private Timestamp startDate;
    private int participantsCount;
    private String tags;
    private byte[] imageData;
    private List<DocumentReference> participants;

    public Event() {

    }

    public Event(String name, Timestamp endDate, Timestamp startDate, int participantsCount, String tags, List<DocumentReference> participants, byte[] imageData) {
        this.name = name;
        this.endDate = endDate;
        this.startDate = startDate;
        this.participantsCount = participantsCount;
        this.tags = tags;
        this.participants = participants;
        this.imageData = imageData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDate_end() {
        return date_end;
    }

    public String Date_endString(){
        return convertTimestampToString(date_end);
    }

    public void setDate_end(Timestamp date_end) {
        this.date_end = date_end;
    }

    public void TabsetDate_end(int[] tab){
        this.date_end = convertDateTimeToTimestamp(tab);
    }

    public Timestamp getDate_start() {
        return date_start;
    }

    public String Date_startString()
    {
        return convertTimestampToString(date_start);
    }

    public void TabsetDate_start(int[] tab) {
        this.date_start = convertDateTimeToTimestamp(tab);
    }

    public int getNb_participants() {
        return nb_participants;
    }

    public String Nb_paricipantsString()
    {
        return String.valueOf(nb_participants);
    }

    public void setNb_participants(int nb_participants) {
        this.nb_participants = nb_participants;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public List<DocumentReference> getParticipants() {
        return participants;
    }

    public void setParticipants(List<DocumentReference> p){
        participants = p;
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
        Timestamp res = new Timestamp(timestamp/1000,0);

        return res;
    }


    public interface ParticipantsNameCallback {
        void onParticipantsNameReady(List<String> participantNames);
    }

    void participants_name(FirebaseFirestore db, ParticipantsNameCallback callback) {
        final List<String> res = new ArrayList<>() ;
        res.add("José");
        final int[] counter = {participants.size()};

        for (DocumentReference document : participants) {
            document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            res.add(document.getString("email"));
                        }
                    }
                    // Décrémenter le compteur et vérifier si toutes les requêtes sont terminées
                    if (--counter[0] == 0) {
                        // Toutes les requêtes sont terminées, appeler le callback avec la liste des noms des participants
                        callback.onParticipantsNameReady(res);
                    }
                }
            });
        }
    }

    /*
    List<String> participants_name(FirebaseFirestore db){
        List<String> res = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(participants.size());
        res.add("José");
        for (DocumentReference document: participants) {
            document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            res.add(document.getString("email"));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                    latch.countDown();
                }
            });
            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }*/

    List<User> userlist(){
        List<User> res = new ArrayList<User>();
        res.add(new User("testharcodé@gmail.com","zbiestcequecamarche"));

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        for (DocumentReference RefDocument : participants) {
            RefDocument.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                Log.d("DIM", "DocumentSnapshot data: " + document.getData());
                                String email = document.getString("email");
                                String password = document.getString("password");

                                User currentuser = new User(email,password);
                                res.add(currentuser);
                            }
                        }
                    }
            });

        }


        return res;
    }

}
