package proyecto.golfus.forat19.ui.screens;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Game_Players;
import Forat19.Golf_Game_Results;
import Forat19.Golf_Games;
import Forat19.Message;
import proyecto.golfus.forat19.adapterList.AdapterHoleList;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.adapterList.AdapterResultList;
import proyecto.golfus.forat19.adapterList.AdapterUserList;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para mostrar resultados del partido
 */
public class Results extends Fragment implements Observer {
    private RecyclerView rv_Results, rv_Holes, rv_Users;
    private ProgressBar loading;
    private Golf_Games currentGame;
    private Message request;
    private AdapterResultList adapterResultList;
    private AdapterHoleList adapterHoleList;
    private AdapterUserList adapterUserList;
    private TextView txt_course, txt_courseType, txt_selectPlayer, txt_selectHole;
    private List<String> listNamePlayers = new ArrayList<>();
    private List<String> listHoles = new ArrayList<>();

    public Results(Golf_Games golf_games) {
        currentGame = golf_games;
    }

    public Results() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        adapterResultList.filterPlayer("");
        updateResults();
        Log.d(Global.TAG,"Pausado: guardando resultados");
    }

    /*@Override
    public void onStop() {
        super.onStop();
        adapterResultList.filterPlayer("");
        updateResults();
        Log.d(Global.TAG,"Parado: guardando resultados");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapterResultList.filterPlayer("");
        updateResults();
        Log.d(Global.TAG,"Parado: guardando resultados");
    }*/

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
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        txt_course = view.findViewById(R.id.txt_result_course);
        txt_courseType = view.findViewById(R.id.txt_result_typeCourse);
        txt_selectHole = view.findViewById(R.id.txt_result_selectHole);
        txt_selectPlayer= view.findViewById(R.id.txt_result_selectPlayer);
        loading = view.findViewById(R.id.pb_result_loading);
        rv_Holes = view.findViewById(R.id.rv_result_holes);
        rv_Holes.setLayoutManager(new GridLayoutManager(getContext(), 9));
        rv_Results = view.findViewById(R.id.rv_result_results);
        rv_Results.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_Users =view.findViewById(R.id.rv_results_users);
        rv_Users.setLayoutManager(new GridLayoutManager(getContext(), 4));
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

        txt_course.setText(R.string.loading_game);
        txt_courseType.setText(currentGame.getGolf_course().getGolf_course());

        listResults();
        //showResults();

        return view;
    }

    @Override
    public void update(Observable o, Object arg) {
        // comprueba si ha recibido un objeto Reply que será un error de conexión

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
                        case Global.LIST_GOLF_GAME_RESULT:
                            if (parameter.equals(Global.OK)) {
                                Global.setGolfGameResults((List<Golf_Game_Results>) request.getObject());
                                showResults();
                            }
                    }
                }
            }
        });
    }

    /**
     * <b>Envia peticion para recibir resultados del partido</b><br>
     * Mensaje = (token¬device, DeleteGolfGame, id golf game, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    private void listResults() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME_RESULT, Integer.toString(currentGame.getId_golf_game()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * Muestra los resultados por pantalla
     * @author Antonio Rodríguez Sirgado
     */
    private void showResults() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                txt_course.setText(currentGame.getGolf_course().getGolf_course());
                txt_courseType.setText(Global.getGolfGameResults().get(0).getGolf_course().getGolf_course_type().getGolf_course_type());

                for (Golf_Game_Players ggp : currentGame.getGolf_game_players()) {
                    listNamePlayers.add(ggp.getPlayer().getUser().getUsername());
                }

                for (int i = 1; i < currentGame.getGolf_course().getHoles() + 1; i++) {
                    listHoles.add(String.valueOf(i));
                }

                adapterUserList = new AdapterUserList(listNamePlayers);
                rv_Users.setAdapter(adapterUserList);
                txt_selectPlayer.setVisibility(View.VISIBLE);

                adapterHoleList = new AdapterHoleList(listHoles);
                rv_Holes.setAdapter(adapterHoleList);
                txt_selectHole.setVisibility(View.VISIBLE);

                adapterResultList = new AdapterResultList(Global.getGolfGameResults());
                rv_Results.setAdapter(adapterResultList);
                rv_Results.setVisibility(View.VISIBLE);


                adapterHoleList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapterResultList.filterHole(listHoles.get(rv_Holes.getChildAdapterPosition(v)));
                    }
                });

                adapterUserList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rv_Results.setVisibility(View.VISIBLE);
                        adapterResultList.filterPlayer(listNamePlayers.get(rv_Users.getChildAdapterPosition(v)));
                    }
                });

                rv_Users.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }
        });

    }

    /**
     * <b>Envia el mensaje para parar el partido seleccionado</b><br>
     * Mensaje = (token¬device, EndGolfGame, id golf game, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    private void updateResults() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.UPDATE_GOLF_GAME_RESULTS, null, Global.getGolfGameResults());
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }
}