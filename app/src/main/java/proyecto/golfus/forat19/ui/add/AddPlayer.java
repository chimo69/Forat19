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
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Player_Data;
import Forat19.Player_Information;
import Forat19.Player_Types;
import Forat19.Players;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.adapterList.AdapterDataList;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para crear un jugador de un tipo de juego
 *
 * @author Antonio Rodríguez Sirgado
 */
public class AddPlayer extends Fragment implements Observer {

    private Button btn_createPlayer;
    private Spinner sp_playerType;
    private Message request;
    private List<String> listPlayerTypes = new ArrayList<String>();
    private int playerTypeSelected;
    private ArrayList<Player_Types> objectPlayerTypes;
    private Player_Types playerTypesSelected;
    private ArrayList<Player_Data> playerData;
    private ArrayList<String> listPlayerData = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdapterDataList adapterDataList;

    public AddPlayer() {
    }

    public static AddPlayer newInstance() {
        AddPlayer fragment = new AddPlayer();
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
        View view = inflater.inflate(R.layout.fragment_add_player, container, false);
        sp_playerType = view.findViewById(R.id.sp_addPlayer_selectGameType);
        recyclerView = view.findViewById(R.id.rv_addPlayer_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        btn_createPlayer = view.findViewById(R.id.btn_addPlayer_addPlayer);
        btn_createPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataPlayers();
            }
        });
        loadPlayerTypes();

        //Comobox de tipos de jugador
        sp_playerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                playerTypeSelected = objectPlayerTypes.get(position).getId_player_type();
                playerTypesSelected = objectPlayerTypes.get(position);
                Log.d(Global.TAG, "Tipo jugador seleccionado: " + playerTypeSelected + " - " + objectPlayerTypes.get(position).getPlayer_type());
                loadDataPlayer();
                listPlayerData.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                        case Global.LIST_PLAYER_TYPE:
                            if (parameter.equals(Global.OK)) {
                                objectPlayerTypes = (ArrayList<Player_Types>) request.getObject();

                                Log.d(Global.TAG, "Tipos de juegadores recibidos: " + objectPlayerTypes.size());

                                for (int i = 0; i < objectPlayerTypes.size(); i++) {
                                    listPlayerTypes.add(objectPlayerTypes.get(i).getPlayer_type());
                                    Log.d(Global.TAG, "Tipo jugador: " + objectPlayerTypes.get(i).getPlayer_type());
                                }
                                Log.d(Global.TAG, "-------------------------------------------------");

                                ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, listPlayerTypes);
                                sp_playerType.setAdapter(adapter);
                            }
                            break;
                        case Global.LIST_PLAYER_DATA:
                            if (parameter.equals(Global.OK)) {
                                playerData = (ArrayList<Player_Data>) request.getObject();

                                for (Player_Data p : (ArrayList<Player_Data>) request.getObject()) {
                                    listPlayerData.add(p.getPlayer_data());
                                }
                                adapterDataList = new AdapterDataList(listPlayerData);
                                recyclerView.setAdapter(adapterDataList);
                            }
                            break;
                        case Global.ADD_PLAYER:
                            Fragment fragment = new Principal();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                            break;
                    }
                }
            }
        });

    }

    /**
     * <b>Lanza mensaje para la carga de tipos de jugador</b><br>
     * Mensaje = (token¬device, ListPlayerType, null, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadPlayerTypes() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_PLAYER_TYPE, null, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Lanza mensaje para la carga de informacion a rellenar por tipo de jugador</b><br>
     * Mensaje = (token¬device, ListPlayerData, tipo de jugador seleccionado, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadDataPlayer() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_PLAYER_DATA, Integer.toString(playerTypeSelected), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Rellena el DataPlayer con todos los PlayerInformation y envia el mensaje al servidor</b><br>
     * Mensaje = (token¬device, AddPlayer, null, new Player)
     *
     * @author Antonio Rodriguez Sirgado
     */
    private void sendDataPlayers() {
        String[] dataEntries = adapterDataList.getDataEntries();
        List<Player_Information> listData = new ArrayList<>();

        for (int i = 0; i < playerData.size(); i++) {
            Player_Information playerInformation = new Player_Information(Integer.parseInt(Utils.getActiveId(getActivity())), playerData.get(i), dataEntries[i]);
            listData.add(playerInformation);
        }

        Players newPlayer = new Players(0, playerTypesSelected, Global.activeUser, null, listData);

        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.ADD_PLAYER, null, newPlayer);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);

    }
}