package lk.ijse.etecmanagementsystem.util;

public class LoginUtil {
    private static String userName;
    private static int userId;

    public static void setUserName(String userName) {
        LoginUtil.userName = userName;
    }
    public static String getUserName() {
        return userName;
    }

    public  static void setUserId(int userId) {
        LoginUtil.userId = userId;
    }
    public  static int getUserId() {
        return userId;
    }
}
