package proyecto.golfus.forat19.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
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
 * Fragment para la actualización de datos de usuario
 * @author Antonio Rodriguez Sirgado
 */
public class UpdateUser extends Fragment implements Observer {

    private TextView txtName, txtMail, txtPhone, txtAddress;
    private TextInputLayout tilName, tilMail, tilPassword, tilRePassword, tilPhone, tilAddress;
    private TextInputEditText txtPassword, txtRePassword;
    public static ProgressBar updateLoading;
    private Button btnUpdate;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    Message request;
    Users user;

    public UpdateUser() {
    }

    public static UpdateUser newInstance() {
        UpdateUser fragment = new UpdateUser();
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
        View view = inflater.inflate(R.layout.fragment_update_user, container, false);

        updateLoading = view.findViewById(R.id.update_loading);
        updateLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

        txtPassword = view.findViewById(R.id.updatePassword);
        tilPassword = view.findViewById(R.id.LayoutUpdatePassword);

        txtRePassword = view.findViewById(R.id.updateRePassword);
        tilRePassword = view.findViewById(R.id.LayoutUpdateRePassword);

        txtName = view.findViewById(R.id.updateName);
        tilName = view.findViewById(R.id.LayoutUpdateName);

        txtMail = view.findViewById(R.id.updateMail);
        tilMail = view.findViewById(R.id.LayoutUpdateMail);

        txtPhone = view.findViewById(R.id.upadtePhone);
        tilPhone = view.findViewById(R.id.LayoutUpdatePhone);

        txtAddress = view.findViewById(R.id.updateAddress);
        tilAddress = view.findViewById(R.id.LayoutUpdateAddress);

        btnUpdate = view.findViewById(R.id.btnUpdateOkOk);

        preferences = this.getActivity().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Botón update
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLoading.setVisibility(View.VISIBLE);
                checkPassword();
            }
        });

        //cargamos datos de usuario
        getUser();

        return view;

    }

    /**
     * Permanece a la espera de que el objeto observado varie
     * @author Antonio Rodriguez Sirgado
     * @param o clase observada
     * @param arg objeto observado
     */
    @Override
    public void update(Observable o, Object arg) {

        // comprueba si ha recibido un objeto Reply que será un error de conexión
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            // Volvemos a fragment inicial
            Fragment fragment = new PrincipalFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        } else {
            btnUpdate.setEnabled(true);
            request = (Message) arg;
            String command = request.getCommand();
            user = (Users) request.getObject();

            Log.d("INFO", "Token recibido: " + request.getToken());
            Log.d("INFO", "Parametros recibido: " + request.getParameters());
            Log.d("INFO", "Comando recibido: " + request.getCommand());

            switch (command) {
                case Global.GET_USER:
                    changeText();
                    break;

                case Global.UPDATE_USER:

                    if (request.getParameters().equals("Error:1")) {

                        UpdateUser.updateLoading.post(() -> UpdateUser.updateLoading.setVisibility(View.INVISIBLE));
                        Users newUser = (Users) request.getObject();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                tilName.setError(null);
                                tilPassword.setError(null);
                                tilRePassword.setError(null);
                                tilMail.setError(null);
                                tilPhone.setError(null);
                                tilAddress.setError(null);

                                List<TextInputLayout> textInputLayoutsError = new ArrayList<TextInputLayout>();
                                List<TextView> textViewList = new ArrayList<TextView>();
                                List<Integer> errorMessage = new ArrayList<Integer>();


                                    // campo nombre

                                if (newUser.getName().equals("*")) {
                                    textInputLayoutsError.add(tilName);
                                    textViewList.add(txtName);
                                    errorMessage.add(R.string.error_name);

                                    // campo password
                                }
                                if (newUser.getPassword().equals("*")) {
                                    textInputLayoutsError.add(tilPassword);
                                    textViewList.add(txtPassword);
                                    errorMessage.add(R.string.error_password);

                                    // campo email
                                }
                                if (newUser.getEmail().equals("*")) {
                                    textInputLayoutsError.add(tilMail);
                                    textViewList.add(txtMail);
                                    errorMessage.add(R.string.error_mail);

                                    // campo telefono
                                }
                                if (newUser.getPhone().equals("*")) {
                                    textInputLayoutsError.add(tilPhone);
                                    textViewList.add(txtPhone);
                                    errorMessage.add(R.string.error_phone);

                                    // campo direccion
                                }
                                if (newUser.getAddress().equals("*")) {
                                    textInputLayoutsError.add(tilAddress);
                                    textViewList.add(txtAddress);
                                    errorMessage.add(R.string.error_address);
                                }

                                if (errorMessage.size() != 0) {
                                    for (int i = 0; i < textInputLayoutsError.size(); i++) {
                                        textInputLayoutsError.get(i).setError(getResources().getString(errorMessage.get(i)));
                                    }
                                    textViewList.get(0).requestFocus();
                                }
                            }

                        });


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnUpdate.setEnabled(true);
                            }
                        });
                    } else if (request.getParameters().equals(Global.OK)) {
                        int activeID = ((Users) request.getObject()).getId_user();
                        String user = ((Users) request.getObject()).getUsername();
                        int typeUser = ((Users) request.getObject()).getId_usertype();

                        editor.putInt(Global.PREF_ACTIVE_ID, activeID);
                        editor.putString(Global.PREF_ACTIVE_USER, user);
                        editor.putInt(Global.PREF_TYPE_USER, typeUser);
                        editor.apply();

                        Utils.showSnack(getView(), R.string.Data_properly_updated, Snackbar.LENGTH_LONG);

                        // Volvemos a fragment inicial
                        Fragment fragment = new MyAccount();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

                        Log.d("INFO", request.getMessageText());
                    } else {
                        Log.d("INFO", request.getMessageText());
                    }
            }
        }
    }

    /**
     * <b>Comprueba en el servidor que los datos modificados sean correctos</b><br>
     * Mensaje = (token¬device, updateUser, id, usuario)
     * @author Antonio Rodriguez Sirgado
     */
    private void checkDataUser() {

        String name = txtName.getText().toString();
        String password = txtPassword.getText().toString();
        String email = txtMail.getText().toString();
        String phone = txtPhone.getText().toString();
        String address = txtAddress.getText().toString();

        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        String active = user.getActive();
        String username = user.getUsername();
        int typeUser = user.getId_usertype();
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);

        Users toCheckUser = new Users(activeID, username, name, password, typeUser, active, email, phone, address);
        Message message = new Message(activeToken + "¬" + Utils.getDevice(getContext()), Global.UPDATE_USER, Integer.toString(activeID), toCheckUser);

        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);

    }

    /**
     * Comprueba que los 2 password coincidan
     * @author Antonio Rodriguez Sirgado
     */
    private void checkPassword() {
        if (!(txtPassword.getText().toString().equals(txtRePassword.getText().toString()))) {

            txtRePassword.requestFocus();
            tilPassword.setError(getResources().getString(R.string.error_rePassword));
            tilRePassword.setError("*");

        } else {
            //updateLoading.setVisibility(View.VISIBLE);
            checkDataUser();
            Utils.hideKeyboard(getActivity());

        }
    }

    /**
     * <b>Obtiene los datos del usuario solicitado</b><br>
     * Mensaje= (token¬device, getUser, id, null)
     * @author Antonio Rodriguez Sirgado
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
     * Modifica los campos de texto con los datos del usuario recibido
     * @author Antonio Rodriguez Sirgado
     */
    public void changeText() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String password = ((Users) request.getObject()).getPassword();
                String phone = ((Users) request.getObject()).getPhone();
                String email = ((Users) request.getObject()).getEmail();
                String name = ((Users) request.getObject()).getName();
                String address = (((Users) request.getObject()).getAddress());

                txtPassword.setText(password);
                txtRePassword.setText(password);
                txtPhone.setText(phone);
                txtMail.setText(email);
                txtName.setText(name);
                txtAddress.setText(address);

                UpdateUser.updateLoading.post(() -> UpdateUser.updateLoading.setVisibility(View.INVISIBLE));
            }
        });
    }
}