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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;

public class RegisterScreen extends AppCompatActivity {

    private String user, password;
    private TextView txtUser, txtName, txtMail, txtPhone, txtAddress, txtInfoError;
    private TextInputEditText txtPassword, txtRePassword;
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
        txtPassword = findViewById(R.id.registerPassword);
        txtRePassword = findViewById(R.id.registerRePassword);
        txtName = findViewById(R.id.registerName);
        txtMail = findViewById(R.id.registerMail);
        txtPhone = findViewById(R.id.registerPhone);
        txtAddress = findViewById(R.id.registerAddress);
        txtInfoError = findViewById(R.id.txtInfoError);


        checkInfo = findViewById(R.id.checkInfo);
        btnSave = findViewById(R.id.btnRegisterOk);

        user = extras.getString(Global.EXTRA_USER);
        password = extras.getString(Global.EXTRA_PASSWORD);

        txtUser.setText(user);
        txtPassword.setText(password);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(txtPassword.getText().toString().equals(txtRePassword.getText().toString()))) {
                    txtRePassword.setTextColor(Color.RED);
                    txtRePassword.requestFocus();
                    txtInfoError.setText(R.string.error_rePassword);
                } else {
                    checkDataUser();
                }
            }
        });

    }

    /**
     * Comprovamos que los datos introducidos sean correctos
     *
     * @return true / false
     */
    private void checkDataUser() {

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

        Thread thread = new Thread(() -> {

            while (request.getMessage() == null) {
            }
            Users newUser;

            Log.d("INFO", "Token: " + request.getMessage().getToken());
            Log.d("INFO", "Parametros: " + request.getMessage().getParameters());
            Log.d("INFO", "Comando: " + request.getMessage().getCommand());


            if (request.getMessage().getParameters().equals("Incorrect Data")) {

                Log.d("INFO", "Nombre: " + ((Users) request.getMessage().getObject()).getName());
                Log.d("INFO", "Correo: " + ((Users) request.getMessage().getObject()).getEmail());


                newUser = (Users) request.getMessage().getObject();

                RegisterScreen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // campo usuario


                        List<TextView> textViewError = new ArrayList<TextView>();
                        List<Integer> errorMessage = new ArrayList<Integer>();

                        if (newUser.getUsername().equals("*")) {
                            textViewError.add(txtUser);

                            errorMessage.add(R.string.error_user);

                            // campo nombre
                        }
                        if (newUser.getName().equals("*")) {
                            textViewError.add(txtName);
                            errorMessage.add(R.string.error_name);

                            // campo password
                        }
                        if (newUser.getPassword().equals("*")) {
                            textViewError.add(txtPassword);
                            errorMessage.add(R.string.error_password);

                            // campo email
                        }
                        if (newUser.getEmail().equals("*")) {
                            textViewError.add(txtMail);
                            errorMessage.add(R.string.error_mail);

                            // campo telefono
                        }
                        if (newUser.getPhone().equals("*")) {
                            textViewError.add(txtPhone);
                            errorMessage.add(R.string.error_phone);

                            // campo direccion
                        }
                        if (newUser.getAddress().equals("*")) {
                            textViewError.add(txtAddress);
                            errorMessage.add(R.string.error_address);

                        }

                        if (errorMessage.size() != 0) {
                            for (int i = 0; i < textViewError.size(); i++) {
                                textViewError.get(i).setTextColor(Color.RED);
                                textViewError.get(i).setError(getResources().getString(errorMessage.get(i)));
                            }

                            textViewError.get(0).requestFocus();

                        }

                    }
                });


            } else {


                String user = ((Users) request.getMessage().getObject()).getUsername();
                int typeUser = ((Users) request.getMessage().getObject()).getId_usertype();
                String activeToken = request.getMessage().getToken();

                editor.putString(Global.PREF_ACTIVE_USER, user);
                editor.putInt(Global.PREF_TYPE_USER, typeUser);
                editor.putString(Global.PREF_ACTIVE_TOKEN, activeToken);
                editor.apply();

                Intent intent = new Intent(RegisterScreen.this, MenuPrincipal.class);
                startActivity(intent);

            }
        });
        thread.start();


    }

}