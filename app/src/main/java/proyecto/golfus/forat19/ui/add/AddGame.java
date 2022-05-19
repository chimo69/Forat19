package proyecto.golfus.forat19.ui.add;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Courses;
import Forat19.Golf_Game_Players;
import Forat19.Golf_Game_Types;
import Forat19.Golf_Games;
import Forat19.Message;
import Forat19.Players;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.adapterList.AdapterCoursesList;
import proyecto.golfus.forat19.adapterList.AdapterFriendshipList;
import proyecto.golfus.forat19.adapterList.AdapterPlayersList;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para la creacion de partidas
 *
 * @author Antonio Rodríguez Sirgado
 */
public class AddGame extends Fragment implements Observer {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button createGame;
    private ImageButton deleteCourse;
    private TextView txtNumberOfGamers, txtCourseSelected;
    private Spinner gameType;
    private List<String> listCourseType = new ArrayList<String>();
    private List<String> listGameType = new ArrayList<String>();
    private ArrayList<Golf_Game_Types> golfGameTypes;
    private ArrayList<Players> listPlayerFriends, listPlayersSelected;
    private ArrayList<Golf_Courses> listCourses;
    private List<Golf_Game_Players> golfGamePlayersSend = new ArrayList<>();
    private SearchView searchFriendsList, searchCoursesList;
    private RecyclerView rw_friends, rw_courses, rw_players;
    private AdapterPlayersList adapterPlayerFriendsList, adapterPlayersSelectedList;
    private AdapterCoursesList adapterCoursesList;
    private CardView cv_courseSelected, cv_players, cv_playersTitle, cv_selectPlayer, cv_number;
    private Golf_Courses courseSelected;
    private Golf_Game_Types golfGameTypesSelected;

    private Message request;

    public AddGame() {
    }

    public static AddGame newInstance() {
        AddGame fragment = new AddGame();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_add_game, container, false);
        gameType = view.findViewById(R.id.createGame_gameType);
        rw_friends = view.findViewById(R.id.newGame_recyclerFriends);
        rw_courses = view.findViewById(R.id.newGame_recyclerCourse);
        rw_players = view.findViewById(R.id.newGame_RecyclerPlayers);
        cv_courseSelected = view.findViewById(R.id.createGame_cvCourse);
        cv_playersTitle = view.findViewById(R.id.createGame_cvPlayerTitle);
        cv_players = view.findViewById(R.id.creatGame_cvPlayers);
        cv_selectPlayer = view.findViewById(R.id.createGame_cvSelectPlayer);
        cv_number = view.findViewById(R.id.createGame_cvNumber);
        createGame = view.findViewById(R.id.createGame_btnCreateGame);
        deleteCourse = view.findViewById(R.id.createGame_btnDeleteCourse);
        searchFriendsList = view.findViewById(R.id.newGame_searchFriend);
        searchCoursesList = view.findViewById(R.id.newGame_searchCourse);
        txtNumberOfGamers = view.findViewById(R.id.createGame_number);
        txtCourseSelected = view.findViewById(R.id.createGame_selectCourse);

        listPlayersSelected = new ArrayList<>();

