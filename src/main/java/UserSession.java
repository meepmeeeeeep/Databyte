public class UserSession {
    private static String employee_name;
    private static String username;
    private static String role;


    // Set session info
    public static void setSession(String employee_name, String username, String role) {
        UserSession.employee_name = employee_name;
        UserSession.username = username;
        UserSession.role = role;
    }

    // Get employee name
    public static String getEmployeeName() {
        return employee_name;
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
