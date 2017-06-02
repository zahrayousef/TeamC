package teamc.ucc.ie.teamc.model;

import java.util.Date;
import java.util.Timer;

/**
 * Created by zahra on 02/06/2017.
 */

public class Event {
    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", start=" + start +
                ", End=" + end +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public Event(String title, String desc, Date start, Date end, String type, String location) {
        this.title = title;
        this.desc = desc;
        this.start = start;
        this.end = end;
        this.type = type;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String title;
    private String desc;
    private Date start;
    private Date end;
    private String type;
    private String location;

}
