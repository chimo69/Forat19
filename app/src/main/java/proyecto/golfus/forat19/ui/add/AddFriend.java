package proyecto.golfus.forat19.ui.add;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.User_Relationships;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.adapterList.AdapterNormalUsersList;
import proyecto.golfus.forat19.ui.screens.MyAccount;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.ui.update.UpdateUserAdmin;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Pantalla para añadir amistades mediante listado o codigo QR
 *
 * @author Antonio Rodríguez Sirgado
 */
public class AddFriend extends Fragment implements Observer, SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private SearchView searchUserList;
    private AdapterNormalUsersList adapterList;
    private Message request;
    private ArrayList<Users> listUsers;
    private Button btnCodeQR;

    public AddFriend() {
        // Required empty public constructor
    }

    public static AddFriend newInstance(String param1, String param2) {
        AddFriend fragment = new AddFriend();
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
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        recyclerView = view.findViewById(R.id.RecyclerNormalListUser);
        searchUserList = view.findViewById(R.id.searchNormalUserList);
        btnCodeQR = view.findViewById(R.id.btn_qrcode);

        searchUserList.setOnQueryTextListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        loadUsers(Global.LIST_ACTIVE_USERS);


        // boton agregar amistad por QR
        btnCodeQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator.forSupportFragment(AddFriend.this)
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        .setBeepEnabled(true)
                        .setPrompt("hola que tal")
                        .initiateScan();

                Log.d(Global.TAG, "Camara iniciada");
            }
        });

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
     */
    public void loadUsers(String typeList) {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(requireContext()), typeList, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    public void loadFutureFriend(String user) {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(requireContext()), Global.GET_USER, user, null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    public void sendNewFriend(Users user, Boolean isChecked) {
        String checked = null;
        if (isChecked) {checked=Global.CHECKED;}
        User_Relationships userRelationships = new User_Relationships(Global.activeUser, user, "Y");
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(requireContext()), Global.ADD_USER_RELATIONSHIP, checked, userRelationships);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterList.filter(newText);
        return false;
    }

    /**
     * Recibe el resultado del intent del lector de QR
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        loadFutureFriend(result.getContents());
        Log.d(Global.TAG, "Dato recibido en camara: " + result.getContents());

    }

    /**
     * Permanece a la espera de que las variables cambien
     *
     * @param o   la clase observada
     * @param arg objeto observado
     * @author Antonio Rodriguez Sirgado
     */
    @Override
    public void update(Observable o, Object arg) {
        // comprueba si ha recibido un objeto Reply que será un error de conexión
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            Fragment fragment = new Principal();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {
            request = (Message) arg;
            String command = request.getCommand();

            if (command.equals(Global.LIST_ACTIVE_USERS)) {
                listUsers = (ArrayList<Users>) request.getObject();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterList = new AdapterNormalUsersList(listUsers, getContext());
                        recyclerView.setAdapter(adapterList);
                        adapterList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("INFO", "Usuario seleccionado: " + listUsers.get(recyclerView.getChildAdapterPosition(view)).getName());

                                AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                                confirmation.setTitle(R.string.attention);
                                confirmation.setMessage("Enviar solicitud de amistad a: "+ listUsers.get(recyclerView.getChildAdapterPosition(view)).getName());
                                confirmation.setCancelable(true);
                                confirmation.setPositiveButton(R.string.yes, (dialog, which) -> {
                                    sendNewFriend(listUsers.get(recyclerView.getChildAdapterPosition(view)), false);
                                });
                                confirmation.setNegativeButton(R.string.Cancel, (dialog, which) -> {

                                });

                                confirmation.show();


                            }
                        });
                    }
                });
            } else if (command.equals(Global.ADD_USER_RELATIONSHIP)) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showSnack(getView(), getString(R.string.Friendship_added), Snackbar.LENGTH_LONG);
                        Fragment fragment = new MyAccount();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                    }
                });
            } else if (command.equals(Global.GET_USER)) {
                if (request.getParameters().equals(Global.OK)) {
                    sendNewFriend((Users) request.getObject(), true);
                }
            }


            //UsersList.loading.post(() -> UsersList.loading.setVisibility(View.INVISIBLE));
        }
    }
}