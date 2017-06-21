package teamc.ucc.ie.teamc.model;

/**
 * Created by zahra on 05/06/2017.
 */

public class Rpe {
    String eventId;
    int score;
    private int duration;
    private Event event;

    public Rpe(String eventId, int score, int duration) {
        this.eventId = eventId;
        this.score = score;
        this.duration = duration;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
