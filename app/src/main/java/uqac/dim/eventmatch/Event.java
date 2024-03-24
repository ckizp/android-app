package uqac.dim.eventmatch;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Event {

    private String name;
    private Timestamp date_end;
    private Timestamp date_start;
    private int nb_participants;
    private String type;

    public Event(){}
    public Event(String n, Timestamp end, Timestamp start, int nb, String t)
    {
        name = n;
        date_end = end;
        date_start = start;
        nb_participants = nb;
        type = t;
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

    public String  getDate_endString(){
        return convertTimestampToString(date_end);
    }

    public void setDate_end(Timestamp date_end) {
        this.date_end = date_end;
    }

    public Timestamp getDate_start() {
        return date_start;
    }

    public String getDate_startString()
    {
        return convertTimestampToString(date_start);
    }

    public void setDate_start(Timestamp date_start) {
        this.date_start = date_start;
    }

    public int getNb_participants() {
        return nb_participants;
    }

    public String getNb_paricipantsString()
    {
        return String.valueOf(nb_participants);
    }

    public void setNb_participants(int nb_participants) {
        this.nb_participants = nb_participants;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static String convertTimestampToString(Timestamp timestamp) {
        long seconds = timestamp.getSeconds();
        Date date = new Date(seconds);
        SimpleDateFormat sdf = new SimpleDateFormat("'le' dd/MM/yyyy 'à' HH:mm", Locale.CANADA);
        return sdf.format(date);
    }

    public static long convertDateTimeToTimestamp(int[] dateTime) {
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

        return timestamp;
    }

}
