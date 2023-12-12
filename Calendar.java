public class Calendar {
    String userID;
    String userName;
    String userPW;
    String channel;

    public Calendar() {
        userID = "";
        userName = "";
        userPW = "";
        channel = "";
    }

    public String getID() { return userID; }
    public String getName() { return userName; }
    public String getPW() { return userPW; }
    public String getChannel() { return channel; }

    public void setID(String id) { userID = id; }
    public void setName(String name) { userName = name; }
    public void setPW(String pw) { userPW = pw; }
    public void setChannel(String ch) { channel = ch; }
}
