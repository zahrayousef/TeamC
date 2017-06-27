package teamc.ucc.ie.teamc.model;

import android.support.annotation.NonNull;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import teamc.ucc.ie.teamc.R;



public class Event extends BaseCalendarEvent implements Serializable{


    public Event(String title, String description, String location, int color, @NonNull Calendar startTime, @NonNull Calendar endTime,String eventType,  boolean allDay) {
        super(title, description, location, color, startTime, endTime, allDay);
        this.title = title;
        this.description = description;
        this.start = startTime.getTime();
        this.end = endTime.getTime();
        this.location = location;
        this.setType(eventType);
    }

    public Event getBaseCalander(){

        Calendar startTime1 = Calendar.getInstance();
        startTime1.setTime(getStart());
        Calendar endTime1 = Calendar.getInstance();
        endTime1.setTime(getEnd());
        Event event = new Event(title, description, location, R.color.theme_primary, startTime1, endTime1, type, false);
        event.setId(getIds());
        return event;
    }

    public Event(Event event){
        super(event);


    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", start=" + start +
                ", End=" + end +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                '}';
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    private String description;
    private Date start;
    private Date end;
    private String type;
    private String location;

    public String getIds() {
        return ids;
    }

    public void setId(String id) {
        this.ids = id;
    }

    private String ids;

    @Override
    public CalendarEvent copy() {
        return getBaseCalander();
    }

}
