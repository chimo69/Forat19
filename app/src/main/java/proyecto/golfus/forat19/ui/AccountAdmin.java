package proyecto.golfus.forat19.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
 * Fragment que muestra al Admin los datos del usuario para poder gestionarlo
 * @author Antonio Rodríguez Sirgado
 */
public class AccountAdmin extends Fragment implements Observer {

    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "username";
    private TextView txtId, txtUsername, txtFullname, txtPhone, txtAddress, txtMail;
    private TextInputLayout layoutPassword, layoutRePassword;
    private TextInputEditText password, rePassword;
    private ImageView logo;
    private LinearLayout title;
    private Button update;
    private CheckBox checkActiveUser, checkAdminUser;
    private Users user;
    private SharedPreferences preferences;
    private Message request;
    private Boolean passwordChanged=false;
    private static ProgressBar loading;

    public AccountAdmin() {
        // Required empty public constructor
    }

    public static AccountAdmin newInstance(int param1, String param2) {
        AccountAdmin fragment = new AccountAdmin();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // recogemos el objeto recibido
        if (getArguments() != null) {
            Bundle args = this.getArguments();
            user = (Users) args.getSerializable("user");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_admin, container, false);
        txtId = view.findViewById(R.id.account_admin_id);
        txtUsername = view.findViewById(R.id.account_admin_username);
        txtFullname = view.findViewById(R.id.txtAccountAdminUser);
        txtMail = view.findViewById(R.id.txtAccountAdminMail);
        txtPhone = view.findViewById(R.id.txtAccountAdminPhone);
        txtAddress = view.findViewById(R.id.txtAccountAdminAddress);
        checkActiveUser = view.findViewById(R.id.checkActiveUser);
        checkAdminUser = view.findViewById(R.id.checkAdminUser);
        title=view.findViewById(R.id.AccountAdminTitle);
        logo=view.findViewById(R.id.AccountAdminLogo);
        update = view.findViewById(R.id.AccountAdminBtnUpdate);
        layoutPassword = view.findViewById(R.id.LayoutAccountAdminPassword);
        layoutRePassword = view.findViewById(R.id.LayoutAccountAdminRePassword);
        password = view.findViewById(R.id.accountAdminPassword);
        rePassword = view.findViewById(R.id.accountAdminRePassword);
        loading= view.findViewById(R.id.accountAdminLoading);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

        txtId.setText(String.format("%06d", user.getId_user()));
        txtUsername.setText(user.getUsername());
        txtFullname.setText(user.getName());
        txtPhone.setText(user.getPhone());
        txtAddress.setText(user.getAddress());
        txtMail.setText(user.getEmail());

        preferences = this.getActivity().getSharedPreferences("Credentials", Context.MODE_PRIVATE);

        Utils.hideKeyboard(getActivity());

        checkAdminUser.setChecked(true);
        checkActiveUser.setChecked(true);

        if (user.getId_usertype()!= Global.TYPE_ADMIN_USER){
            logo.setVisibility(View.INVISIBLE);
            checkAdminUser.setChecked(false);

        }
        if (user.getActive().equals("N")){
            title.setBackgroundColor(getResources().getColor(R.color.error));
            checkActiveUser.setChecked(false);
        }

        // Checkbox de usuario activo
        checkActiveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkActiveUser.isChecked()){
                    title.setBackgroundColor(getResources().getColor(R.color.green));
                }else{
                    title.setBackgroundColor(getResources().getColor(R.color.error));
                }
            }
        });

        // Checkbox de usuario administrador
        checkAdminUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAdminUser.isChecked()){
                    logo.setVisibility(View.VISIBLE);
                }else{
                    logo.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Boton actualizar
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPassword();
            }
        });
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
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
        } else {
            request = (Message) arg;
            user = (Users) request.getObject();

            Log.d("INFO", "Token recibido: " + request.getToken());
            Log.d("INFO", "Parametros recibido: " + request.getParameters());
            Log.d("INFO", "Comando recibido: " + request.getCommand());

            if (request.getParameters().equals("Error:1")){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutPassword.setError(getResources().getString(R.string.error_password));
                    }
                });
            } else if (request.getParameters().equals(Global.OK)) {
                Utils.showSnack(getView(), R.string.Data_properly_updated, Snackbar.LENGTH_LONG);
            }
        }
        AccountAdmin.loading.post(() -> AccountAdmin.loading.setVisibility(View.INVISIBLE));
    }

    /**
     * Comprueba que los 2 password coincidan y que se haya escrito algo
     * @author Antonio Rodriguez Sirgado
     */
    private void checkPassword() {
        if (password.getText().toString().equals("") && rePassword.getText().toString().equals("")){
            passwordChanged= false;
            checkDataUser();
        } else {

            if (!(password.getText().toString().equals(rePassword.getText().toString()))) {

                rePassword.requestFocus();
                layoutPassword.setError(getResources().getString(R.string.error_rePassword));
                layoutRePassword.setError("*");

            } else {
                passwordChanged=true;
                checkDataUser();
                Utils.hideKeyboard(getActivity());

            }
        }

    }

    /**
     * <b>Comprueba en el servidor que los datos modificados sean correctos</b><br>
     * Mensaje = (token¬device, updateUser, id, usuario)
     * @author Antonio Rodriguez Sirgado
     */
    private void checkDataUser() {
        String pass;
        if (passwordChanged){
            pass = password.getText().toString();
        } else {
            pass = user.getPassword();
        }

        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);

        AccountAdmin.loading.post(() -> AccountAdmin.loading.setVisibility(View.VISIBLE));
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);
        int typeUser=1;
        String activeUser="N";
        if (checkAdminUser.isChecked()){
            typeUser=0;
        }
        if (checkActiveUser.isChecked()){
            activeUser="S";
        }
        Users toCheckUser = new Users(user.getId_user(), user.getUsername(), user.getName(), pass, typeUser, activeUser, user.getEmail(), user.getPhone(), user.getAddress());
        Message message = new Message(activeToken + "¬" + Utils.getDevice(getContext()), Global.UPDATE_USER, Integer.toString(activeID), toCheckUser);

        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);

    }



}