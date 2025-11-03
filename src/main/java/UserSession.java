public class UserSession {
    private static String username;
    private static String role;

    // Set session info
    public static void setSession(String username, String role) {
        UserSession.username = username;
        UserSession.role = role;
    }

    // Get username
    public static String getUsername() {
        return username;
    }

    // Get role
    public static String getRole() {
        return role;
    }

}