        // Boton de seleccion de tipo de juego
        gameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                golfGameTypesSelected = golfGameTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Boton crear juego
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPlayersSelected.size() > 0) {
                    sendNewGame();
                } else {
                    Utils.showSnack(getView(), getString(R.string.not_enough_gamers), Snackbar.LENGTH_LONG);
                }
            }
        });

        // Boton eliminar recorrido actual
        deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCourseSelected(false);
            }
        });

        searchFriendsList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapterFriendshipList.filter(newText);
                return false;
            }
        });

        searchCoursesList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterCoursesList.filter(newText);
                return false;
            }
        });

        txtNumberOfGamers.setText(String.valueOf(golfGamePlayersSend.size()));

        rw_friends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rw_courses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rw_players.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        new ItemTouchHelper(itemTouch).attachToRecyclerView(rw_players);

        adapterPlayersSelectedList = new AdapterPlayersList(listPlayersSelected);
        rw_players.setAdapter(adapterPlayersSelectedList);

        // Campo de busqueda de recorridos
        searchCoursesList.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        loadGameType();
        loadFriendsWithThisGamer();
        loadCoursesRelated();

        listPlayersSelected.add(Global.activePlayer);

        adapterPlayersSelectedList.notifyItemInserted(listPlayersSelected.size() + 1);
        txtNumberOfGamers.setText(String.valueOf(listPlayersSelected.size()));

        return view;
    }


    /**
     * Permanece a la espera de que el objeto observado varie
     *
     * @param o   clase observada
     * @param arg objeto observado
     * @author Antonio Rodriguez Sirgado
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
        } else {

            request = (Message) arg;

            switch (((Message) arg).getCommand()) {

                case Global.LIST_GOLF_GAME_TYPE:

                    golfGameTypes = (ArrayList<Golf_Game_Types>) request.getObject();
                    Log.d(Global.TAG, "Tipos de juego recibidos: " + golfGameTypes.size());

                    for (int i = 0; i < golfGameTypes.size(); i++) {
                        listGameType.add(golfGameTypes.get(i).getGolf_game_type());
                        Log.d(Global.TAG, "Tipo de juego: " + golfGameTypes.get(i).getGolf_game_type());
                    }
                    Log.d(Global.TAG, "-------------------------------------------------");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, listGameType);
                            gameType.setAdapter(adapter);

                        }
                    });
                    break;
                case Global.LIST_GOLF_GAME_PLAYER:

                    request = (Message) arg;
                    listPlayerFriends = (ArrayList<Players>) request.getObject();

                    Log.d(Global.TAG, "Jugadores recibidos: " + listPlayerFriends.size());
                    for (Players p : listPlayerFriends) {
                        Log.d(Global.TAG, "Jugador recibido: " + p.getUser().getUsername());
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterPlayerFriendsList = new AdapterPlayersList(listPlayerFriends);
                            rw_friends.setAdapter(adapterPlayerFriendsList);
                            adapterPlayerFriendsList.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (listPlayerFriends.size() < Global.MAX_GAMERS) {
                                        boolean idFound = false;
                                        for (Players p : listPlayersSelected) {
                                            if (listPlayerFriends.get(rw_friends.getChildAdapterPosition(v)).getId_player() == p.getId_player()) {
                                                idFound = true;
                                            }
                                        }
                                        if (!idFound) {
                                            listPlayersSelected.add(listPlayerFriends.get(rw_friends.getChildAdapterPosition(v)));
                                            adapterPlayersSelectedList.notifyItemInserted(listPlayersSelected.size() + 1);
                                            txtNumberOfGamers.setText(String.valueOf(listPlayersSelected.size()));
                                        } else {
                                            Utils.showSnack(getView(), getString(R.string.player_already_in_the_match), Snackbar.LENGTH_LONG);
                                        }
                                    } else {
                                        Utils.showSnack(getView(), getString(R.string.match_is_complete), Snackbar.LENGTH_LONG);
                                    }
                                }
                            });
                        }
                    });

                    break;

                case Global.LIST_GOLF_GAME_COURSE:
                    request = (Message) arg;
                    listCourses = (ArrayList<Golf_Courses>) request.getObject();

                    Log.d(Global.TAG, "Recorridos recibidos: " + listCourses.size());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterCoursesList = new AdapterCoursesList(listCourses, getContext());
                            rw_courses.setAdapter(adapterCoursesList);
                            adapterCoursesList.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showSelectPlayer();
                                    Log.d(Global.TAG, String.valueOf(listCourses.get(rw_courses.getChildAdapterPosition(v))));
                                    txtCourseSelected.setText(String.valueOf(listCourses.get(rw_courses.getChildAdapterPosition(v)).getGolf_course()));
                                    rw_courses.setVisibility(View.GONE);
                                    isCourseSelected(true);
                                    courseSelected = listCourses.get(rw_courses.getChildAdapterPosition(v));


                                }
                            });
                        }
                    });
                    break;

                case Global.ADD_GOLF_GAME:

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showSnack(getView(), "Juego creado con éxito", Snackbar.LENGTH_LONG);
                            Fragment fragment = new Principal();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

                        }
                    });
                    break;
            }
        }
    }

    /**
     * <b>Envia el mensaje para cargar los tipos de juego</b><br>
     * Mensaje = (token¬device, listGolfGameType, null, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadGameType() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME_TYPE, null, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia el mensaje para cargar listado de amigos</b><br>
     * Mensaje = (token¬device, listActiveUsers, id usuario, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadFriendsWithThisGamer() {
        String idPlayer = String.valueOf(Global.activePlayer.getId_player());
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME_PLAYER, idPlayer, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia el mensaje para cargar los recorridos</b><br>
     * Mensaje = (token¬device, listGolfCourse, null, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadCoursesRelated() {
        String idPlayerType = String.valueOf(Global.activePlayer.getPlayer_type().getId_player_type());
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME_COURSE, idPlayerType, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }


    private void sendNewGame() {

        createListGolfGamers();

        DateTimeFormatter dtfHour = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyyMMdd");
        String hourGame = dtfHour.format(LocalDateTime.now());
        int dateGame = Integer.parseInt(dtfDate.format(LocalDateTime.now()));

        Log.d(Global.TAG, hourGame + " - " + dateGame);

        Golf_Games newGame = new Golf_Games(0, courseSelected, golfGameTypesSelected, null, dateGame, hourGame, 0, Global.CREATE, golfGamePlayersSend);
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.ADD_GOLF_GAME, Integer.toString(Global.activePlayer.getId_player()), newGame);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    private void createListGolfGamers() {
        for (Players p : listPlayersSelected) {
            Golf_Game_Players ggp = new Golf_Game_Players(0, p, 0, 0);
            golfGamePlayersSend.add(ggp);
        }
    }

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder.getAbsoluteAdapterPosition() != 0) {
                listPlayersSelected.remove(viewHolder.getAdapterPosition());
                adapterPlayersSelectedList.notifyDataSetChanged();
                txtNumberOfGamers.setText(String.valueOf(listPlayersSelected.size()));
            } else {
                adapterPlayersSelectedList.notifyDataSetChanged();
                Utils.showSnack(getView(), getString(R.string.host_player_cannot_be_removed), Snackbar.LENGTH_LONG);
            }
        }
    };

    /**
     * Segun si ha sido o no seleccionado el recorrido muestra u oculta otros paneles
     *
     * @param isSelected true si el recorrido ha sido seleccionado
     * @author Antonio Rodríguez Sirgado
     */
    public void isCourseSelected(boolean isSelected) {
        if (isSelected) {
            deleteCourse.setVisibility(View.VISIBLE);
            cv_courseSelected.setCardBackgroundColor(getResources().getColor(R.color.green));
            searchCoursesList.setVisibility(View.GONE);
            rw_courses.setVisibility(View.GONE);
        } else {
            txtCourseSelected.setText("Select course");
            deleteCourse.setVisibility(View.GONE);
            cv_courseSelected.setCardBackgroundColor(getResources().getColor(R.color.grey));
            searchCoursesList.setVisibility(View.VISIBLE);
            rw_courses.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Muestra los campos de los jugadores
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void showSelectPlayer() {
        rw_friends.setVisibility(View.VISIBLE);
        cv_number.setVisibility(View.VISIBLE);
        cv_players.setVisibility(View.VISIBLE);
        cv_playersTitle.setVisibility(View.VISIBLE);
        cv_selectPlayer.setVisibility(View.VISIBLE);
        searchFriendsList.setVisibility(View.VISIBLE);
    }
}