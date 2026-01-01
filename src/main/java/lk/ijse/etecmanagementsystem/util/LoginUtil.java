package lk.ijse.etecmanagementsystem.util;

public class LoginUtil {
    private static String userName;
    private static int userId;
    private static String userRole;

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

    public static void setUserRole(String userRole) {
        LoginUtil.userRole = userRole;
    }
    public static String getUserRole() { return userRole; }
}
