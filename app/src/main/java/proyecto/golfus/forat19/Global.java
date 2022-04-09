package proyecto.golfus.forat19;

public class Global {
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PASSWORD = "password";

    public static final int TYPE_ADMIN_USER = 0;
    public static final int TYPE_NORMAL_USER = 1;

    public static final String PREF_ACTIVE_USER="activeUser";
    public static final String PREF_OPEN_KEEP_SESSION_OPEN="keepSessionOpen";
    public static final String PREF_TYPE_USER="typeUser";
    public static final String PREF_ACTIVE_TOKEN="activeToken";

    // Codigos para enviar
    public static final String LOGIN="Login";
    public static final String VALIDATE_TOKEN="ValidateToken";
    public static final String LOGOUT="Logout";
    public static final String ADD_USER="AddUser";
    public static final String UPDATE_USER="UpdateUser";
    public static final String DELETE_USER="DeleteUser";

    // Codigo recibidos
    public static final String ERROR="Error";
    public static final String USER_ADDED="User Added";
    public static final String INCORRECT_DATA="Incorrect Data";
    public static final String INVALID_TOKEN="Invalid token";
    public static final String TOKEN_VALIDATED="Token validated";
    public static final String USER_UPDATED="User updated";
    public static final String TOKEN = "Token";


}
