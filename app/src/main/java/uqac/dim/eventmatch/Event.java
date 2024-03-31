package uqac.dim.eventmatch;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Event {
    private String name;
    private Timestamp endDate;
    private Timestamp startDate;
    private int participantsCount;
    private String type;
    private List<DocumentReference> participants;

    public Event(){}
    public Event(String n, Timestamp end, Timestamp start, int nb, String t,List<DocumentReference> l)
    {
        name = n;
        endDate = end;
        startDate = start;
        participantsCount = nb;
        type = t;
        participants = l;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public String Date_endString(){
        return convertTimestampToString(endDate);
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public void TabsetDate_end(int[] tab){
        this.endDate = convertDateTimeToTimestamp(tab);
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public String Date_startString()
    {
        return convertTimestampToString(startDate);
    }

    public void TabsetDate_start(int[] tab) {
        this.startDate = convertDateTimeToTimestamp(tab);
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public String Nb_paricipantsString()
    {
        return String.valueOf(participantsCount);
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
