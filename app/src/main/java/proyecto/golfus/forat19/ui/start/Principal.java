package proyecto.golfus.forat19.ui.start;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import proyecto.golfus.forat19.ui.add.AddGame;
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
    private AdapterGameList adapterCreatedGameList;
    private ArrayList<Players> listPlayers = new ArrayList<>();
    private Message request;
    private TextView txtInfoPlayers, playerSelected, txtInfoCreatedGames;
    private CardView deleteCurrentPlayer, cvPlayerSelected, cvCurrentGame, cvCreatedGames;
    private ArrayList<Golf_Games> listGolfGamesCreated = new ArrayList<>();
    private Button selectAll;

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
        txtInfoCreatedGames = view.findViewById(R.id.txt_principal_createdGames);
        playerSelected = view.findViewById(R.id.txt_principal_PlayerSelected);

        selectAll = view.findViewById(R.id.btn_principal_selectAll);
        cvPlayerSelected = view.findViewById(R.id.cv_principal_playerSelected);

        selectAll.setOnClickListener(new View.OnClickListener() {
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

        getPlayers();
        new ItemTouchHelper(itemTouch).attachToRecyclerView(rv_createdGamesList);
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

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                                            //listGolfGames(Global.SHOW_STARTED_GAMES);
                                        }
                                    });
                                } else {
                                    txtInfoPlayers.setText("No has creado aun ningún jugador");
                                }

                            }
                            break;
                        case Global.LIST_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                ArrayList<Golf_Games> listGolfGamesReceived = (ArrayList<Golf_Games>) request.getObject();

                                if (listGolfGamesReceived.size() > 0) {

                                    String status = listGolfGamesReceived.get(0).getStatus();
                                    Log.d(Global.TAG, "Estatus: " + status);

                                    if (status.equals(Global.CREATE)) { // Si son partidos creados

                                        listGolfGamesCreated = listGolfGamesReceived;

                                        Log.d(Global.TAG,"Numero de juegos: "+listGolfGamesCreated);

                                        if (listGolfGamesCreated.size() == 0) {
                                            rv_createdGamesList.setVisibility(View.GONE);
                                            txtInfoCreatedGames.setText(R.string.no_games_to_show);
                                        } else {
                                            cvCreatedGames.setVisibility(View.VISIBLE);
                                            adapterCreatedGameList = new AdapterGameList(listGolfGamesCreated, getContext());
                                            rv_createdGamesList.setAdapter(adapterCreatedGameList);

                                            adapterCreatedGameList.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Golf_Games gg = listGolfGamesCreated.get(rv_createdGamesList.getChildAdapterPosition(v));
                                                    Fragment fragment = new AddGame();
                                                    Bundle args = new Bundle();
                                                    args.putSerializable("game", gg);
                                                    fragment.setArguments(args);
                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                                                }
                                            });
                                        }

                                    } else if (status.equals(Global.START)) { // Si son partidos empezados
                                        cvCurrentGame.setVisibility(View.VISIBLE);
                                    } else if (status.equals(Global.END)) { // Si son partidos acabados

                                    }

                                    for (Golf_Games g : listGolfGamesCreated) {
                                        Log.d(Global.TAG, "Juego: " + g.getGolf_game());
                                    }
                                } else {
                                    Log.d(Global.TAG, "Sin partidos que mostrar");
                                }
                            }
                            break;
                        case Global.DELETE_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                Utils.showSnack(getView(), getString(R.string.game_succesfully_eliminated), Snackbar.LENGTH_LONG);
                            } else {
                                Utils.showSnack(getView(), getString(R.string.Game_could_not_be_deleted), Snackbar.LENGTH_LONG);
                            }
                            break;
                    }

                }
            }
        });

    }

    /**
     * <b>Envia el mensaje para cargar los jugadores del usuario</b><br>
     * Mensaje = (token¬device, listUserPlayer, id user, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    private void getPlayers() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_USER_PLAYER, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia el mensaje para cargar las partidas</b><br>
     * Mensaje = (token¬device, listGolfGame, C/S/E, null)
     *
     * @param typeList tipo de listado (C=Created, S=Started, E=Ended)
     * @author Antonio Rodríguez Sirgado
     */
    private void listGolfGames(String typeList) {
        Log.d(Global.TAG, "Enviando peticion tipo: " + typeList);
        String txt = Global.activePlayer.getId_player() + "¬" + typeList;
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME, txt, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
            confirmation.setTitle(R.string.attention);
            confirmation.setMessage(R.string.are_you_sure_eliminate_game);
            confirmation.setCancelable(true);
            confirmation.setPositiveButton(R.string.yes, (dialog, which) -> {
                deleteGame(listGolfGamesCreated.get(viewHolder.getAbsoluteAdapterPosition()));
                listGolfGamesCreated.remove(viewHolder.getAbsoluteAdapterPosition());
                adapterCreatedGameList.notifyDataSetChanged();
            });
            confirmation.setNegativeButton(R.string.Cancel, (dialog, which) -> {
                adapterCreatedGameList.notifyDataSetChanged();
            });

            confirmation.show();
        }
    };

    /**
     * <b>Envia el mensaje para borrar el partido seleccionado</b><br>
     * Mensaje = (token¬device, DeleteGolfGame, id golf game, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    private void deleteGame(Golf_Games golf_games) {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.DELETE_GOLF_GAME, Integer.toString(golf_games.getId_golf_game()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

}