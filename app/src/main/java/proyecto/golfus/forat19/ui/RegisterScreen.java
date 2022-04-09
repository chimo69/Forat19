package proyecto.golfus.forat19.ui;

import static proyecto.golfus.forat19.utils.Utils.esTablet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

public class RegisterScreen extends AppCompatActivity implements Observer {

    private String user, password;
    private TextView txtUser, txtName, txtMail, txtPhone, txtAddress;
    private TextInputLayout tilUser,tilName,tilMail,tilPassword, tilRePassword, tilPhone,tilAddress;
    private TextInputEditText txtPassword, txtRePassword;
    public static ProgressBar registerLoading;
    private Button btnSave;
    private CheckBox checkInfo;

    SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (esTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        txtUser = findViewById(R.id.registerId);
        tilUser =findViewById(R.id.LayoutRegisterId);

        txtPassword = findViewById(R.id.registerPassword);
        tilPassword = findViewById(R.id.LayoutRegisterPassword);

        txtRePassword = findViewById(R.id.registerRePassword);
        tilRePassword = findViewById(R.id.LayoutRegisterRePassword);

        txtName = findViewById(R.id.registerName);
        tilName =findViewById(R.id.LayoutRegisterName);

        txtMail = findViewById(R.id.registerMail);
        tilMail =findViewById(R.id.LayoutRegisterMail);

        txtPhone = findViewById(R.id.registerPhone);
        tilPhone =findViewById(R.id.LayoutRegisterPhone);

        txtAddress = findViewById(R.id.registerAddress);
        tilAddress =findViewById(R.id.LayoutRegisterAddress);

        btnSave = findViewById(R.id.btnRegisterOk);

        user = extras.getString(Global.EXTRA_USER);
        password = extras.getString(Global.EXTRA_PASSWORD);

        txtUser.setText(user);
        txtPassword.setText(password);

        registerLoading = findViewById(R.id.register_loading);
        registerLoading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(txtPassword.getText().toString().equals(txtRePassword.getText().toString()))) {

                    txtRePassword.requestFocus();
                    tilPassword.setError(getResources().getString(R.string.error_rePassword));
                    tilRePassword.setError("*");

                } else {
                    registerLoading.setVisibility(View.VISIBLE);
                    checkDataUser();
                    Utils.hideKeyboard(RegisterScreen.this);
                    btnSave.setEnabled(false);

                }
            }
        });

    }

    /**
     * Comprovamos que los datos introducidos sean correctos
     *
     */
    private void checkDataUser() {

        //RegisterScreen.registerLoading.post(() -> RegisterScreen.registerLoading.setVisibility(View.VISIBLE));

        String token = preferences.getString("Token", "");
        int id_user = 0;
        int id_usertype = 1;
        String username = txtUser.getText().toString();
        String name = txtName.getText().toString();
        String password = txtPassword.getText().toString();
        String active = "S";
        String email = txtMail.getText().toString();
        String phone = txtPhone.getText().toString();
        String address = txtAddress.getText().toString();


        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();

        Users toCheckUser = new Users(id_user, username, name, password, id_usertype, active, email, phone, address);
        Message message = new Message(token, "AddUser", null, toCheckUser);

        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);


    }

    @Override
    public void update(Observable o, Object arg) {

        Message request = (Message) arg;

        Log.d("INFO", "Token: " + request.getToken());
        Log.d("INFO", "Parametros: " + request.getParameters());
        Log.d("INFO", "Comando: " + request.getCommand());

        if (request.getParameters().equals("Incorrect Data")) {

            Log.d("INFO", "Nombre: " + ((Users) request.getObject()).getName());
            Log.d("INFO", "Correo: " + ((Users) request.getObject()).getEmail());

            Users newUser;
            newUser = (Users) request.getObject();

            RegisterScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // campo usuario

                    List<TextInputLayout> textInputLayoutsError = new ArrayList<TextInputLayout>();
                    List<TextView> textViewList = new ArrayList<TextView>();
                    List<Integer> errorMessage = new ArrayList<Integer>();

                    if (newUser.getUsername().equals("*")) {
                        textInputLayoutsError.add(tilUser);
                        textViewList.add(txtUser);
                        errorMessage.add(R.string.error_user);

                        // campo nombre
                    }
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

            RegisterScreen.registerLoading.post(() -> RegisterScreen.registerLoading.setVisibility(View.GONE));
            RegisterScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnSave.setEnabled(true);
                }
            });


        } else {

            String user = ((Users) request.getObject()).getUsername();
            int typeUser = ((Users) request.getObject()).getId_usertype();
            String activeToken = request.getToken();

            editor.putString(Global.PREF_ACTIVE_USER, user);
            editor.putInt(Global.PREF_TYPE_USER, typeUser);
            editor.putString(Global.PREF_ACTIVE_TOKEN, activeToken);
            editor.apply();

            Intent intent = new Intent(RegisterScreen.this, MenuPrincipal.class);
            startActivity(intent);

        }
    }
}