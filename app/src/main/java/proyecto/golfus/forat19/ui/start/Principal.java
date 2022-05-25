package proyecto.golfus.forat19.ui.start;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Games;
import Forat19.Message;
import Forat19.Players;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.adapterList.AdapterGameList;
import proyecto.golfus.forat19.adapterList.AdapterPlayerTypeList;
import proyecto.golfus.forat19.ui.add.AddGame;
import proyecto.golfus.forat19.ui.screens.Game;
import proyecto.golfus.forat19.ui.screens.Results;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment contenedor
 *
 * @author Antonio Rodriguez Sirgado
 */
public class Principal extends Fragment implements Observer, View.OnClickListener {

    private ProgressBar loading;
    private RecyclerView rv_playerList, rv_createdGamesList;
    private AdapterPlayerTypeList adapterPlayersList;
    private AdapterGameList adapterCreatedGameList;
    private ArrayList<Players> listPlayers = new ArrayList<>();
    private Message request;
    private TextView txtInfoPlayers, playerSelected, txtInfoCreatedGames, txtCurrentGame;
    private CardView cvPlayerSelected, cvCurrentGame, cvCreatedGames, cvPlayers;
    private ArrayList<Golf_Games> listGolfGamesCreated = new ArrayList<>();
    private ArrayList<Golf_Games> currentGame = new ArrayList<>();
    private Button selectAll;
    private ImageButton ib_stop, ib_pause, ib_delete, ib_info;
    private int created, current;

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
        loading = view.findViewById(R.id.pb_principal_loading);
        cvCurrentGame = view.findViewById(R.id.cv_principal_currentGame);
        cvCreatedGames = view.findViewById(R.id.cv_principal_createdGames);
        cvPlayers = view.findViewById(R.id.cv_principal_players);
        rv_playerList = view.findViewById(R.id.rv_principal_playerType);
        rv_createdGamesList = view.findViewById(R.id.rv_principal_createdGames);
        rv_playerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_createdGamesList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterPlayersList = new AdapterPlayerTypeList(listPlayers, getContext());
        rv_playerList.setAdapter(adapterPlayersList);
        txtInfoPlayers = view.findViewById(R.id.txt_principal_players);
        txtInfoCreatedGames = view.findViewById(R.id.txt_principal_createdGames);
        txtCurrentGame = view.findViewById(R.id.txt_principal_currentGame);
        ib_delete = view.findViewById(R.id.ib_principal_delete);
        ib_pause = view.findViewById(R.id.ib_principal_pause);
        ib_stop = view.findViewById(R.id.ib_principal_stop);
        ib_info = view.findViewById(R.id.ib_principal_info);

        playerSelected = view.findViewById(R.id.txt_principal_PlayerSelected);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

        selectAll = view.findViewById(R.id.btn_principal_selectAll);
        cvPlayerSelected = view.findViewById(R.id.cv_principal_playerSelected);

        ib_delete.setOnClickListener(this);
        ib_info.setOnClickListener(this);
        ib_stop.setOnClickListener(this);
        cvCurrentGame.setOnClickListener(this);
        selectAll.setOnClickListener(this);

        if (Global.activePlayer != null) {
            playerSelected.setText(Global.activePlayer.getPlayer_type().getPlayer_type());
            cvPlayerSelected.setVisibility(View.VISIBLE);
            listGolfGames(Global.SHOW_CREATED_GAMES);
            listGolfGames(Global.SHOW_STARTED_GAMES);
        }

