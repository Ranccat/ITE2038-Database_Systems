import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalendarDB {
    static String URL = "jdbc:postgresql://127.0.0.1:5432/daniel";
    static String USER = "daniel";
    static String PW = "daniel";

    public static void Register(Calendar cal) {
        String SQL_INSERT = "INSERT INTO calendar (userID, userName, userPW, channel) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
            PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, cal.getID());
            ps.setString(2, cal.getName());
            ps.setString(3, cal.getPW());
            ps.setString(4, cal.getChannel());
            ps.executeUpdate();
            System.out.println("New Registration");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean IsValidUserID(String id)
    {
        String SQL_SELECT = "SELECT * FROM calendar";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("userID").equals(id)) {
                    return false;
                }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    public static void UpdateUserInfo(Calendar newCal, Calendar oldCal)
    {
        String SQL_UPDATE = "UPDATE calendar SET userID=?, userName=?, userPW=? WHERE userID=? AND userName=? AND userPW=?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
        PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, newCal.userID);
            ps.setString(2, newCal.userName);
            ps.setString(3, newCal.userPW);
            ps.setString(4, oldCal.userID);
            ps.setString(5, oldCal.userName);
            ps.setString(6, oldCal.userPW);
            ps.executeUpdate();
            System.out.println("Updated user info");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Calendar FindUser(String id, String pw)
    {
        String SQL_SELECT = "SELECT * FROM calendar";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("userID").equals(id) && rs.getString("userPW").equals(pw)) {
                    Calendar cal = new Calendar();
                    cal.setID(rs.getString("userID"));
                    cal.setName(rs.getString("userName"));
                    cal.setPW(rs.getString("userPW"));
                    cal.setChannel(rs.getString("channel"));
                    return cal;
                }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static List<Calendar> GetAllUsers()
    {
        List<Calendar> cals = new ArrayList<>();

        String SQL_SELECT = "SELECT * FROM calendar";

        try (Connection conn = DriverManager.getConnection(URL, USER, PW);
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Calendar cal = new Calendar();
                cal.setID(rs.getString("userID"));
                cal.setID(rs.getString("userName"));
                cal.setPW(rs.getString("userPW"));
                cal.setChannel(rs.getString("channel"));
                cals.add(cal);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cals;
    }
}
