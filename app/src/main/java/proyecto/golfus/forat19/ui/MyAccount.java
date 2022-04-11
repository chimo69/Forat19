package proyecto.golfus.forat19.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccount extends Fragment implements Observer {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView txtUserInfo, txtNameInfo, txtPhoneInfo, txtMailInfo, txtAddressInfo;
    private View view;
    private Button btnDelete;
    private SharedPreferences preferences;
    Message request;
    Users user;


    public MyAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccount.
     */

    public static MyAccount newInstance(String param1, String param2) {
        MyAccount fragment = new MyAccount();
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
        //super.onCreateView(inflater,container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        txtUserInfo = view.findViewById(R.id.txtUserInfo);
        txtMailInfo = view.findViewById(R.id.txtMailInfo);
        txtPhoneInfo = view.findViewById(R.id.txtPhoneInfo);
        txtAddressInfo = view.findViewById(R.id.txtAddressInfo);
        txtNameInfo = view.findViewById(R.id.txtNameInfo);
        preferences = this.getActivity().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        btnDelete = view.findViewById(R.id.btn_delete);

        // Boton de borrado de cuenta
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                confirmation.setTitle(R.string.attention);
                confirmation.setMessage("¿Seguro que quieres eliminar la cuenta?");
                confirmation.setCancelable(true);
                confirmation.setPositiveButton(R.string.yes, (dialog, which) -> {
                    //loadingMenu.setVisibility(View.VISIBLE);
                    //    logoutUser();
                deleteUser();
                });
                confirmation.setNegativeButton(R.string.Cancel, (dialog, which) -> {
                });

                confirmation.show();


            }
        });

        getUser();
        return view;
    }

    /**
     * Consigue los datos del usuario solicitado por ID
     * Mensaje = (token, getUser, id, null)
     */
    private void getUser() {
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);
        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);

        Message mMessage = new Message(activeToken, Global.GET_USER, Integer.toString(activeID), null);
        Log.d("INFO", "Enviando token: " + activeToken);

        RequestServer request = new RequestServer();
        request.request(mMessage);
        request.addObserver(this);
    }

    /**
     * Desactiva al usuario solicitado por ID
     * Mensaje= (token, deleteUser, null, usuario)
     */
    private void deleteUser() {
        String token = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        Message mMessage = new Message(token, Global.DELETE_USER, null, user);

        RequestServer request = new RequestServer();
        request.request(mMessage);
        request.addObserver(this);
    }

    /**
     * Permanece a la espera de que las variables cambien
     *
     * @param o   la clase observada
     * @param arg objeto obserbado
     */
    @Override
    public void update(Observable o, Object arg) {

        request = (Message) arg;
        String parameter = request.getParameters();
        user= (Users) request.getObject();

        Log.d("INFO", "Parametro recibido:" + parameter);

        switch (parameter) {
            case Global.USER_GETTED:
                if (request.getParameters().equals(Global.USER_GETTED)) {

                    Log.d("INFO", "USER:" + ((Users) request.getObject()).getUsername());
                    Log.d("INFO", "MAIL: " + ((Users) request.getObject()).getEmail());

                    changeText();
                }
                break;
            case Global.USER_UPDATED:
                if (request.getParameters().equals(Global.USER_UPDATED)) {
                    // TODO mostrar resultado
                    Log.d("INFO", request.getParameters());
                } else if (request.getCommand().equals(Global.ERROR)) {
                    Log.d("INFO", request.getParameters());
                }

                break;

            case Global.DELETE_USER:
                if (request.getParameters().equals(Global.USER_UPDATED)){
                    Utils.showToast(getActivity(),"Cuenta eliminada con éxito",Toast.LENGTH_LONG);
                    Intent intent = new Intent(getActivity(),LoginScreen.class);
                    startActivity(intent);
                }else{
                    Utils.showToast(getActivity(),"Ha sido imposible eliminar la cuenta",Toast.LENGTH_SHORT);
                    Log.d("INFO","Imposible eliminar");
                }
        }

    }

    public void changeText() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String userName = ((Users) request.getObject()).getUsername();
                String phone = ((Users) request.getObject()).getPhone();
                String email = ((Users) request.getObject()).getEmail();
                String name = ((Users) request.getObject()).getName();
                String address = (((Users) request.getObject()).getAddress());
                if (email==null) {
                    email = "-";
                }
                if (address==null) {
                    address = "-";
                }
                if (phone==null){
                    phone="-";
                }
                txtUserInfo.setText(userName);
                txtPhoneInfo.setText(phone);
                txtMailInfo.setText(email);
                txtNameInfo.setText(name);
                txtAddressInfo.setText(address);
            }
        });
    }


}