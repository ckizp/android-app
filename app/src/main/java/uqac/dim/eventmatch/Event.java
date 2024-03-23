package uqac.dim.eventmatch;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

public class Event {

    private String name;
    private String date_end;
    private String date_start;
    private int nb_participants;
    private String type;

    public Event(){}
    public Event(String n,String end,String start, int nb, String t)
    {
        name = n;
        date_end = end;
        date_start = start;
        nb_participants = nb;
        type = t;
    }

    public String getName(){
        return name;
    }

    public String getDate_start(){
        return date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public int getNb_participants() {
        return nb_participants;
    }

    public String getType() {
        return type;
    }
}
