package proyecto.golfus.forat19.ui.start;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Forat19.Golf_Game_Results;
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

    private SwitchCompat sw_showEnded;
    private ProgressBar loading;
    private RecyclerView rv_playerList, rv_createdGamesList, rv_endedGamesList;
    private AdapterPlayerTypeList adapterPlayersList;
    private AdapterGameList adapterCreatedGameList, adapterEndedGameList;
    private ArrayList<Players> listPlayers = new ArrayList<>();
    private Message request;
    private TextView txt_Players, txt_playerSelected, txt_infoCreatedGames, txt_CurrentGame, txt_infoEndedGames, txt_infoPlayers, txt_time;
    private CardView cvPlayerSelected, cvCurrentGame, cvCreatedGames, cvEndedGames, cvPlayers, cv_infoPlayers;
    private ArrayList<Golf_Games> listGolfGamesCreated = new ArrayList<>();
    private ArrayList<Golf_Games> listGolfGamesEnded = new ArrayList<>();
    private ArrayList<Golf_Games> currentGame = new ArrayList<>();
    private Button selectAll;
    private ImageButton ib_stop, ib_pause, ib_info;
    private int created, current, ended;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

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
        sw_showEnded = view.findViewById(R.id.sw_principal_showEnded);
        loading = view.findViewById(R.id.pb_principal_loading);
        cvCurrentGame = view.findViewById(R.id.cv_principal_currentGame);
        cvCreatedGames = view.findViewById(R.id.cv_principal_createdGames);
        cvEndedGames = view.findViewById(R.id.cv_principal_endedGames);
        cvPlayers = view.findViewById(R.id.cv_principal_players);
        cv_infoPlayers = view.findViewById(R.id.cv_principal_infoPlayer);
        rv_playerList = view.findViewById(R.id.rv_principal_playerType);
        rv_createdGamesList = view.findViewById(R.id.rv_principal_createdGames);
        rv_playerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_createdGamesList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_endedGamesList = view.findViewById(R.id.rv_principal_endedGames);
        rv_endedGamesList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterPlayersList = new AdapterPlayerTypeList(listPlayers, getContext());
        rv_playerList.setAdapter(adapterPlayersList);
        txt_Players = view.findViewById(R.id.txt_principal_players);
        txt_infoPlayers = view.findViewById(R.id.txt_principal_infoPlayer);
        txt_infoCreatedGames = view.findViewById(R.id.txt_principal_createdGames);
        txt_infoEndedGames = view.findViewById(R.id.txt_principal_endedGames);
        txt_CurrentGame = view.findViewById(R.id.txt_principal_currentGame);
        txt_playerSelected = view.findViewById(R.id.txt_principal_PlayerSelected);
        txt_time = view.findViewById(R.id.txt_principal_time);
        ib_pause = view.findViewById(R.id.ib_principal_pause);
        ib_stop = view.findViewById(R.id.ib_principal_stop);
        ib_info = view.findViewById(R.id.ib_principal_info);
        ib_info.setEnabled(false);


        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

        selectAll = view.findViewById(R.id.btn_principal_selectAll);
        cvPlayerSelected = view.findViewById(R.id.cv_principal_playerSelected);

        ib_info.setOnClickListener(this);
        ib_stop.setOnClickListener(this);
        ib_pause.setOnClickListener(this);
        cvCurrentGame.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        sw_showEnded.setOnClickListener(this);

        if (Global.activePlayer != null) {
            loadAllGames();
        } else {
            cvCreatedGames.setVisibility(View.GONE);
        }

        getPlayers();
        new ItemTouchHelper(swipedRight).attachToRecyclerView(rv_createdGamesList);
        new ItemTouchHelper(swipedLeft).attachToRecyclerView(rv_createdGamesList);

        if (Utils.getShowEndedStatus(getActivity())) {
            sw_showEnded.setChecked(true);
            cvEndedGames.setVisibility(View.VISIBLE);
        } else {
            sw_showEnded.setChecked(false);
            cvEndedGames.setVisibility(View.GONE);
        }

        Utils.hideKeyboard(getActivity());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw_principal_showEnded:

                if (sw_showEnded.isChecked()) {
                    cvEndedGames.setVisibility(View.VISIBLE);
                    Utils.setShowEndedStatus(getActivity(), true);
                } else {
                    cvEndedGames.setVisibility(View.GONE);
                    Utils.setShowEndedStatus(getActivity(), false);
                }
                break;
            case R.id.btn_principal_selectAll:
                Global.activePlayer = null;
                cvPlayerSelected.setVisibility(View.INVISIBLE);
                break;
            case R.id.ib_principal_info:
                Fragment fragment = new Results(currentGame.get(0));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment,"results").addToBackStack(null).commit();
                break;
            case R.id.ib_principal_stop:
                if (Global.getGolfGameResults() != null) {
                    AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                    confirmation.setTitle(R.string.attention);
                    confirmation.setMessage(R.string.are_you_sure_end_game);
                    confirmation.setCancelable(true);
                    confirmation.setPositiveButton(R.string.yes, (dialog, which) -> {
                        endGame(currentGame.get(0));

                    });
                    confirmation.setNegativeButton(R.string.Cancel, (dialog, which) -> {

                    });

                    confirmation.show();
                }
                break;
            case R.id.ib_principal_pause:

                if (Global.isTimePaused) {
                    ib_pause.setImageResource(android.R.drawable.ic_media_pause);
                    Global.start = Instant.now();
                } else {
                    ib_pause.setImageResource(android.R.drawable.ic_media_play);
                    Global.elapsedTime = Global.elapsedTime + Duration.between(Global.start, Instant.now()).toMillis();
                    Log.d(Global.TAG, "Pausando en tiempo total: " + Utils.formatTime(Global.elapsedTime));
                }

                Global.isTimePaused = !Global.isTimePaused;

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
                                            txt_playerSelected.setText(listPlayers.get(rv_playerList.getChildAdapterPosition(v)).getPlayer_type().getPlayer_type());
                                            cvPlayerSelected.setVisibility(View.VISIBLE);
                                            cvCreatedGames.setVisibility(View.VISIBLE);
                                            sw_showEnded.setVisibility(View.VISIBLE);

                                            listGolfGames(Global.SHOW_STARTED_GAMES);
                                            listGolfGames(Global.SHOW_CREATED_GAMES);
                                            listGolfGames(Global.SHOW_ENDED_GAMES);
                                            txt_infoCreatedGames.setText(R.string.loading_games);
                                            txt_infoEndedGames.setText(R.string.loading_games);

                                            created = 0;
                                            current = 0;
                                            ended = 0;
                                        }
                                    });
                                } else {
                                    cvPlayers.setVisibility(View.GONE);
                                    selectAll.setVisibility(View.GONE);
                                    cvCreatedGames.setVisibility(View.GONE);
                                    //txt_playerSelected.setText(R.string.no_gamer_created);
                                    cv_infoPlayers.setVisibility(View.VISIBLE);
                                    sw_showEnded.setVisibility(View.GONE);
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
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "game").addToBackStack(null).commit();
                                            }
                                        });

                                    }
                                    if (status.equals(Global.START)) { // Si son partidos empezados
                                        Global.currentGame = true;
                                        current++;
                                        currentGame = listGolfGamesReceived;
                                        txt_CurrentGame.setText(currentGame.get(0).getGolf_course().getGolf_course());
                                        ListResults(currentGame.get(0));

                                    }
                                    if (status.equals(Global.END)) { // Si son partidos acabados
                                        ended++;
                                        listGolfGamesEnded = listGolfGamesReceived;
                                        adapterEndedGameList = new AdapterGameList(listGolfGamesEnded, getContext());
                                        rv_endedGamesList.setAdapter(adapterEndedGameList);
                                        sw_showEnded.setEnabled(true);
                                        adapterEndedGameList.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Fragment fragment = new Results(listGolfGamesEnded.get(rv_endedGamesList.getChildAdapterPosition(v)));
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                                            }
                                        });
                                    }
                                }
                            }

                            Log.d(Global.TAG, "Current: " + current + " | created: " + created + " | Ended: " + ended);


                            if (current > 0) {
                                cvCurrentGame.setVisibility(View.VISIBLE);
                                showTime();
                            } else {
                                cvCurrentGame.setVisibility(View.GONE);
                                Global.currentGame = false;
                            }

                            if (created > 0) {
                                txt_infoCreatedGames.setText(R.string.pending_games);
                                cvCreatedGames.setVisibility(View.VISIBLE);
                                rv_createdGamesList.setVisibility(View.VISIBLE);
                            } else {
                                txt_infoCreatedGames.setText(R.string.no_pending_games_to_show);
                                rv_createdGamesList.setVisibility(View.GONE);
                            }

                            if (ended > 0) {
                                txt_infoEndedGames.setText(R.string.ended_games);
                                rv_endedGamesList.setVisibility(View.VISIBLE);
                                //sw_showEnded.setVisibility(View.VISIBLE);
                            } else {
                                txt_infoEndedGames.setText(R.string.no_ended_games_to_show);
                                rv_endedGamesList.setVisibility(View.GONE);
                                //sw_showEnded.setVisibility(View.GONE);
                            }

                            loading.setVisibility(View.GONE);
                            break;
                        case Global.DELETE_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                Utils.showSnack(getView(), getString(R.string.game_succesfully_eliminated), Snackbar.LENGTH_LONG);
                                loadAllGames();
                            } else if (parameter.equals(Global.ERROR3)) {
                                Utils.showSnack(getView(), getString(R.string.the_game_needs_to_be_finished_first), Snackbar.LENGTH_LONG);
                            } else {
                                Utils.showSnack(getView(), getString(R.string.Game_could_not_be_deleted), Snackbar.LENGTH_LONG);
                            }
                            loading.setVisibility(View.GONE);
                            break;
                        case Global.END_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                Global.setGolfGameResults(null);
                                listGolfGames(Global.SHOW_STARTED_GAMES);
                                listGolfGames(Global.SHOW_CREATED_GAMES);
                                listGolfGames(Global.SHOW_ENDED_GAMES);
                                created = 0;
                                current = 0;
                                ended = 0;
                                executor.shutdownNow();

                                Utils.showSnack(getView(), getString(R.string.game_succesfully_ended), Snackbar.LENGTH_LONG);
                            } else {
                                Utils.showSnack(getView(), getString(R.string.game_could_not_be_ended), Snackbar.LENGTH_LONG);
                            }
                            loading.setVisibility(View.GONE);
                            break;


                        case Global.LIST_GOLF_GAME_RESULT:
                            if (parameter.equals((Global.OK))) {
                                Global.setGolfGameResults((List<Golf_Game_Results>) request.getObject());
                                ib_info.setEnabled(true);
                            }
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
     * @param golf_games partido seleccionado
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
     * @param golf_games partido seleccionado
     * @author Antonio Rodríguez Sirgado
     */
    private void endGame(Golf_Games golf_games) {
        Log.d(Global.TAG, "Voy a finalizar el partido: " + golf_games.getId_golf_game() + " | status: " + golf_games.getStatus());
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

    /**
     * <b>Envia peticion para recibir resultados del partido</b><br>
     * Mensaje = (token¬device, DeleteGolfGame, id golf game, null)
     *
     * @param golf_games partido seleccionado
     * @author Antonio Rodríguez Sirgado
     */
    private void ListResults(Golf_Games golf_games) {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME_RESULT, Integer.toString(golf_games.getId_golf_game()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    private void showTime() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                {
                    if (!Global.isTimePaused) {
                        Log.d(Global.TAG,"Mensaje cada segundo");
                        txt_time.setText(Utils.formatTime(Duration.between(Global.start, Instant.now()).toMillis() + Global.elapsedTime));
                        Log.d(Global.TAG, Utils.formatTime(Duration.between(Global.start, Instant.now()).toMillis() + Global.elapsedTime));
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS); // cada 1 segundo
    }

    private void loadAllGames(){
        txt_playerSelected.setText(Global.activePlayer.getPlayer_type().getPlayer_type());
        cvPlayerSelected.setVisibility(View.VISIBLE);
        listGolfGames(Global.SHOW_CREATED_GAMES);
        listGolfGames(Global.SHOW_STARTED_GAMES);
        listGolfGames(Global.SHOW_ENDED_GAMES);
        created = 0;
        current = 0;
        ended = 0;
        txt_infoCreatedGames.setText(R.string.loading_games);
        txt_infoEndedGames.setText(R.string.loading_games);
    }
}
