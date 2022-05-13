package proyecto.golfus.forat19;

/**
 * Conjunto de variables globales y preferencias
 * @author Antonio Rodríguez Sirgado
 */
public class Global {

    public static final int TIME_OUT_LIMIT=3000;
    public static final String TAG="F19";
    public static final String YES = "Y";
    public static final int MAX_GAMERS = 4;

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PASSWORD = "password";

    // Tipos de usuarios
    public static final int TYPE_ADMIN_USER = 0;
    public static final int TYPE_NORMAL_USER = 1;
    public static final int TYPE_ADVANCED_USER = 2;

    // Tipos de recorrido
    public static final int TYPE_GOLF = 0;
    public static final int TYPE_PANDP = 1;
    public static final int TYPE_PAR3 = 2;

    // Preferences
    public static final String PREF_ACTIVE_USER="activeUser";
    public static final String PREF_OPEN_KEEP_SESSION_OPEN="keepSessionOpen";
    public static final String PREF_TYPE_USER="typeUser";
    public static final String PREF_ACTIVE_TOKEN="activeToken";
    public static final String PREF_ACTIVE_ID = "activeID";

    // Codigos para enviar
    public static final String LOGIN="Login";
    public static final String VALIDATE_TOKEN="ValidateToken";
    public static final String LOGOUT="Logout";
    public static final String ADD_USER="AddUser";
    public static final String UPDATE_USER="UpdateUser";
    public static final String DELETE_USER="DeleteUser";
    public static final String GET_USER = "GetUser";
    public static final String LIST_ALL_USERS = "ListUser*";
    public static final String LIST_ACTIVE_USERS = "ListUserA";
    public static final String LIST_INACTIVE_USERS = "ListUserI";
    public static final String LIST_USER_TYPES = "ListUserType";
    public static final String LIST_INSTALLATIONS = "ListInstallation";
    public static final String LIST_GOLF_COURSES = "ListGolfCourse";
    public static final String GET_GOLF_COURSE_TYPE ="GetGolfCourseType" ;
    public static final String LIST_GOLF_COURSES_TYPE = "ListGolfCourseType";
    public static final String ADD_GOLF_COURSE = "AddGolfCourse";
    public static final String UPDATE_GOLF_COURSE ="UpdateGolfCourse";
    public static final String UPDATE_GOLF_COURSE_HOLE = "UpdateGolfCourseHole" ;
    public static final String LIST_GOLF_GAME_TYPE = "ListGolfGameType";
    public static final String LIST_PLAYER_TYPE ="ListPlayerType" ;

    // Codigo recibidos
    public static final String OK = "Ok";


}
