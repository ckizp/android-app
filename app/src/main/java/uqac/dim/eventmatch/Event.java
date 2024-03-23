package uqac.dim.eventmatch;

import com.google.firebase.Timestamp;

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

    public void setDate_end(Timestamp date_end) {
        this.date_end = date_end;
    }

    public Timestamp getDate_start() {
        return date_start;
    }

    public void setDate_start(Timestamp date_start) {
        this.date_start = date_start;
    }

    public int getNb_participants() {
        return nb_participants;
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

}
