package proyecto.golfus.forat19.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Course_Types;
import Forat19.Golf_Game_Players;
import Forat19.Golf_Games;
import Forat19.Installations;
import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;


/**
 * Fragment para mostrar la informacion de un partido
 *
 * @author Antonio Rodríguez Sirgado
 */
public class Game extends Fragment implements Observer {

    private ImageButton start;
    private Golf_Games game;
    private Message request;
    private int idGameReceived;
    private TextView date, time, typeGame, installation, course, numberHoles, typeCourse;
    private ListView listPlayersView;
    private List<String> listNamePlayers = new ArrayList<>();
    private ArrayAdapter<String> adapterPlayersList;

    public Game() {
        // Required empty public constructor
    }

    public Game(int id_golf_game) {
        idGameReceived = id_golf_game;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_game, container, false);
        date = view.findViewById(R.id.txt_game_date);
        time = view.findViewById(R.id.txt_game_time);
        typeGame = view.findViewById(R.id.txt_game_gameType);
        installation = view.findViewById(R.id.txt_game_installation);
        course = view.findViewById(R.id.txt_game_course);
        typeCourse = view.findViewById(R.id.txt_game_courseType);
        numberHoles = view.findViewById(R.id.txt_game_numberHoles);
        listPlayersView = view.findViewById(R.id.lv_game_listPlayers);
        start = view.findViewById(R.id.btn_game_start);


        getGame(idGameReceived);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Global.currentGame) {
                    startGame();
                } else {
                    Utils.showSnack(v, "Ya hay un partido empezado", Snackbar.LENGTH_LONG);
                }
            }
        });

        return view;

    }

    @Override
    public void update(Observable o, Object arg) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (arg instanceof Reply) {
                    Utils.showSnack(getView(), ((Reply) arg).getTypeError(), Snackbar.LENGTH_LONG);
                    Fragment fragment = new Principal();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

                } else if (arg instanceof Message) {
                    request = (Message) arg;
                    String command = request.getCommand();
                    String parameter = request.getParameters();

                    switch (command) {
                        case Global.GET_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {

                                game = (Golf_Games) request.getObject();

                                Log.d(Global.TAG, "Id instalacion" + game.getGolf_course().getId_installation());
                                getInstallation();
                                getGolfCourse();

                                time.setText(game.getGame_hour());
                                date.setText(Utils.changeDateFormat(game.getGame_date()));
                                typeGame.setText(game.getGolf_game_type().getGolf_game_type());
                                course.setText(game.getGolf_course().getGolf_course());
                                numberHoles.setText(Integer.toString(game.getGolf_course().getHoles()));

                                for (Golf_Game_Players ggp : game.getGolf_game_players()) {
                                    Log.d(Global.TAG, ggp.getPlayer().getUser().getUsername());
                                    listNamePlayers.add(ggp.getPlayer().getUser().getName() + " - " + ggp.getPlayer().getUser().getUsername());
                                }

                                adapterPlayersList = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listNamePlayers);
                                listPlayersView.setAdapter(adapterPlayersList);

                                Log.d(Global.TAG, game.getGame_date() + " - " + game.getGame_hour());
                                Log.d(Global.TAG, game.getStatus());
                                Log.d(Global.TAG, game.getGolf_course().getGolf_course());
                                Log.d(Global.TAG, game.getGolf_game_players().get(0).getPlayer().getUser().getName());

                            }
                            break;

                        case Global.START_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                Global.start = Instant.now();
                                Utils.showSnack(getView(), getString(R.string.The_game_has_started), Snackbar.LENGTH_LONG);
                                Fragment fragment = new Principal();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "principal").commit();
                            }
                            break;
                        case Global.GET_INSTALLATION:

                            if (parameter.equals(Global.OK)) {
                                Installations inst = (Installations) request.getObject();
                                installation.setText(inst.getInstallation());

                            }
                            break;

                        case Global.GET_GOLF_COURSE_TYPE:
                            if (parameter.equals(Global.OK)) {
                                Golf_Course_Types golfCourseTypes = (Golf_Course_Types) request.getObject();
                                typeCourse.setText(golfCourseTypes.getGolf_course_type());
                            }
                            break;
                    }
                }
            }
        });
    }

    /**
     * <b>Envia el mensaje para cargar el partido seleccionado</b><br>
     * Mensaje = (token¬device, GetGolfGame, id golf game, null)
     *
     * @param idGame id del partido a descargar
     * @author Antonio Rodríguez Sirgado
     */
    private void getGame(int idGame) {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.GET_GOLF_GAME, Integer.toString(idGame), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia el mensaje para empezar el partido seleccionado</b><br>
     * Mensaje = (token¬device, GetGolfGame, id golf game, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    private void startGame() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.START_GOLF_GAME, Integer.toString(game.getId_golf_game()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Pide al servidor informacion de la instalacion</b>
     * Mensaje = (token¬device, GetInstallation, id instalacion, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void getInstallation() {

        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.GET_INSTALLATION, Integer.toString(game.getGolf_course().getId_installation()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Pide al servidor informacion del recorrido</b>
     * Mensaje = (token¬device, GetGolfCourse, id recorrido, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void getGolfCourse() {

        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.GET_GOLF_COURSE_TYPE, Integer.toString(game.getGolf_course().getId_golf_course_type()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }


}