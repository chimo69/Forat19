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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.adapterList.AdapterResultList;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para mostrar resultados del partido
 */
public class Results extends Fragment implements Observer {
    private RecyclerView rvResults, rvHoles;
    private ProgressBar loading;
    private Golf_Games currentGame;
    private Message request;
    private AdapterResultList adapterResultList;
    private AdapterHoleList adapterHoleList;
    private TextView txtCourse, txtCourseType;
    private List<String> listNamePlayers = new ArrayList<>();
    private List<String> listHoles = new ArrayList<>();
    private ArrayAdapter<String> adapterPlayersList;
    private ListView listPlayersView;

    public Results(Golf_Games golf_games) {
        currentGame = golf_games;
    }

    public Results() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapterResultList.filterPlayer("");
        Log.d(Global.TAG, "Hasta luego");
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
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        txtCourse = view.findViewById(R.id.txt_result_course);
        txtCourseType = view.findViewById(R.id.txt_result_typeCourse);
        loading = view.findViewById(R.id.pb_result_loading);
        listPlayersView = view.findViewById(R.id.lv_result_players);
        rvHoles = view.findViewById(R.id.rv_result_holes);
        rvHoles.setLayoutManager(new GridLayoutManager(getContext(), 9));
        rvResults = view.findViewById(R.id.rv_result_results);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);


        if (Global.getGolfGameResults() != null) {
            Log.d(Global.TAG,"Tamaño: "+ Global.getGolfGameResults().size());
            for (Golf_Game_Results g : Global.getGolfGameResults()) {
                Log.d(Global.TAG, " Info: " + g.getGolf_course_hole().getId_golf_course_hole());
            }
        }

        showResults();

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
    private void ListResults() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME_RESULT, Integer.toString(currentGame.getId_golf_game()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    private void showResults() {

        if (Global.getGolfGameResults() == null ) {
            ListResults();
        } else {
            if (Global.getGolfGameResults().get(0).getGolf_game().getId_golf_game() == currentGame.getId_golf_game()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        txtCourse.setText(currentGame.getGolf_course().getGolf_course());
                        //txtCourseType.setText(currentGame.getGolf_course().getGolf_course_type().getGolf_course_type());

                        for (Golf_Game_Players ggp : currentGame.getGolf_game_players()) {
                            listNamePlayers.add(ggp.getPlayer().getUser().getUsername());
                        }

                        for (int i = 1; i < currentGame.getGolf_course().getHoles() + 1; i++) {
                            listHoles.add(String.valueOf(i));
                        }

                        adapterPlayersList = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listNamePlayers);
                        listPlayersView.setAdapter(adapterPlayersList);

                        adapterHoleList = new AdapterHoleList(listHoles);
                        rvHoles.setAdapter(adapterHoleList);

                        adapterResultList = new AdapterResultList(Global.getGolfGameResults());
                        rvResults.setAdapter(adapterResultList);

                        adapterHoleList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adapterResultList.filterHole(listHoles.get(rvHoles.getChildAdapterPosition(v)));
                                Log.d(Global.TAG, "Pulsando: " + listHoles.get(rvHoles.getChildAdapterPosition(v)));
                                rvResults.setVisibility(View.VISIBLE);
                            }
                        });

                        listPlayersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                rvResults.setVisibility(View.VISIBLE);
                                adapterResultList.filterPlayer(listNamePlayers.get(position));
                            }
                        });

                        listPlayersView.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                });
            } else {
                ListResults();
            }


        }

    }
}