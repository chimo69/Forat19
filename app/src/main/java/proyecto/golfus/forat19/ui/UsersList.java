package proyecto.golfus.forat19.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.adapterList.AdapterList;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;


/**
 * Pantalla que muestra el listado de usuarios
 * @author Antonio Rodríguez Sirgado
 */
public class UsersList extends Fragment implements Observer {

    private SharedPreferences preferences;
    private Message request;
    private ArrayList<Users> listUsers;
    private RecyclerView recyclerView;
    private Button btn_allUsers, btn_activeUsers, btn_inactiveUsers;
    private static ProgressBar loading;

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
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerListUsers);
        btn_allUsers = view.findViewById(R.id.btn_allUsers);
        btn_activeUsers = view.findViewById(R.id.btn_ActiveUsers);
        btn_inactiveUsers = view.findViewById(R.id.btn_InactiveUsers);
        loading = view.findViewById(R.id.userlist_loading);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        btn_allUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                loadUsers(Global.LIST_ALL_USERS);
            }
        });
        btn_activeUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                loadUsers(Global.LIST_ACTIVE_USERS);
            }
        });
        btn_inactiveUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                loadUsers(Global.LIST_INACTIVE_USERS);
            }
        });

        loading.setVisibility(View.VISIBLE);
        loadUsers(Global.LIST_ALL_USERS);
        return view;
    }

    /**
     * <b>Carga la lista de usuarios del servidor</b><br><br>
     * LIST_ALL_USERS: muestra todos los usuarios<br>
     * LIST_ACTIVE_USERS: muestra usuarios activos<br>
     * LIST_INACTIVE_USERS: muestra usuarios inactivos<br>
     * Mensaje = (token¬device,typeList, Id, null)
     *
     * @param typeList tipo de lista a mostrar
     *
     */
    public void loadUsers(String typeList){
        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);

        Message message = new Message(activeToken + "¬" + Utils.getDevice(requireContext()), typeList, Integer.toString(activeID), null);
        Log.d("INFO", "Enviando token: " + activeToken);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }


    /**
     * Permanece a la espera de que las variables cambien
     * @author Antonio Rodriguez Sirgado
     * @param o la clase observada
     * @param arg objeto observado
     */
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

            listUsers = (ArrayList<Users>) request.getObject();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AdapterList adapterList = new AdapterList(listUsers);
                    recyclerView.setAdapter(adapterList);
                }
            });

            UsersList.loading.post(() -> UsersList.loading.setVisibility(View.INVISIBLE));


        }
    }
}