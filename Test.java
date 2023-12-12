import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Test {
    public static void main(String[] args) {
        String startDate = "23-12-09";
        String startTime = "18:00";
        String endDate = "23-12-09";
        String endTime = "21:00";

        String startDateTime = "20" + startDate + " " + startTime + ":00";
        String endDateTime = "20" + endDate + " " + endTime + ":00";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp start = null;
        Timestamp end = null;
        try {
            start = new Timestamp(dateFormat.parse(startDateTime).getTime());
            end = new Timestamp(dateFormat.parse(endDateTime).getTime());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        System.out.println(start);
        System.out.println(end);
    }
}
