package teamc.ucc.ie.teamc.model;

/**
 * Created by zahra on 05/06/2017.
 */

public class Rpe {
    String eventId;

    public Rpe(String eventId, int score) {
        this.eventId = eventId;
        this.score = score;
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

    int score;
}
