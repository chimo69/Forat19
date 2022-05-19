package proyecto.golfus.forat19.ui.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Players;
import Forat19.User_Relationships;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.adapterList.AdapterPlayerTypeList;
import proyecto.golfus.forat19.ui.lists.FriendsList;
import proyecto.golfus.forat19.ui.start.LoginScreen;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.ui.add.AddFriend;
import proyecto.golfus.forat19.ui.update.UpdateUser;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para mostrar la cuenta del usuario
 *
 * @author Antonio Rodriguez Sirgado
 */
public class MyAccount extends Fragment implements Observer {

    private TextView txtUserInfo, txtNameInfo, txtPhoneInfo, txtMailInfo, txtAddressInfo, infoPlayers, numberRequest;
    private CardView cvInfoPlayer, cvNumber;
    private ImageButton btnShowFriends, btnAddFriends;
    private ImageView qrCode;
    private View view;
    private Button btnDelete, btnUpdate;
    private static ProgressBar loading;
    private RecyclerView recyclerView;
    private AdapterPlayerTypeList adapterPlayersList;
    private ArrayList<Players> listPlayers = new ArrayList<>();
    private ArrayList<User_Relationships> listRequest = new ArrayList<>();

    Message request;
    Users user;

    public MyAccount() {
    }

    public static MyAccount newInstance() {
        MyAccount fragment = new MyAccount();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        qrCode = view.findViewById(R.id.qrCode);
        cvInfoPlayer = view.findViewById(R.id.CardView5);
        infoPlayers = view.findViewById(R.id.myAccount_infoPlayers);
        txtUserInfo = view.findViewById(R.id.txtUserInfo);
        txtMailInfo = view.findViewById(R.id.txtMailInfo);
        txtPhoneInfo = view.findViewById(R.id.txtPhoneInfo);
        txtAddressInfo = view.findViewById(R.id.txtAddressInfo);
        txtNameInfo = view.findViewById(R.id.txtNameInfo);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnAddFriends = view.findViewById(R.id.btn_addFriends);
        btnShowFriends = view.findViewById(R.id.btn_showFriends);
        recyclerView = view.findViewById(R.id.rw_myaccount_typePlayer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterPlayersList = new AdapterPlayerTypeList(listPlayers, getContext());
        recyclerView.setAdapter(adapterPlayersList);
        loading = view.findViewById(R.id.loading_myAccount);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        cvNumber = view.findViewById(R.id.cv_myAccount_number);
        numberRequest = view.findViewById(R.id.tv_MyAccount_number);

        // Boton de borrado de cuenta
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                confirmation.setTitle(R.string.attention);
                confirmation.setMessage(R.string.delete_the_account);
                confirmation.setCancelable(true);
                confirmation.setPositiveButton(R.string.yes, (dialog, which) -> {
                    deleteUser();
                });
                confirmation.setNegativeButton(R.string.Cancel, (dialog, which) -> {
                });

                confirmation.show();
            }
        });

        // Boton de actualización
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new UpdateUser();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            }
        });

        // Boton de añadir amigos
        btnAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddFriend();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
            }
        });

        // Boton de mostrar amigos
        btnShowFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FriendsList(listRequest);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
            }
        });
        getUser();
        loadRequestFriends();

        return view;
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
            //user = (Users) request.getObject();

            switch (command) {
                case Global.GET_USER:
                    if (request.getParameters().equals(Global.OK)) {

                        Log.d(Global.TAG, "USUARIO: " + ((Users) request.getObject()).getUsername());
                        Log.d(Global.TAG, "NOMBRE: " + ((Users) request.getObject()).getName());
                        Log.d(Global.TAG, "MAIL: " + ((Users) request.getObject()).getEmail());
                        Log.d(Global.TAG, "-------------------------------------------------");
                        changeText();
                        getTypePlayers();
                    }
                    break;

                case Global.DELETE_USER:
                    if (request.getParameters().equals(Global.OK)) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showToast(getActivity(), R.string.Account_deleted, Toast.LENGTH_LONG);
                            }
                        });
                        Intent intent = new Intent(getActivity(), LoginScreen.class);
                        startActivity(intent);
                    } else {
                        Utils.showSnack(getView(), R.string.Not_possible_to_delete_account, Snackbar.LENGTH_LONG);
                    }
                case Global.LIST_USER_PLAYER:
                    if (request.getParameters().equals(Global.OK)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listPlayers = (ArrayList<Players>) request.getObject();
                                if (listPlayers.size() > 0) {
                                    adapterPlayersList = new AdapterPlayerTypeList(listPlayers, getContext());
                                    recyclerView.setAdapter(adapterPlayersList);

                                    // Seleccion de jugadores
                                    adapterPlayersList.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d(Global.TAG, "Jugador seleccionado: " + listPlayers.get(recyclerView.getChildAdapterPosition(v)).getPlayer_type().getPlayer_type());
                                            Fragment fragment = new Player(listPlayers.get(recyclerView.getChildAdapterPosition(v)));
                                            cvInfoPlayer.setVisibility(View.VISIBLE);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_infoPlayer, fragment).commit();
                                        }
                                    });
                                } else {
                                    infoPlayers.setText("No dispone de jugadores");

                                }
                            }
                        });
                    }
                    break;

                case Global.LIST_USER_RELATIONSHIP_O:
                    listRequest = (ArrayList<User_Relationships>) request.getObject();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listRequest.size() > 0) {
                                cvNumber.setVisibility(View.VISIBLE);
                                numberRequest.setText(String.valueOf(listRequest.size()));
                            } else {
                                cvNumber.setVisibility(View.GONE);
                            }
                        }
                    });
                    break;
            }

        }

    }

    /**
     * Cambia el texto del usuario con los datos obtenidos
     *
     * @author Antonio Rodriguez Sirgado
     */
    public void changeText() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String userName = ((Users) request.getObject()).getUsername();
                String phone = ((Users) request.getObject()).getPhone();
                String email = ((Users) request.getObject()).getEmail();
                String name = ((Users) request.getObject()).getName();
                String address = (((Users) request.getObject()).getAddress());
                if (email == null) {
                    email = "-";
                }
                if (address == null) {
                    address = "-";
                }
                if (phone == null) {
                    phone = "-";
                }

                MyAccount.loading.post(() -> MyAccount.loading.setVisibility(View.INVISIBLE));
                txtUserInfo.setText(userName);
                txtPhoneInfo.setText(phone);
                txtMailInfo.setText(email);
                txtNameInfo.setText(name);
                txtAddressInfo.setText(address);

                // codigo QR
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                try {
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(userName, BarcodeFormat.QR_CODE, 500, 500);
                    qrCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * <b>Consigue los datos del usuario solicitado por ID</b><br>
     * Mensaje = (token¬device, getUser, id, null)
     *
     * @author Antonio Rodriguez Sirgado
     */
    private void getUser() {
        //Utils.sendRequest(getActivity(), Global.GET_USER, Utils.getActiveId(getActivity()), null);
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.GET_USER, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Desactiva al usuario solicitado por ID</b><br>
     * Mensaje= (token¬device, deleteUser, null, usuario)
     *
     * @author Antonio Rodriguez Sirgado
     */
    private void deleteUser() {
        //Utils.sendRequest(getActivity(), Global.DELETE_USER, null, user);
        user = (Users) request.getObject();
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.DELETE_USER, null, user);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    private void getTypePlayers() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_USER_PLAYER, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia el mensaje para cargar las solicitudes de amistad pendientes</b><br>
     * Mensaje = (token¬device, listUserRelationshipI, id usuario, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadRequestFriends() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_USER_RELATIONSHIP_O, String.valueOf(Global.activeUser.getId_user()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }


}