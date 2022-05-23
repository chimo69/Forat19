package proyecto.golfus.forat19.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Game_Players;
import Forat19.Golf_Games;
import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.adapterList.AdapterGameList;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;



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
                            if (parameter.equals(Global.OK)){

                                game = (Golf_Games) request.getObject();

                                time.setText(game.getGame_hour());
                                date.setText(Utils.changeDateFormat(game.getGame_date()));
                                typeGame.setText(game.getGolf_game_type().getGolf_game_type());
                                //installation.setText(game.getGolf_course().getInstallation().getInstallation());
                                course.setText(game.getGolf_course().getGolf_course());
                                //typeCourse.setText(game.getGolf_course().getGolf_course_type().getGolf_course_type());
                                numberHoles.setText(Integer.toString(game.getGolf_course().getHoles()));


                                for (Golf_Game_Players ggp: game.getGolf_game_players()) {
                                    Log.d(Global.TAG, ggp.getPlayer().getUser().getUsername());
                                    listNamePlayers.add(ggp.getPlayer().getUser().getName()+" - "+ggp.getPlayer().getUser().getUsername());
                                }

                                adapterPlayersList = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,listNamePlayers);
                                listPlayersView.setAdapter(adapterPlayersList);

                                Log.d(Global.TAG, game.getGame_date()  +" - "+ game.getGame_hour());
                                Log.d(Global.TAG, game.getStatus());
                                Log.d(Global.TAG, game.getGolf_course().getGolf_course());
                                Log.d(Global.TAG, game.getGolf_game_players().get(0).getPlayer().getUser().getName());


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
     * @author Antonio Rodríguez Sirgado
     */
    private void getGame(int idGame) {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.GET_GOLF_GAME, Integer.toString(idGame), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    private void startGame (){
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.START_GOLF_GAME, Integer.toString(game.getId_golf_game()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }


}