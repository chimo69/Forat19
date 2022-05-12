package proyecto.golfus.forat19.ui.add;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import Forat19.Player_Types;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para crear un jugador de un tipo de juego
 *
 * @author Antonio Rodríguez Sirgado
 */
public class AddPlayer extends Fragment implements Observer {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button createPlayer;
    private Spinner playerType;
    private Message request;
    private List<String> listPlayerTypes = new ArrayList<String>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddPlayer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPlayer.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPlayer newInstance(String param1, String param2) {
        AddPlayer fragment = new AddPlayer();
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
        View view = inflater.inflate(R.layout.fragment_add_player, container, false);
        playerType = view.findViewById(R.id.createPlayer_GameType);



        loadPlayerTypes();
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

                ArrayList<Player_Types> objectPlayerTypes =  (ArrayList<Player_Types>)request.getObject();

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
        /*Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_PLAYER_TYPE, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);*/
    }
}