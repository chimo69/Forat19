package proyecto.golfus.forat19.ui.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.adapterList.AdapterCoursesList;
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

    private Golf_Games game;
    private Button createGame, changeDate, changeHour;
    private ImageButton deleteCourse;
    private TextView txtNumberOfGamers, txtCourseSelected, txtTitle, txtHour, txtDate;
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
    private Boolean update;
    private int dayGame, monthGame, yearGame, hourGame, minutesGame;

    private Message request;

    public AddGame() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            game = (Golf_Games) getArguments().getSerializable("game");
            update = true;
        } else {
            update = false;
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
        changeDate = view.findViewById(R.id.btn_addGame_changeData);
        changeHour = view.findViewById(R.id.btn_addGame_ChangeHour);
        searchFriendsList = view.findViewById(R.id.newGame_searchFriend);
        searchCoursesList = view.findViewById(R.id.newGame_searchCourse);
        txtNumberOfGamers = view.findViewById(R.id.createGame_number);
        txtCourseSelected = view.findViewById(R.id.createGame_selectCourse);
        txtTitle = view.findViewById(R.id.txt_addGame_title);
        txtHour = view.findViewById(R.id.txt_addGame_hour);
        txtDate = view.findViewById(R.id.txt_addGame_date);

        listPlayersSelected = new ArrayList<>();

        // Boton cambio de fecha
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dayGame = Utils.dateToInt(game.getGame_date(), "d");
                monthGame = Utils.dateToInt(game.getGame_date(), "m");
                yearGame = Utils.dateToInt(game.getGame_date(), "y");

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String monthTxt, dayOfMonthTxt;
                            month++;
                            if (month<10){
                                monthTxt = "0" + month;
                            }else{
                                monthTxt = String.valueOf(month);
                            }
                            if (dayOfMonth<10){
                                dayOfMonthTxt = "0" + dayOfMonth;
                            }else{
                                dayOfMonthTxt = String.valueOf(dayOfMonth);
                            }

                            txtDate.setText(dayOfMonthTxt + "/" + monthTxt + "/" + year);
                    }
                }, yearGame, monthGame, dayGame);

                datePickerDialog.show();
            }
        });

        // Boton cambio de hora
        changeHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hourGame = Utils.timeToInt(game.getGame_hour(), "h");
                minutesGame = Utils.timeToInt(game.getGame_hour(), "m");

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtHour.setText(hourOfDay + ":" + minute);
                    }
                }, hourGame, minutesGame, true);
                timePickerDialog.show();
            }
        });

        // Boton de seleccion de tipo de juego
        gameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                golfGameTypesSelected = golfGameTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Boton crear juego
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPlayersSelected.size() > 0) {
                    if (update) {
                        sendUpdateGame();
                    } else {
                        sendNewGame();
                    }
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

        // Campo de busqueda de recorridos
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

        // Campo de busqueda de jugadores
        searchFriendsList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterPlayerFriendsList.filter(newText);
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

        loadGameType();
        loadFriendsWithThisGamer();

        if (update) {
            createGame.setText(R.string.update_game);
            txtCourseSelected.setText(game.getGolf_course().getGolf_course());
            rw_courses.setVisibility(View.GONE);
            courseSelected = game.getGolf_course();
            isCourseSelected(true);
            deleteCourse.setVisibility(View.INVISIBLE);
            txtTitle.setText(R.string.update_game);
            golfGameTypesSelected = game.getGolf_game_type();
            for (Golf_Game_Players g : game.getGolf_game_players()) {
                listPlayersSelected.add(g.getPlayer());
            }
            txtDate.setText(Utils.changeDateFormat(game.getGame_date()));
            txtHour.setText(game.getGame_hour());

            showSelectPlayer();
        } else {
            txtHour.setVisibility(View.GONE);
            txtDate.setVisibility(View.GONE);
            changeHour.setVisibility(View.GONE);
            changeDate.setVisibility(View.GONE);
            loadCoursesRelated();
            listPlayersSelected.add(Global.activePlayer);
        }


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

        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (arg instanceof Reply) {
                    Utils.showSnack(getView(), ((Reply) arg).getTypeError(), Snackbar.LENGTH_LONG);
                } else {
                    request = (Message) arg;
                    String command = ((Message) arg).getCommand();
                    String parameter = ((Message) arg).getParameters();

                    switch (command) {

                        case Global.LIST_GOLF_GAME_TYPE:

                            if (parameter.equals(Global.OK)) {
                                golfGameTypes = (ArrayList<Golf_Game_Types>) request.getObject();
                                Log.d(Global.TAG, "Tipos de juego recibidos: " + golfGameTypes.size());

                                for (int i = 0; i < golfGameTypes.size(); i++) {
                                    listGameType.add(golfGameTypes.get(i).getGolf_game_type());
                                    Log.d(Global.TAG, "Tipo de juego: " + golfGameTypes.get(i).getGolf_game_type());
                                }
                                Log.d(Global.TAG, "-------------------------------------------------");

                                ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, listGameType);
                                gameType.setAdapter(adapter);

                                if (update) {
                                    gameType.setSelection(listGameType.indexOf(game.getGolf_game_type().getGolf_game_type()));
                                    gameType.setEnabled(false);
                                }
                            }

                            break;
                        case Global.LIST_GOLF_GAME_PLAYER:

                            if (parameter.equals(Global.OK)) {
                                listPlayerFriends = (ArrayList<Players>) request.getObject();

                                Log.d(Global.TAG, "Jugadores recibidos: " + listPlayerFriends.size());
                                for (Players p : listPlayerFriends) {
                                    Log.d(Global.TAG, "Jugador recibido: " + p.getUser().getUsername());
                                }

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

                            break;

                        case Global.LIST_GOLF_GAME_COURSE:

                            if (parameter.equals(Global.OK)) {
                                listCourses = (ArrayList<Golf_Courses>) request.getObject();

                                Log.d(Global.TAG, "Recorridos recibidos: " + listCourses.size());

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
                            break;
                        case Global.ADD_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                Utils.showSnack(getView(), "Juego creado con éxito", Snackbar.LENGTH_LONG);
                                Fragment fragment = new Principal();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                            }
                            break;
                        case Global.UPDATE_GOLF_GAME:
                            if (parameter.equals(Global.OK)) {
                                Utils.showSnack(getView(), "Juego actualizado con éxito", Snackbar.LENGTH_LONG);
                                Fragment fragment = new Principal();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                            }
                            break;
                    }
                }
            }
        });
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


    /**
     * <b>Envia el mensaje para guardar el nuevo partido</b><br>
     * Mensaje = (token¬device, addGolfGame, id Player, new game)
     *
     * @author Antonio Rodríguez Sirgado
     */
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

    private void sendUpdateGame() {
        createListGolfGamers();

        String hourGame = txtHour.getText().toString();
        int dateGame = Utils.changeDateFormat(txtDate.getText().toString());

        Golf_Games updatedGame = new Golf_Games(game.getId_golf_game(), courseSelected, golfGameTypesSelected, null, dateGame, hourGame, 0, Global.CREATE, golfGamePlayersSend);
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.UPDATE_GOLF_GAME, Integer.toString(Global.activePlayer.getId_player()), updatedGame);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * Crea un objeto Golf_Game_players con todos los jugadores para crear una partida
     */
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