package proyecto.golfus.forat19.ui.add;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView numberOfGamers, courseSelected;
    private Spinner courseType, gameType;
    private List<String> listCourseType = new ArrayList<String>();
    private List<String> listGameType = new ArrayList<String>();
    private ArrayList<Users> listFriends;
    private ArrayList<Golf_Courses> listCourses;
    private ArrayList<Users> listPlayers= new ArrayList<Users>();
    private SearchView searchFriendsList, searchCoursesList;
    private RecyclerView rw_friends, rw_courses, rw_players;
    private AdapterNormalUsersList adapterNormalUsersList;
    private AdapterCoursesList adapterCoursesList;
    private AdapterPlayersList adapterPlayersList;

    private Message request;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddGame() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddGame.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGame newInstance(String param1, String param2) {
        AddGame fragment = new AddGame();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        searchFriendsList = view.findViewById(R.id.newGame_searchFriend);
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

        searchCoursesList = view.findViewById(R.id.newGame_searchCourse);
        searchCoursesList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapterCoursesList.filter(newText);
                return false;
            }
        });

        numberOfGamers = view.findViewById(R.id.createGame_number);
        numberOfGamers.setText(String.valueOf(listPlayers.size()));
        courseSelected=view.findViewById(R.id.createGame_selectCourse);

        rw_friends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rw_courses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rw_players.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapterPlayersList = new AdapterPlayersList(listPlayers);
        rw_players.setAdapter(adapterPlayersList);

        searchCoursesList.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rw_courses.setVisibility(View.VISIBLE);
            }
        });

        courseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                            adapterNormalUsersList = new AdapterNormalUsersList(listFriends);
                            rw_friends.setAdapter(adapterNormalUsersList);
                            adapterNormalUsersList.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listPlayers.add(listFriends.get(rw_friends.getChildAdapterPosition(v)));
                                    adapterPlayersList.notifyItemInserted(listPlayers.size()+1);
                                    numberOfGamers.setText(String.valueOf(listPlayers.size()));
                                }
                            });
                        }
                    });

                    break;

                case Global.LIST_GOLF_COURSES:
                    request = (Message) arg;
                    listCourses = (ArrayList<Golf_Courses>) request.getObject();

                    Log.d(Global.TAG,"Recorridos recibidos: " + listCourses.size());


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterCoursesList = new AdapterCoursesList(listCourses);
                            rw_courses.setAdapter(adapterCoursesList);
                            adapterCoursesList.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(Global.TAG, String.valueOf(listCourses.get(rw_courses.getChildAdapterPosition(v))));
                                    courseSelected.setText(String.valueOf(listCourses.get(rw_courses.getChildAdapterPosition(v)).getGolf_course()));
                                    rw_courses.setVisibility(View.GONE);

                                }
                            });
                        }
                    });

                    break;
            }
        }
    }

    public void loadCourseType() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_COURSES_TYPE, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    public void loadGameType() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_GAME_TYPE, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    public void loadFriends() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_ACTIVE_USERS, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    public void loadCourses(){
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_GOLF_COURSES, null, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }


}