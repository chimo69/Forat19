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

    private Button createPlayer;
    private Spinner playerType;
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
        playerType = view.findViewById(R.id.createPlayer_GameType);
        recyclerView = view.findViewById(R.id.recyclerListDataPlayer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        createPlayer = view.findViewById(R.id.btn_createPlayer);
        createPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataPlayers();
            }
        });
        loadPlayerTypes();

        playerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                playerTypeSelected =  objectPlayerTypes.get(position).getId_player_type();
                playerTypesSelected = objectPlayerTypes.get(position);
                Log.d(Global.TAG,"Tipo jugador seleccionado: "+playerTypeSelected+" - "+ objectPlayerTypes.get(position).getPlayer_type());
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
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
        } else {

            request = (Message) arg;

            if (request.getCommand().equals(Global.LIST_PLAYER_TYPE)) {

                objectPlayerTypes =  (ArrayList<Player_Types>)request.getObject();

                Log.d(Global.TAG, "Tipos de juegadores recibidos: " + objectPlayerTypes.size());

                for (int i = 0; i < objectPlayerTypes.size(); i++) {
                    listPlayerTypes.add(objectPlayerTypes.get(i).getPlayer_type());
                    Log.d(Global.TAG, "Tipo jugador: " + objectPlayerTypes.get(i).getPlayer_type());
                }
                Log.d(Global.TAG,"-------------------------------------------------");
               getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, listPlayerTypes);
                        playerType.setAdapter(adapter);

                    }
                });
            } else if (request.getCommand().equals(Global.LIST_PLAYER_DATA)){

                playerData = (ArrayList<Player_Data>) request.getObject();

                for (Player_Data p: (ArrayList<Player_Data>)request.getObject()){
                    listPlayerData.add (p.getPlayer_data());
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterDataList = new AdapterDataList(listPlayerData);
                        recyclerView.setAdapter(adapterDataList);
                    }
                });
            } else if (request.getCommand().equals(Global.ADD_PLAYER)){
                Fragment fragment = new Principal();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            }
        }
    }

    /**
     * Lanza mensaje para la carga de tipos de jugador
     * @author Antonio Rodríguez Sirgado
     */
    public void loadPlayerTypes() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_PLAYER_TYPE, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * Lanza mensaje para la carga de informacion a rellenar por tipo de jugador
     * @author Antonio Rodríguez Sirgado
     */
    public void loadDataPlayer(){
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_PLAYER_DATA, Integer.toString(playerTypeSelected), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * Rellena el DataPlayer con todos los PlayerInformation y envia el mensaje al servidor
     * @author Antonio Rodriguez Sirgado
     */
    private void sendDataPlayers() {
        String[] dataEntries = adapterDataList.getDataEntries();
        List <Player_Information> information = new ArrayList<>();
        List<Player_Information> listData = new ArrayList<>();

        for (int i =0; i <playerData.size();i++){
            Player_Information playerInformation = new Player_Information(Integer.parseInt(Utils.getActiveId(getActivity())), playerData.get(i), dataEntries[i]);
            listData.add(playerInformation);
        }

        Players newPlayer = new Players(0, playerTypesSelected, Global.activeUser,null, listData);

        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.ADD_PLAYER, null, newPlayer);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);

    }
}