package proyecto.golfus.forat19;

import java.security.Key;

import Forat19.Players;
import Forat19.Users;

/**
 * Conjunto de variables globales y preferencias
 * @author Antonio Rodríguez Sirgado
 */
public class Global {

    public static final String CHECKED = "Checked";
    public static final String CREATE = "C";
    public static final String START = "S";
    public static final String END = "E";
    public static final String SHOW_CREATED_GAMES = "C";
    public static final String SHOW_STARTED_GAMES = "S";
    public static final String SHOW_ENDED_GAMES = "E";
    public static final String REJECT = "R";
    public static final String ACCEPT = "A";
    public static final String TAG="F19";
    public static final String YES = "Y";
    public static final int MAX_GAMERS = 4;

    public static final int TIME_OUT_LIMIT=6000;
    public static Users activeUser=null;
    public static Players activePlayer=null;
    public static Key serverKey;
    public static Boolean currentGame;

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PASSWORD = "password";

    // Tipos de usuarios
    public static final int TYPE_ADMIN_USER = 0;
    public static final int TYPE_NORMAL_USER = 1;
    public static final int TYPE_ADVANCED_USER = 2;

    // Tipos de recorrido
    public static final int TYPE_GOLF = 1;
    public static final int TYPE_PAR3 = 2;
    public static final int TYPE_PANDP = 3;

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
    public static final String LIST_PLAYER_DATA = "ListPlayerData";
    public static final String ADD_PLAYER = "AddPlayer";
    public static final String LIST_USER_PLAYER = "ListUserPlayer";
    public static final String GET_PLAYER = "GetPlayer" ;
    public static final String UPDATE_PLAYER = "UpdatePlayer";
    public static final String ADD_USER_RELATIONSHIP ="AddUserRelationship" ;
    public static final String LIST_USER_RELATIONSHIP_I ="ListUserRelationshipI" ;
    public static final String LIST_USER_RELATIONSHIP_O ="ListUserRelationshipO" ;
    public static final String DELETE_USER_RELATIONSHIP = "DeleteUserRelationship";
    public static final String ADD_GOLF_GAME = "AddGolfGame";
    public static final String LIST_GOLF_GAME_COURSE ="ListGolfGameCourse" ;
    public static final String LIST_GOLF_GAME_PLAYER = "ListGolfGamePlayer";
    public static final String DELETE_GOLF_GAME = "DeleteGolfGame";
    public static final String LIST_GOLF_GAME = "ListGolfGame";
    public static final String LIST_POSSIBLE_RELATIONSHIP = "ListPossibleRelationship";
    public static final String UPDATE_USER_RELATIONSHIP = "UpdateUserRelationship";
    public static final String UPDATE_GOLF_GAME = "UpdateGolfGame";
    public static final String GET_GOLF_GAME = "GetGolfGame";
    public static final String START_GOLF_GAME = "StartGolfGame";

    // Codigo recibidos
    public static final String OK = "Ok";


}
