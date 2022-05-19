package proyecto.golfus.forat19.ui.start;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Games;
import Forat19.Message;
import Forat19.Players;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.adapterList.AdapterGameList;
import proyecto.golfus.forat19.adapterList.AdapterPlayerTypeList;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment contenedor
 *
 * @author Antonio Rodriguez Sirgado
 */
public class Principal extends Fragment implements Observer {

    private RecyclerView rv_playerList, rv_createdGamesList;
    private AdapterPlayerTypeList adapterPlayersList;
    private AdapterGameList adapterGameList;
    private ArrayList<Players> listPlayers = new ArrayList<>();
    private Message request;
    private TextView txtInfoPlayers, playerSelected;
    private CardView deleteCurrentPlayer, cvPlayerSelected, cvCurrentGame, cvCreatedGames;
    private ArrayList<Golf_Games> listGolfGamesCreated = new ArrayList<>();

    public Principal() {
    }

    public static Principal newInstance() {
        Principal fragment = new Principal();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_principal, container, false);
        cvCurrentGame = view.findViewById(R.id.cv_principal_currentGame);
        cvCreatedGames = view.findViewById(R.id.cv_principal_createdGames);
        rv_playerList = view.findViewById(R.id.rv_principal_playerType);
        rv_createdGamesList = view.findViewById(R.id.rv_principal_createdGames);
        rv_playerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_createdGamesList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterPlayersList = new AdapterPlayerTypeList(listPlayers, getContext());
        rv_playerList.setAdapter(adapterPlayersList);
        txtInfoPlayers = view.findViewById(R.id.txt_principal_players);
        playerSelected = view.findViewById(R.id.txt_principal_PlayerSelected);
        deleteCurrentPlayer = view.findViewById(R.id.cv_principal_deleteActualPlayer);
        cvPlayerSelected = view.findViewById(R.id.cv_principal_playerSelected);

        deleteCurrentPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.activePlayer = null;
                cvPlayerSelected.setVisibility(View.INVISIBLE);
            }
        });

        if (Global.activePlayer != null) {
            playerSelected.setText(Global.activePlayer.getPlayer_type().getPlayer_type());
            cvPlayerSelected.setVisibility(View.VISIBLE);
            listGolfGames(Global.SHOW_CREATED_GAMES);
            listGolfGames(Global.SHOW_STARTED_GAMES);
        }

        getTypePlayers();


        return view;
    }

    /**
     * Permanece a la espera de que las variables cambien
     *
     * @param o   la clase observada
     * @param arg objeto observado
     * @author Antonio Rodriguez Sirgado
     */
    @Override
    public void update(Observable o, Object arg) {
        // comprueba si ha recibido un objeto Reply que será un error de conexión
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            Fragment fragment = new Principal();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {
            request = (Message) arg;
            String command = request.getCommand();
            String parameter = request.getParameters();

            switch (command) {
                case Global.LIST_USER_PLAYER:
                    if (parameter.equals(Global.OK)) {

                        listPlayers = (ArrayList<Players>) request.getObject();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (listPlayers.size() > 0) {
                                    adapterPlayersList = new AdapterPlayerTypeList(listPlayers, getContext());
                                    rv_playerList.setAdapter(adapterPlayersList);

                                    // Seleccion de jugadores
                                    adapterPlayersList.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d(Global.TAG, "Jugador seleccionado: " + listPlayers.get(rv_playerList.getChildAdapterPosition(v)).getPlayer_type().getPlayer_type());
                                            Global.activePlayer = listPlayers.get(rv_playerList.getChildAdapterPosition(v));
                                            playerSelected.setText(listPlayers.get(rv_playerList.getChildAdapterPosition(v)).getPlayer_type().getPlayer_type());
                                            cvPlayerSelected.setVisibility(View.VISIBLE);

                                            listGolfGames(Global.SHOW_CREATED_GAMES);
                                            listGolfGames(Global.SHOW_STARTED_GAMES);
                                        }
                                    });
                                } else {
                                    txtInfoPlayers.setText("No has creado aun ningún jugador");

                                }
                            }
                        });
                    }
                    break;
                case Global.LIST_GOLF_GAME:
                    if (parameter.equals(Global.OK)) {
                        listGolfGamesCreated = (ArrayList<Golf_Games>) request.getObject();

                        if (listGolfGamesCreated.size() > 0) {
                            String status = listGolfGamesCreated.get(0).getStatus();
                            Log.d(Global.TAG, "Estatus: " + status);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    if (status.equals(Global.CREATE)) { // Si son partidos creados
                                        cvCreatedGames.setVisibility(View.VISIBLE);
                                        adapterGameList = new AdapterGameList(listGolfGamesCreated, getContext());
                                        rv_createdGamesList.setAdapter(adapterGameList);
                                    } else if (status.equals(Global.START)) { // Si son partidos empezados
                                        cvCurrentGame.setVisibility(View.VISIBLE);
                                    } else if (status.equals(Global.END)){

                                    }
                                }
                            });

                            for (Golf_Games g : listGolfGamesCreated) {
                                Log.d(Global.TAG, "Juego: " + g.getGolf_game());
                            }
                        } else {
                            Log.d(Global.TAG, "Sin partidos que mostrar");
                        }
                    }
            }

        }
    }


    private void getTypePlayers() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_USER_PLAYER, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    private void listGolfGames(String typeList) {
        Log.d(Global.TAG, "Enviando peticion tipo: " + typeList);
        String txt = Global.activePlayer.getId_player() + "¬" + typeList;
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME, txt, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

}