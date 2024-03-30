package uqac.dim.eventmatch;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Event {
    private String name;
    private Timestamp endDate;
    private Timestamp startDate;
    private int participantsCount;
    private String type;

    public Event(){}
    public Event(String n, Timestamp end, Timestamp start, int nb, String t)
    {
        name = n;
        endDate = end;
        startDate = start;
        participantsCount = nb;
        type = t;
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

    public void setDate_end(int[] tab){
        this.endDate = convertDateTimeToTimestamp(tab);
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public String Date_startString()
    {
        return convertTimestampToString(startDate);
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
    public void setDate_start(int[] tab){
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
}
