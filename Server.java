import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.HashMap;

public class Server {
    static Calendar myCalendar;

    public static boolean UserRegistration(String id, String name, String pw)
    {
        boolean check = CalendarDB.IsValidUserID(id);
        if (check) {
            myCalendar = new Calendar();
            myCalendar.setID(id);
            myCalendar.setName(name);
            myCalendar.setPW(pw);
            myCalendar.setChannel("In-App");
            CalendarDB.Register(myCalendar);
            return true;
        }
        return false;
    }

    public static boolean UserLogin(String id, String pw)
    {
        Calendar cal = CalendarDB.FindUser(id, pw);
        if (cal == null) {
            return false;
        }
        myCalendar = cal;
        return true;
    }

    public static boolean UserUpdate(String name, String id, String pw)
    {
        boolean check = CalendarDB.IsValidUserID(id);
        if (check) {
            Calendar oldCal = new Calendar();
            oldCal.setName(myCalendar.getName());
            oldCal.setID(myCalendar.getID());
            oldCal.setPW(myCalendar.getPW());

            myCalendar.setName(name);
            myCalendar.setID(id);
            myCalendar.setPW(pw);

            CalendarDB.UpdateUserInfo(myCalendar, oldCal);

            return true;
        }

        return false;
    }

    public static boolean AddEvent(Timestamp start, Timestamp end, String name, String description, List<String> members)
    {
        boolean check = EventDB.IsValidDateTime(start, end, myCalendar.userID);

        if (check) {
            Event event = new Event();
            event.setOwnerID(myCalendar.userID);
            event.setName(name);
            event.setDescription(description);
            event.setStartTime(start);
            event.setEndTime(end);
            EventDB.AddNewEvent(event, members);

            return true;
        }

        return false;
    }

    public static List<String> GetAvailableMembers(Timestamp start, Timestamp end)
    {
        List<String> users = new ArrayList<>();
        List<Calendar> cals = CalendarDB.GetAllUsers();

        for (Calendar cal : cals) {
            if (EventDB.IsValidDateTime(start, end, cal.userID)) {
                users.add(cal.userID);
            }
        }

        return users;
    }

    public static Map<Integer, List<Event>> GetEventsByDay(LocalDate today, String id)
    {
        return EventDB.GetEventsOfMonth(today, id);
    }
}