        getPlayers();
        new ItemTouchHelper(swipedRight).attachToRecyclerView(rv_createdGamesList);
        new ItemTouchHelper(swipedLeft).attachToRecyclerView(rv_createdGamesList);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_principal_delete:
                deleteGame(currentGame.get(0));
                break;
            case R.id.btn_principal_selectAll:
                Global.activePlayer = null;
                cvPlayerSelected.setVisibility(View.INVISIBLE);
            case R.id.ib_principal_info:
                Fragment fragment = new Results(currentGame.get(0));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                break;
            case R.id.ib_principal_stop:
                stopGame(currentGame.get(0));
                break;
        }
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

        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // comprueba si ha recibido un objeto Reply que será un error de conexión
                if (arg instanceof Reply) {
                    Utils.showSnack(getView(), ((Reply) arg).getTypeError(), Snackbar.LENGTH_LONG);
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
                                Log.d(Global.TAG, "Numero de players recibidos: " + listPlayers.size());
                                if (listPlayers.size() > 0) {
                                    adapterPlayersList = new AdapterPlayerTypeList(listPlayers, getContext());
                                    rv_playerList.setAdapter(adapterPlayersList);

                                    // Seleccion de jugadores
                                    adapterPlayersList.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            loading.setVisibility(View.VISIBLE);
                                            Log.d(Global.TAG, "Jugador seleccionado: " + listPlayers.get(rv_playerList.getChildAdapterPosition(v)).getPlayer_type().getPlayer_type());
                                            Global.activePlayer = listPlayers.get(rv_playerList.getChildAdapterPosition(v));
                                            playerSelected.setText(listPlayers.get(rv_playerList.getChildAdapterPosition(v)).getPlayer_type().getPlayer_type());
                                            cvPlayerSelected.setVisibility(View.VISIBLE);

                                            listGolfGames(Global.SHOW_STARTED_GAMES);
                                            listGolfGames(Global.SHOW_CREATED_GAMES);
                                            listGolfGames(Global.SHOW_ENDED_GAMES);

                                            created = 0;
                                            current = 0;
                                        }
                                    });
                                } else {
                                    cvPlayers.setVisibility(View.GONE);
                                    selectAll.setVisibility(View.GONE);
                                    cvCreatedGames.setVisibility(View.GONE);
                                    txtInfoPlayers.setText("No has creado aun ningún jugador");
                                }

                                loading.setVisibility(View.GONE);
                            }
                            break;
                        case Global.LIST_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {

                                ArrayList<Golf_Games> listGolfGamesReceived = (ArrayList<Golf_Games>) request.getObject();

                                if (listGolfGamesReceived.size() > 0) {

                                    String status = listGolfGamesReceived.get(0).getStatus();
                                    Log.d(Global.TAG, "Estatus: " + status);

                                    if (status.equals(Global.CREATE)) { // Si son partidos creados
                                        created++;
                                        listGolfGamesCreated = listGolfGamesReceived;
                                        Log.d(Global.TAG, "Numero de juegos: " + listGolfGamesCreated.size());
                                        for (Golf_Games g : listGolfGamesCreated) {
                                            Log.d(Global.TAG, "Juego: " + g.getGolf_game());
                                        }

                                        adapterCreatedGameList = new AdapterGameList(listGolfGamesCreated, getContext());
                                        rv_createdGamesList.setAdapter(adapterCreatedGameList);

                                        adapterCreatedGameList.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Golf_Games gg = listGolfGamesCreated.get(rv_createdGamesList.getChildAdapterPosition(v));

                                                Fragment fragment = new Game(gg.getId_golf_game());
                                                Bundle args = new Bundle();
                                                args.putSerializable("game", gg);
                                                fragment.setArguments(args);
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                                            }
                                        });

                                    }
                                    if (status.equals(Global.START)) { // Si son partidos empezados
                                        Global.currentGame= true;
                                        current++;
                                        currentGame = (ArrayList<Golf_Games>) request.getObject();
                                        txtCurrentGame.setText(currentGame.get(0).getGolf_course().getGolf_course());

                                    }
                                    if (status.equals(Global.END)) { // Si son partidos acabados
                                        // TODO
                                    }
                                }
                            }

                            if (current > 0) {
                                cvCurrentGame.setVisibility(View.VISIBLE);
                            } else {
                                cvCurrentGame.setVisibility(View.GONE);
                                Global.currentGame=false;
                            }

                            if (created > 0) {
                                txtInfoCreatedGames.setText(R.string.pending_games);
                                rv_createdGamesList.setVisibility(View.VISIBLE);
                                Log.d(Global.TAG, "Created: " + created + " Current: " + current);
                            } else {
                                txtInfoCreatedGames.setText(R.string.no_games_to_show);
                                rv_createdGamesList.setVisibility(View.GONE);
                            }
                            loading.setVisibility(View.GONE);
                            break;
                        case Global.DELETE_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                Utils.showSnack(getView(), getString(R.string.game_succesfully_eliminated), Snackbar.LENGTH_LONG);
                            } else if (parameter.equals(Global.ERROR3)) {
                            Utils.showSnack(getView(), getString(R.string.the_game_needs_to_be_finished_first), Snackbar.LENGTH_LONG);
                            } else {
                                Utils.showSnack(getView(), getString(R.string.Game_could_not_be_deleted), Snackbar.LENGTH_LONG);
                            }
                            loading.setVisibility(View.GONE);
                            break;
                        case Global.END_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                Utils.showSnack(getView(), getString(R.string.game_succesfully_ended), Snackbar.LENGTH_LONG);
                            } else {
                                Utils.showSnack(getView(), getString(R.string.game_could_not_be_ended), Snackbar.LENGTH_LONG);
                            }
                            loading.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    // Si desplaza un juego a la derecha
    ItemTouchHelper.SimpleCallback swipedRight = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(getResources().getColor(R.color.error))
                    .addActionIcon(R.drawable.ic_delete)
                    .addSwipeRightLabel(getString(R.string.delete))
                    .setSwipeRightLabelColor(getResources().getColor(R.color.white))
                    .setSwipeRightLabelTextSize(1, 16)
                    .create()
                    .decorate();
        }
    };

    // Si desplaza un juego a la izquierda
    ItemTouchHelper.SimpleCallback swipedLeft = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Golf_Games gg = listGolfGamesCreated.get(viewHolder.getAbsoluteAdapterPosition());
            Fragment fragment = new AddGame();
            Bundle args = new Bundle();
            args.putSerializable("game", gg);
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(getResources().getColor(R.color.green))
                    .addActionIcon(R.drawable.ic_edit)
                    .addSwipeLeftLabel(getString(R.string.update))
                    .setSwipeLeftLabelColor(getResources().getColor(R.color.white))
                    .setSwipeLeftLabelTextSize(1, 16)
                    .create()
                    .decorate();
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

        listGolfGames(Global.SHOW_STARTED_GAMES);
    }

    /**
     * <b>Envia el mensaje para parar el partido seleccionado</b><br>
     * Mensaje = (token¬device, EndGolfGame, id golf game, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    private void stopGame(Golf_Games golf_games) {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.END_GOLF_GAME, Integer.toString(golf_games.getId_golf_game()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
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

}
