package proyecto.golfus.forat19.ui.screens;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Player_Data;
import Forat19.Player_Information;
import Forat19.Players;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.adapterList.AdapterDataList;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

public class Player extends Fragment implements Observer {


    private Players players;
    private Players player;
    private Button update, delete;
    private RecyclerView recyclerView;
    private AdapterDataList adapterDataList;
    private Message request;
    private ArrayList<Player_Information> playerInformationList;
    private ArrayList<String> listPlayerData = new ArrayList<>();


    public Player() {

    }

    public Player(Players players) {
        this.players = players;
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
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        update = view.findViewById(R.id.btn_updatePlayer);
        recyclerView = view.findViewById(R.id.rv_players);
        update = view.findViewById(R.id.btn_updatePlayer);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayer();
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if (players != null) {
            Log.d(Global.TAG, "Tipo jugador recibido: " + players.getPlayer_type().getPlayer_type());
            loadPlayerInformation();
        }
        return view;
    }

    private void updatePlayer() {
        String[] dataEntries = adapterDataList.getDataEntries();
        List<Player_Information> listData = new ArrayList<>();

        for (int i = 0; i < listPlayerData.size(); i++) {
            Player_Information playerInformation = new Player_Information(Integer.parseInt(Utils.getActiveId(getActivity())), player.getPlayer_information().get(i).getPlayer_data(), dataEntries[i]);
            listData.add(playerInformation);
        }

        Players playerUpdated = new Players(player.getId_player(), player.getPlayer_type(), Global.activeUser, player.getActive(), listData);

        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.UPDATE_PLAYER, null, playerUpdated);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * Lanza mensaje para la carga de informacion a rellenar por tipo de jugador
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadPlayerInformation() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.GET_PLAYER, Integer.toString(players.getId_player()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
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
                    String command = request.getCommand();
                    String parameter = request.getParameters();

                    switch (command) {
                        case Global.GET_PLAYER:
                            if (parameter.equals(Global.OK)){
                                player = (Players) request.getObject();
                                playerInformationList = (ArrayList<Player_Information>) player.getPlayer_information();
                                String information[] = new String[playerInformationList.size()];

                                for (int i = 0; i < playerInformationList.size(); i++) {
                                    information[i] = playerInformationList.get(i).getValue();
                                    listPlayerData.add(playerInformationList.get(i).getPlayer_data().getPlayer_data());
                                    Log.d(Global.TAG, "Info: " + information[i]);
                                }

                                adapterDataList = new AdapterDataList(listPlayerData);
                                adapterDataList.setDataEntries(information);

                                recyclerView.setAdapter(adapterDataList);
                            }
                            break;
                        case Global.UPDATE_PLAYER:
                            if (parameter.equals(Global.OK)){
                                Utils.showSnack(getView(), "Player succesfully upgraded", Snackbar.LENGTH_LONG);
                                Fragment fragment = new MyAccount();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                                break;
                            }
                    }
                }
            }
        });
    }
}