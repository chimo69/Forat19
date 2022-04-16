package proyecto.golfus.forat19;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.ui.PrincipalFragment;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;


public class UsersList extends Fragment implements Observer {

    private SharedPreferences preferences;
    private Message request;

    public UsersList() {

    }

    public static UsersList newInstance(String param1, String param2) {
        UsersList fragment = new UsersList();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getActivity().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loadUsers();
        return inflater.inflate(R.layout.fragment_users_list, container, false);
    }

    public void loadUsers(){
        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);

        Message message = new Message(activeToken + "¬" + Utils.getDevice(requireContext()), "ListUser*", Integer.toString(activeID), null);
        Log.d("INFO", "Enviando token: " + activeToken);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }


    @Override
    public void update(Observable o, Object arg) {

        // comprueba si ha recibido un objeto Reply que será un error de conexión
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            Fragment fragment = new PrincipalFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {
            request = (Message) arg;
            String command = request.getCommand();

            Log.d("INFO", "Token recibido: " + request.getToken());
            Log.d("INFO", "Parametros recibido: " + request.getParameters());
            Log.d("INFO", "Comando recibido: " + request.getCommand());

            List<Users> listUsers= new ArrayList<Users>();

            listUsers = (List<Users>) request.getObject();

            for (int i=0; i< listUsers.size();i++){
                Log.d("INFO","Usuario"+listUsers.get(i).getUsername());
            }
        }
    }
}