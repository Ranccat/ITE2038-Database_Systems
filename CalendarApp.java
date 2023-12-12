import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class CalendarApp {
    static Map<Integer, List<Event>> eventsByDay;

    public static void main(String[] args) {
        OpenApp();
    }

    private static void OpenApp()
    {
        // New Frame
        JFrame loginFrame = new JFrame("Log In");
        loginFrame.setSize(800, 600);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Title
        JButton titleButton = new JButton("Calendar");
        titleButton.setEnabled(false);
        loginFrame.add(titleButton, BorderLayout.NORTH);
        // Menu
        JPanel loginPanel = new JPanel(new GridLayout(1, 2));
        JButton loginButton = new JButton("Sign In");
        JButton signupButton = new JButton("Sign Up");
        loginPanel.add(loginButton);
        loginPanel.add(signupButton);
        loginFrame.add(loginPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            loginFrame.dispose();
            ShowLogIn();
        });
        signupButton.addActionListener(e -> {
            loginFrame.dispose();
            ShowSignUp();
        });

        loginFrame.setVisible(true);
    }

    private static void ShowMonth(LocalDate today)
    {
        // New Frame
        JFrame frame = new JFrame("Calendar");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Dates
        String[] daysOfWeek = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUNE", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"};
        LocalDate firstDayOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
        DayOfWeek startDay = firstDayOfMonth.getDayOfWeek();
        int daysInMonth = firstDayOfMonth.lengthOfMonth();
        String currentMonthStr = months[today.getMonthValue() - 1];

        // Month Panel
        JPanel monthPanel = new JPanel(new GridLayout(7, 7));
        frame.add(monthPanel, BorderLayout.CENTER);
        // Add Days Of The Week
        for (String dayOfWeek : daysOfWeek) {
            JButton dayButton = new JButton(dayOfWeek);
            dayButton.setEnabled(false);
            monthPanel.add(dayButton);
        }
        // Fill In Empty Buttons at top
        int blankCount = 35;
        if (startDay.getValue() != 7) {
            for (int i = 1; i <= startDay.getValue(); i++) {
                monthPanel.add(new JPanel());
                blankCount--;
            }
        }

        eventsByDay = Server.GetEventsByDay(today, Server.myCalendar.userID);

        // Add Days
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = new JButton(Integer.toString(day));

            List<Event> events = eventsByDay.getOrDefault(day, Collections.emptyList());
            int eventCount = events.size();
            dayButton.setToolTipText("Events: " + eventCount);

            monthPanel.add(dayButton);
            blankCount--;

            LocalDate ld = LocalDate.of(today.getYear(), today.getMonth(), day);
            dayButton.addActionListener(e -> ShowDay(ld));
        }
        // Fill in Empty Buttons at bottom
        while (blankCount > 0) {
            monthPanel.add(new JPanel());
            blankCount--;
        }

        // Top Menu: current month & move to another month
        JPanel topMenuPanel = new JPanel(new GridLayout(1, 3));
        frame.add(topMenuPanel, BorderLayout.NORTH);
        JButton prevMonthButton = new JButton("Prev Month");
        JButton nextMonthButton = new JButton("Next Month");
        JButton currentMonthButton = new JButton(today.getYear() + " " + currentMonthStr);
        topMenuPanel.add(prevMonthButton);
        topMenuPanel.add(currentMonthButton);
        topMenuPanel.add(nextMonthButton);
        currentMonthButton.setEnabled(false);

        // Bottom Menu: events
        JPanel bottomMenuPanel = new JPanel(new GridLayout(1, 4));
        frame.add(bottomMenuPanel, BorderLayout.SOUTH);
        JButton addEventButton = new JButton("Add Event");
        JButton searchEventButton = new JButton("Search Event");
        JButton checkScheduleButton = new JButton("Check Schedule");
        JButton updateUserButton = new JButton("Update User");
        bottomMenuPanel.add(addEventButton);
        bottomMenuPanel.add(searchEventButton);
        bottomMenuPanel.add(checkScheduleButton);
        bottomMenuPanel.add(updateUserButton);

        //// Listeners ////
        // Go To Prev Month
        prevMonthButton.addActionListener(e -> {
            LocalDate prevDay = today.minusMonths(1);
            frame.dispose();
            ShowMonth(prevDay);
        });
        // Go To Next Month
        nextMonthButton.addActionListener(e -> {
            LocalDate nextDay = today.plusMonths(1);
            frame.dispose();
            ShowMonth(nextDay);
        });
        // Add Event
        addEventButton.addActionListener(e -> ShowAddEvent());
        // Modify Event
        searchEventButton.addActionListener(e -> ShowSearchEvent());
        // Delete Event
        checkScheduleButton.addActionListener(e -> ShowCheckSchedule());
        // Show Events
        updateUserButton.addActionListener(e -> ShowUpdateUser());

        frame.setVisible(true);

        System.out.println("이번 달 개수: " + eventsByDay.size());
    }

    private static void ShowWeek(LocalDate today)
    {

    }

    private static void ShowDay(LocalDate today)
    {
        JFrame frame = new JFrame(String.valueOf(today));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(400, 600);

        List<Event> events = eventsByDay.get(today.getDayOfMonth());

        if (!(events == null)) {
            String[] eventArray = events.toArray(new String[0]);
            JList<String> stringJList = new JList<>(eventArray);
            JScrollPane scrollPane = new JScrollPane(stringJList);
            frame.add(scrollPane, BorderLayout.CENTER);
        }
        else {
            JButton textButton = new JButton("No Event");
            textButton.setEnabled(false);
            frame.add(textButton, BorderLayout.CENTER);
        }

        frame.setVisible(true);
    }

    private static void ShowUpdateUser()
    {
        // New Frame
        JFrame frame = new JFrame("Update User");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2));
        JLabel newNameLabel = new JLabel("New Name:");
        JTextField newNameField = new JTextField(20);
        JLabel newIDLabel = new JLabel("New ID:");
        JTextField newIDField = new JTextField(20);
        JLabel newPWLabel = new JLabel("New PW:");
        JTextField newPWField = new JTextField(20);

        formPanel.add(newNameLabel);
        formPanel.add(newNameField);
        formPanel.add(newIDLabel);
        formPanel.add(newIDField);
        formPanel.add(newPWLabel);
        formPanel.add(newPWField);

        JButton checkButton = new JButton("Apply Change");

        frame.add(formPanel, BorderLayout.CENTER);
        frame.add(checkButton, BorderLayout.SOUTH);

        checkButton.addActionListener(e -> {
            // Get Fields
            String name = newNameField.getText();
            String id = newIDField.getText();
            String pw = newPWField.getText();

            boolean check = Server.UserUpdate(name, id, pw);

            if (check) {
                JFrame okFrame = new JFrame();
                okFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                okFrame.setSize(300, 150);

                JButton textButton = new JButton("User Info Changed");
                textButton.setEnabled(false);
                okFrame.add(textButton, BorderLayout.CENTER);

                JButton okButton = new JButton("OK");
                okButton.setEnabled(true);
                okFrame.add(okButton, BorderLayout.SOUTH);

                okFrame.setVisible(true);

                okButton.addActionListener(okE -> {
                    okFrame.dispose();
                    frame.dispose();
                });
            }
            else {
                JFrame noFrame = new JFrame();
                noFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                noFrame.setSize(300, 150);

                JButton textButton = new JButton("Update Failed: id is already in use");
                textButton.setEnabled(false);
                noFrame.add(textButton, BorderLayout.CENTER);

                JButton noButton = new JButton("Retry");
                noButton.setEnabled(true);
                noFrame.add(noButton, BorderLayout.SOUTH);

                noFrame.setVisible(true);

                noButton.addActionListener(noE -> {
                    noFrame.dispose();

                    newIDField.setText("");
                    newNameField.setText("");
                    newPWField.setText("");
                });
            }
        });

        frame.setVisible(true);
    }

    private static void ShowAddEvent()
    {
        JFrame frame = new JFrame("Add Event");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(8, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(50);

        JLabel startDateLabel = new JLabel("Start Date(YY-MM-DD):");
        JTextField startDateField = new JTextField(10);
        JLabel startTimeLabel = new JLabel("Start Time(HH:MM)");
        JTextField startTimeField = new JTextField(10);
        JLabel endDateLabel = new JLabel("End Date(YY-MM-DD):");
        JTextField endDateField = new JTextField(10);
        JLabel endTimeLabel = new JLabel("End Time(HH:MM)");
        JTextField endTimeField = new JTextField(10);

        JButton addMemberButton = new JButton("Add Member");
        JButton showMembersButton = new JButton("Show Members");

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        formPanel.add(startDateLabel);
        formPanel.add(startDateField);
        formPanel.add(startTimeLabel);
        formPanel.add(startTimeField);
        formPanel.add(endDateLabel);
        formPanel.add(endDateField);
        formPanel.add(endTimeLabel);
        formPanel.add(endTimeField);
        formPanel.add(addMemberButton);
        formPanel.add(showMembersButton);

        JButton confirmButton = new JButton("Confirm");

        frame.add(formPanel, BorderLayout.CENTER);
        frame.add(confirmButton, BorderLayout.SOUTH);

        addMemberButton.addActionListener(e -> {
            // TODO
        });

        showMembersButton.addActionListener(e -> {
            // TODO
        });

        confirmButton.addActionListener(e -> {
            String startDate = startDateField.getText();
            String startTime = startTimeField.getText();
            String endDate = endDateField.getText();
            String endTime = endTimeField.getText();

            String startDateTime = "20" + startDate + " " + startTime + ":00";
            String endDateTime = "20" + endDate + " " + endTime + ":00";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp start = null;
            Timestamp end = null;
            try {
                start = new Timestamp(dateFormat.parse(startDateTime).getTime());
                end = new Timestamp(dateFormat.parse(endDateTime).getTime());
            }
            catch (Exception exception) {
                System.out.println(exception.getMessage());
            }

            String name = nameField.getText();
            String description = descriptionField.getText();

            List<String> members = new ArrayList<>();
            // TODO members 추가
            members.add(Server.myCalendar.userID);

            boolean check = Server.AddEvent(start, end, name, description, members);

            if(check) {
                JFrame okFrame = new JFrame();
                okFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                okFrame.setSize(300, 150);

                JButton textButton = new JButton("Event Added");
                textButton.setEnabled(false);
                okFrame.add(textButton, BorderLayout.CENTER);

                JButton okButton = new JButton("Return To Calendar");
                okButton.setEnabled(true);
                okFrame.add(okButton, BorderLayout.SOUTH);

                okFrame.setVisible(true);

                okButton.addActionListener(okE -> {
                    okFrame.dispose();
                    frame.dispose();
                    ShowMonth(LocalDate.now());
                });
            }
            else {
                JFrame noFrame = new JFrame();
                noFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                noFrame.setSize(300, 150);

                JButton textButton = new JButton("Time already is use");
                textButton.setEnabled(false);
                noFrame.add(textButton, BorderLayout.CENTER);

                JButton noButton = new JButton("Retry");
                noButton.setEnabled(true);
                noFrame.add(noButton, BorderLayout.SOUTH);

                noFrame.setVisible(true);

                noButton.addActionListener(noE -> {
                    noFrame.dispose();
                });
            }
        });

        frame.setVisible(true);
    }

    private static void ShowSearchEvent()
    {

    }

    private static void ShowCheckSchedule()
    {
        JFrame frame = new JFrame("Check Schedule");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2));

        JLabel startDateLabel = new JLabel("Start Date(YY-MM-DD):");
        JTextField startDateField = new JTextField(10);
        JLabel startTimeLabel = new JLabel("Start Time(HH:MM)");
        JTextField startTimeField = new JTextField(10);
        JLabel endDateLabel = new JLabel("End Date(YY-MM-DD):");
        JTextField endDateField = new JTextField(10);
        JLabel endTimeLabel = new JLabel("End Time(HH:MM)");
        JTextField endTimeField = new JTextField(10);

        formPanel.add(startDateLabel);
        formPanel.add(startDateField);
        formPanel.add(startTimeLabel);
        formPanel.add(startTimeField);
        formPanel.add(endDateLabel);
        formPanel.add(endDateField);
        formPanel.add(endTimeLabel);
        formPanel.add(endTimeField);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(1, 2));

        JButton checkButton = new JButton("Check");
        JButton closeButton = new JButton("Close");
        menuPanel.add(checkButton);
        menuPanel.add(closeButton);

        frame.add(formPanel, BorderLayout.CENTER);
        frame.add(menuPanel, BorderLayout.SOUTH);

        checkButton.addActionListener(e -> {
            JFrame listFrame = new JFrame();
            listFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            listFrame.setSize(400, 200);

            // 입력한 시간 Parsing
            String startDate = startDateField.getText();
            String startTime = startTimeField.getText();
            String endDate = endDateField.getText();
            String endTime = endTimeField.getText();

            String startDateTime = "20" + startDate + " " + startTime + ":00";
            String endDateTime = "20" + endDate + " " + endTime + ":00";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp start = null;
            Timestamp end = null;
            try {
                start = new Timestamp(dateFormat.parse(startDateTime).getTime());
                end = new Timestamp(dateFormat.parse(endDateTime).getTime());
            }
            catch (Exception exception) {
                System.out.println(exception.getMessage());
            }

            List<String> availableUsers = Server.GetAvailableMembers(start, end);
            if (!availableUsers.isEmpty()) {
                String[] userArray = availableUsers.toArray(new String[0]);
                JList<String> stringJList = new JList<>(userArray);
                JScrollPane scrollPane = new JScrollPane(stringJList);
                listFrame.add(scrollPane, BorderLayout.CENTER);
            }
            else {
                JButton textButton = new JButton("No Available Member");
                textButton.setEnabled(false);
                listFrame.add(textButton, BorderLayout.CENTER);
            }

            JButton okButton = new JButton("OK");
            listFrame.add(okButton, BorderLayout.SOUTH);

            okButton.addActionListener(okE -> listFrame.dispose());

            listFrame.setVisible(true);
        });

        closeButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private static void ShowLogIn()
    {
        JFrame frame = new JFrame("Sign In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(20);
        JLabel pwLabel = new JLabel("Password:");
        JPasswordField pwField = new JPasswordField(20);
        JButton signInButton = new JButton("Log In");

        panel.add(idLabel);
        panel.add(idField);
        panel.add(pwLabel);
        panel.add(pwField);
        panel.add(new JLabel(""));
        panel.add(signInButton);
        frame.add(panel);

        signInButton.addActionListener(e -> {
            String id = idField.getText();
            char[] pwChars = pwField.getPassword();
            String pw = new String(pwChars);

            boolean check = Server.UserLogin(id, pw);
            pwField.setText("");

            if (check) {
                JFrame okFrame = new JFrame();
                okFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                okFrame.setSize(300, 150);

                JButton textButton = new JButton("Login Success");
                textButton.setEnabled(false);
                okFrame.add(textButton, BorderLayout.CENTER);

                JButton okButton = new JButton("Start App");
                okButton.setEnabled(true);
                okFrame.add(okButton, BorderLayout.SOUTH);

                okFrame.setVisible(true);

                okButton.addActionListener(okE -> {
                    okFrame.dispose();
                    frame.dispose();
                    ShowMonth(LocalDate.now());
                });
            }
            else {
                JFrame noFrame = new JFrame();
                noFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                noFrame.setSize(300, 150);

                JButton textButton = new JButton("Login Failed: Invalid id or password");
                textButton.setEnabled(false);
                noFrame.add(textButton, BorderLayout.CENTER);

                JButton noButton = new JButton("Retry");
                noButton.setEnabled(true);
                noFrame.add(noButton, BorderLayout.SOUTH);

                noFrame.setVisible(true);

                noButton.addActionListener(noE -> {
                    noFrame.dispose();

                    idField.setText("");
                });
            }
        });

        frame.setVisible(true);
    }

    private static void ShowSignUp()
    {
        JFrame frame = new JFrame("Sign Up");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel nameLabel = new JLabel("New Name:");
        JTextField nameField = new JTextField(20);
        JLabel idLabel = new JLabel("New ID:");
        JTextField idField = new JTextField(20);
        JLabel pwLabel = new JLabel("New Password:");
        JPasswordField pwField = new JPasswordField(20);
        JButton signUpButton = new JButton("Sign Up");

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(idLabel);
        panel.add(idField);
        panel.add(pwLabel);
        panel.add(pwField);
        panel.add(new JLabel(""));
        panel.add(signUpButton);
        frame.add(panel);

        signUpButton.addActionListener(e -> {
            String name = nameField.getText();
            String id = idField.getText();
            char[] pwChars = pwField.getPassword();
            String pw = new String(pwChars);

            boolean check = Server.UserRegistration(id, name, pw);
            pwField.setText("");

            if (check) {
                JFrame okFrame = new JFrame();
                okFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                okFrame.setSize(300, 150);

                JButton textButton = new JButton("Registration Success");
                textButton.setEnabled(false);
                okFrame.add(textButton, BorderLayout.CENTER);

                JButton okButton = new JButton("Confirm");
                okButton.setEnabled(true);
                okFrame.add(okButton, BorderLayout.SOUTH);

                okFrame.setVisible(true);

                okButton.addActionListener(okE -> {
                    okFrame.dispose();
                    frame.dispose();
                    OpenApp();
                });
            }
            else {
                JFrame noFrame = new JFrame();
                noFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                noFrame.setSize(300, 150);

                JButton textButton = new JButton("Registration Failed: ID already exists");
                textButton.setEnabled(false);
                noFrame.add(textButton, BorderLayout.CENTER);

                JButton noButton = new JButton("Retry");
                noButton.setEnabled(true);
                noFrame.add(noButton, BorderLayout.SOUTH);

                noFrame.setVisible(true);

                noButton.addActionListener(noE -> {
                    noFrame.dispose();

                    idField.setText("");
                });
            }
        });

        frame.setVisible(true);
    }
}
