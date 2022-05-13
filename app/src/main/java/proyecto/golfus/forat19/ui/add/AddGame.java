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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Course_Types;
import Forat19.Golf_Courses;
import Forat19.Golf_Game_Types;
import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.adapterList.AdapterCoursesList;
import proyecto.golfus.forat19.adapterList.AdapterPlayersList;
import proyecto.golfus.forat19.adapterList.AdapterNormalUsersList;
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
    private TextView numberOfGamers, courseSelected;
    private Spinner courseType, gameType;
    private List<String> listCourseType = new ArrayList<String>();
    private List<String> listGameType = new ArrayList<String>();
    private ArrayList<Users> listFriends;
    private ArrayList<Golf_Courses> listCourses;
    private ArrayList<Users> listPlayers = new ArrayList<Users>();
    private SearchView searchFriendsList, searchCoursesList;
    private RecyclerView rw_friends, rw_courses, rw_players;
    private AdapterNormalUsersList adapterNormalUsersList;
    private AdapterCoursesList adapterCoursesList;
    private AdapterPlayersList adapterPlayersList;
    private CardView cv_courseSelected, cv_players, cv_playersTitle, cv_selectPlayer, cv_number;

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
        courseType = view.findViewById(R.id.createGame_courseType);
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
        numberOfGamers = view.findViewById(R.id.createGame_number);
        courseSelected = view.findViewById(R.id.createGame_selectCourse);

        // Boton crear juego
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPlayers.size() > 1) {

                } else {
                    Utils.showSnack(getView(), getString(R.string.not_enough_gamers), Snackbar.LENGTH_LONG);
                }
            }
        });

        // Boton eliminar recorrido actual
        deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseSelected(false);
            }
        });

        searchFriendsList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterNormalUsersList.filter(newText);
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

        numberOfGamers.setText(String.valueOf(listPlayers.size()));

        rw_friends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rw_courses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rw_players.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        new ItemTouchHelper(itemTouch).attachToRecyclerView(rw_players);
        adapterPlayersList = new AdapterPlayersList(listPlayers);

        rw_players.setAdapter(adapterPlayersList);

        // Campo de busqueda de recorridos
        searchCoursesList.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Spinner de seleccion de tipo de recorrido
        courseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinner de seleccion de tipo de juego
        gameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadGameType();
        loadCourseType();
        loadFriends();
        loadCourses();
        loadMyAccount();

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
                case Global.LIST_GOLF_COURSES_TYPE:
                    ArrayList<Golf_Course_Types> objectCourseTypes = (ArrayList<Golf_Course_Types>) request.getObject();

                    Log.d(Global.TAG, "Tipos de recorridos recibidos: " + objectCourseTypes.size());

                    for (int i = 0; i < objectCourseTypes.size(); i++) {
                        listCourseType.add(objectCourseTypes.get(i).getGolf_course_type());
                        Log.d(Global.TAG, "Tipo recorrido: " + objectCourseTypes.get(i).getGolf_course_type());
                    }
                    Log.d(Global.TAG, "-------------------------------------------------");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, listCourseType);
                            courseType.setAdapter(adapter);

                        }
                    });
                    break;
                case Global.LIST_GOLF_GAME_TYPE:
                    ArrayList<Golf_Game_Types> objectGameTypes = (ArrayList<Golf_Game_Types>) request.getObject();

                    Log.d(Global.TAG, "Tipos de juego recibidos: " + objectGameTypes.size());

                    for (int i = 0; i < objectGameTypes.size(); i++) {
                        listGameType.add(objectGameTypes.get(i).getGolf_game_type());
                        Log.d(Global.TAG, "Tipo de juego: " + objectGameTypes.get(i).getGolf_game_type());
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
                case Global.LIST_ACTIVE_USERS:

                    request = (Message) arg;
                    listFriends = (ArrayList<Users>) request.getObject();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterNormalUsersList = new AdapterNormalUsersList(listFriends, getContext());
                            rw_friends.setAdapter(adapterNormalUsersList);
                            adapterNormalUsersList.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (listPlayers.size() < Global.MAX_GAMERS) {
                                        boolean idFound = false;
                                        for (Users u : listPlayers) {
                                            if (listFriends.get(rw_friends.getChildAdapterPosition(v)).getId_user() == u.getId_user()) {
                                                idFound = true;
                                            }
                                        }
                                        if (!idFound) {
                                            listPlayers.add(listFriends.get(rw_friends.getChildAdapterPosition(v)));
                                            adapterPlayersList.notifyItemInserted(listPlayers.size() + 1);
                                            numberOfGamers.setText(String.valueOf(listPlayers.size()));
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

                case Global.LIST_GOLF_COURSES:
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
                                    courseSelected.setText(String.valueOf(listCourses.get(rw_courses.getChildAdapterPosition(v)).getGolf_course()));
                                    rw_courses.setVisibility(View.GONE);
                                    courseSelected(true);

                                }
                            });
                        }
                    });
                    break;

                case Global.GET_USER:
                    request = (Message) arg;
                    listPlayers.add((Users) request.getObject());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterPlayersList.notifyItemInserted(listPlayers.size() + 1);
                            numberOfGamers.setText(String.valueOf(listPlayers.size()));
                        }
                    });
                    break;
            }
        }
    }

    /**
     * <b>Envia el mensaje para cargar los tipos de recorrido</b><br>
     * Mensaje = (token¬device, listGolfCourseType, id usuario, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadCourseType() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_COURSES_TYPE, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia el mensaje para cargar los tipos de juego</b><br>
     * Mensaje = (token¬device, listGolfGameType, id usuario, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadGameType() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME_TYPE, Utils.getActiveId(getActivity()), null);
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
    public void loadFriends() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_ACTIVE_USERS, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia el mensaje para cargar los recorridos</b><br>
     * Mensaje = (token¬device, listGolfCourse, id usuario, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadCourses() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_COURSES, null, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia el mensaje para cargar datos del usuario actual</b><br>
     * Mensaje = (token¬device, getUser, id usuario, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadMyAccount() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.GET_USER, Utils.getActiveId(getActivity()), null);
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
            if (viewHolder.getAbsoluteAdapterPosition() != 0) {
                listPlayers.remove(viewHolder.getAdapterPosition());
                adapterPlayersList.notifyDataSetChanged();
                numberOfGamers.setText(String.valueOf(listPlayers.size()));
            } else {
                adapterPlayersList.notifyDataSetChanged();
                Utils.showSnack(getView(), getString(R.string.host_player_cannot_be_removed), Snackbar.LENGTH_LONG);
            }
        }
    };

    /**
     * Segun si ha sido o no seleccionado el recorrido muestra u oculta otros paneles
     * @author Antonio Rodríguez Sirgado
     * @param isSelected true si el recorrido ha sido seleccionado
     */
    public void courseSelected(boolean isSelected) {
        if (isSelected) {
            deleteCourse.setVisibility(View.VISIBLE);
            cv_courseSelected.setCardBackgroundColor(getResources().getColor(R.color.green));
            searchCoursesList.setVisibility(View.GONE);
            rw_courses.setVisibility(View.GONE);
        } else {
            courseSelected.setText("Select course");
            deleteCourse.setVisibility(View.GONE);
            cv_courseSelected.setCardBackgroundColor(getResources().getColor(R.color.grey));
            searchCoursesList.setVisibility(View.VISIBLE);
            rw_courses.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Muestra los campos de los jugadores
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