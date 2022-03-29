package proyecto.golfus.forat19;

import static proyecto.golfus.forat19.utils.Services.esTablet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

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

        txtUser = (TextView) findViewById(R.id.registerId);
        txtPassword = (TextInputEditText) findViewById(R.id.registerPassword);
        txtRePassword = (TextInputEditText) findViewById(R.id.registerRePassword);
        txtName = (TextView) findViewById(R.id.registerName);
        txtMail = (TextView) findViewById(R.id.registerMail);
        txtPhone = (TextView) findViewById(R.id.registerPhone);
        txtAddress = (TextView) findViewById(R.id.registerAddress);
        txtInfoError = (TextView) findViewById(R.id.txtInfoError);

        checkInfo = (CheckBox) findViewById(R.id.checkInfo);
        btnSave = (Button) findViewById(R.id.btnRegisterOk);

        user = extras.getString(LoginScreen.EXTRA_USER);
        password = extras.getString(LoginScreen.EXTRA_PASSWORD);

        txtUser.setText(user);
        txtPassword.setText(password);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDataUser()) {
                    AlertDialog.Builder confirmation = new AlertDialog.Builder(RegisterScreen.this);
                    confirmation.setTitle("Atención");
                    confirmation.setMessage("¿Son todos los datos correctos?");
                    confirmation.setCancelable(true);
                    confirmation.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            createUser();
                            editor.putString("user", txtUser.getText().toString());
                            editor.putBoolean("openSession", true);
                            editor.apply();

                            Intent intent = new Intent(RegisterScreen.this, PrincipalScreen.class);
                            startActivity(intent);
                        }
                    });

                    confirmation.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

                    confirmation.show();

                }
                ;
            }
        });

    }

    /**
     * Comprovamos que los datos introducidos sean correctos
     *
     * @return true / false
     */
    private boolean checkDataUser() {

        String[] words = txtName.getText().toString().split("\\s+");
        String patternPassword = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,})";
        String patternMail = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        String patternUser = "((?i)^[a-zA-z].*).{6,10}";

        //txtUser.setTextColor(Color.WHITE);
        //txtName.setTextColor(Color.WHITE);


        // campo usuario
        if (!txtUser.getText().toString().matches(patternUser)) {
            txtUser.setTextColor(Color.RED);
            txtUser.requestFocus();
            txtInfoError.setText(R.string.error_user);
            return false;

            // campo nombre
        } else if (txtName.length() < 10 || txtName.length() > 40 || words.length != 2) {
            txtName.setTextColor(Color.RED);
            txtName.requestFocus();
            txtInfoError.setText(R.string.error_name);
            return false;

            // campo password
        } else if (!txtPassword.getText().toString().matches(patternPassword)) {
            txtPassword.setTextColor(Color.RED);
            txtPassword.requestFocus();
            txtInfoError.setText(R.string.error_password);
            return false;

            // campo rePassword
        } else if (!(txtPassword.getText().toString().equals(txtRePassword.getText().toString()))) {
            txtRePassword.setTextColor(Color.RED);
            txtRePassword.requestFocus();
            txtInfoError.setText(R.string.error_rePassword);
            return false;

            // campo email
        } else if (!(txtMail.getText().toString().matches(patternMail))) {
            txtMail.setTextColor(Color.RED);
            txtMail.requestFocus();
            txtInfoError.setText(R.string.error_mail);
            return false;

            // campo telefono
        } else if (txtPhone.length() != 9) {
            txtPhone.setTextColor(Color.RED);
            txtPhone.requestFocus();
            txtInfoError.setText(R.string.error_phone);
            return false;

            // campo direccion
        } else if (txtAddress.length() > 70) {
            txtAddress.setTextColor(Color.RED);
            txtAddress.requestFocus();
            txtInfoError.setText(R.string.error_address);
            return false;
        }

        txtInfoError.setText("");
        return true;

    }

    private void createUser() {
        User newUser = new User();

        newUser.setName(txtName.getText().toString());
        newUser.setId(txtUser.getText().toString());
        newUser.setPassword(txtPassword.getText().toString());
        newUser.setMail(txtMail.getText().toString());
        newUser.setPhone(txtPhone.getText().toString());
        newUser.setAddress(txtAddress.getText().toString());
        newUser.setInfo(checkInfo.isChecked());
    }
}