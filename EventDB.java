import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        String SQL_SELECT = "SELECT eventID, ownerID, name, description, startTime, endTime FROM event " +
                "WHERE EXTRACT(MONTH FROM startTime) = ? AND EXTRACT(YEAR FROM startTime) = ? " +
                "AND ? IN (SELECT userID FROM event_members WHERE event_members.eventID = event.eventID)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {

            ps.setInt(1, today.getMonthValue());
            ps.setInt(2, today.getYear());
            ps.setString(3, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int day = rs.getTimestamp("startTime").toLocalDateTime().getDayOfMonth();

                Event event = new Event();
                event.setEventID(rs.getInt("eventID"));
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

    public static void UpdateEventDetails(Event event, String name, String description)
    {
        String SQL_UPDATE = "UPDATE event SET name=?, description=? WHERE eventID=?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, event.eventID);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void DeletePassedEvents()
    {
        Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());

        // Delete events from event_members table
        String SQL_DELETE1 = "DELETE FROM event_members WHERE eventID IN " +
                "( SELECT eventID FROM event WHERE startTime <= ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE1)) {

            ps.setTimestamp(1, currentTimestamp);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Delete all the remaining events in event table
        String SQL_DELETE2 = "DELETE FROM event WHERE startTime <= ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_DELETE2)) {

            ps.setTimestamp(1, currentTimestamp);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Deleting event that the user own
    public static void DeleteMyEvent(int id)
    {
        String SQL_DELETE1 = "DELETE FROM event_members WHERE eventID = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_DELETE1)) {

            ps.setInt(1, id);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String SQL_DELETE2 = "DELETE FROM event WHERE eventID = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_DELETE2)){

            ps.setInt(1, id);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Deleting event of what the user doesn't own
    public static void DeleteOthersEvent(int eventID, String userID)
    {
        String SQL_DELETE = "DELETE FROM event_members WHERE eventID = ? AND userID = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){

            ps.setInt(1, eventID);
            ps.setString(2, userID);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Finding all events that include 'str' in name or description
    public static List<String> FindEvents(String str, String id)
    {
        List<String> events = new ArrayList<>();

        String SQL_SELECT = "SELECT * FROM event WHERE (" +
                "(name ILIKE '%' || ? || '%' OR description ILIKE '%' || ? || '%') " +
                "AND eventID IN ( SELECT eventID FROM event_members WHERE userID = ?))";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {

            ps.setString(1, str);
            ps.setString(2, str);
            ps.setString(3, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                events.add(rs.getString("name"));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return events;
    }
}
