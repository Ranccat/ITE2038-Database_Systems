import java.sql.Timestamp;

public class Event {
    int eventID;
    String ownerID;
    String name;
    String description;
    Timestamp startTime;
    Timestamp endTime;

    public int getEventID() { return eventID; }
    public String getOwnerID() { return ownerID; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Timestamp getStartTime() { return startTime; }
    public Timestamp getEndTime() { return endTime; }

    public void setEventID(int value) { eventID = value; }
    public void setOwnerID(String value) { ownerID = value; }
    public void setName(String value) { name = value; }
    public void setDescription(String value) { description = value; }
    public void setStartTime(Timestamp value) { startTime = value; }
    public void setEndTime(Timestamp value) { endTime = value; }
}
