import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class EventDB {
    static String URL = "jdbc:postgresql://127.0.0.1:5432/daniel";
    static String USER = "daniel";
    static String PW = "daniel";

    public static boolean IsValidDateTime(Timestamp start, Timestamp end, String id)
    {
        // TODO: event 멤버인 경우인 지도 확인하기
        // 시간이 겹치는 모든 event SELECT
        String SQL_SELECT = "SELECT event.* FROM event " +
                "JOIN event_members ON event.eventID = event_members.eventID " +
                "WHERE ((? < endTime AND ? > startTime) " +
                "OR (? > startTime AND ? < endTime)) " +
                "AND event_members.userID = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {

            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
            ps.setTimestamp(3, end);
            ps.setTimestamp(4, start);
            ps.setString(5, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return false;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    public static void AddNewEvent(Event event, List<String> members)
    {
        String SQL_INSERT = "INSERT INTO event (ownerID, name, description, startTime, endTime)" +
                "VALUES (?, ?, ?, ?, ?) RETURNING eventID";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, event.ownerID);
            ps.setString(2, event.name);
            ps.setString(3, event.description);
            ps.setTimestamp(4, event.startTime);
            ps.setTimestamp(5, event.endTime);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AddNewEventMembers(rs.getInt(1), members);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void AddNewEventMembers(int id, List<String> members)
    {
        String SQL_INSERT = "INSERT INTO event_members (eventID, userID) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            for (String s : members) {
                ps.setInt(1, id);
                ps.setString(2, s);
                ps.addBatch();
            }

            ps.executeBatch();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Map<Integer, List<Event>> GetEventsOfMonth(LocalDate today, String id)
    {
        Map<Integer, List<Event>> events = new HashMap<>();

        String SQL_SELECT = "SELECT eventID, ownerID, name, description, startTime, endTime FROM Event " +
                "WHERE EXTRACT(MONTH FROM startTime) = ? AND EXTRACT(YEAR FROM startTime) = ? " +
                "AND (? = ownerID OR ? IN (SELECT userID FROM EVENT_MEMBERS WHERE EVENT_MEMBERS.eventID = Event.eventID))";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {

            ps.setInt(1, today.getMonthValue());
            ps.setInt(2, today.getYear());
            ps.setString(3, id);
            ps.setString(4, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int day = rs.getTimestamp("startTime").toLocalDateTime().getDayOfMonth();
                Event event = new Event();
                event.setOwnerID(rs.getString("ownerID"));
                event.setName(rs.getString("name"));
                event.setDescription(rs.getString("description"));
                event.setStartTime(rs.getTimestamp("startTime"));
                event.setEndTime(rs.getTimestamp("endTime"));
                events.computeIfAbsent(day, k -> new ArrayList<>()).add(event);
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return events;
    }
}
