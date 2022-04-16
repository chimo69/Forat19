package proyecto.golfus.forat19.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para mostrar la cuenta del usuario
 *
 * @author Antonio Rodriguez Sirgado
 */
public class MyAccount extends Fragment implements Observer {

    private TextView txtUserInfo, txtNameInfo, txtPhoneInfo, txtMailInfo, txtAddressInfo;
    private ImageView qrCode;
    private View view;
    private Button btnDelete, btnUpdate;
    private SharedPreferences preferences;
    private static ProgressBar loading;

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
        txtUserInfo = view.findViewById(R.id.txtUserInfo);
        txtMailInfo = view.findViewById(R.id.txtMailInfo);
        txtPhoneInfo = view.findViewById(R.id.txtPhoneInfo);
        txtAddressInfo = view.findViewById(R.id.txtAddressInfo);
        txtNameInfo = view.findViewById(R.id.txtNameInfo);
        preferences = this.getActivity().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnUpdate = view.findViewById(R.id.btn_update);
        loading = view.findViewById(R.id.loading_myAccount);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);


        // Boton de borrado de cuenta
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                confirmation.setTitle(R.string.attention);
                confirmation.setMessage("¿Seguro que quieres eliminar la cuenta?");
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

        getUser();
        return view;
    }

    /**
     * @author Antonio Rodriguez Sirgado
     * Consigue los datos del usuario solicitado por ID
     * Mensaje = (token, getUser, id, null)
     */
    private void getUser() {
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);
        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);

        Message mMessage = new Message(activeToken + "¬" + Utils.getDevice(requireContext()), Global.GET_USER, Integer.toString(activeID), null);

        RequestServer request = new RequestServer();
        request.request(mMessage);
        request.addObserver(this);
    }

    /**
     * @author Antonio Rodriguez Sirgado
     * Desactiva al usuario solicitado por ID
     * Mensaje= (token, deleteUser, null, usuario)
     */
    private void deleteUser() {
        String token = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        Message mMessage = new Message(token + "¬" + Utils.getDevice(getActivity()), Global.DELETE_USER, null, user);

        RequestServer request = new RequestServer();
        request.request(mMessage);
        request.addObserver(this);
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
            Fragment fragment = new PrincipalFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {
            request = (Message) arg;
            String command = request.getCommand();
            user = (Users) request.getObject();

            Log.d("INFO", "Token recibido: " + request.getToken());
            Log.d("INFO", "Parametros recibido: " + request.getParameters());
            Log.d("INFO", "Comando recibido: " + request.getCommand());
            switch (command) {
                case Global.GET_USER:
                    if (request.getParameters().equals(Global.OK)) {

                        Log.d("INFO", "USER:" + ((Users) request.getObject()).getUsername());
                        Log.d("INFO", "MAIL: " + ((Users) request.getObject()).getEmail());

                        changeText();
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
                        Log.d("INFO", "Imposible eliminar");
                    }
            }

        }

    }

    /**
     * @author Antonio Rodriguez Sirgado
     * Cambia el texto del usuario con los datos obtenidos
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

                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                try {
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(userName, BarcodeFormat.QR_CODE, 750, 750);
                    qrCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}