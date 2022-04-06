package proyecto.golfus.forat19.ui;

import static proyecto.golfus.forat19.utils.Utils.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Forat19.*;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

public class LoginScreen extends AppCompatActivity {


    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PASSWORD = "password";

    private ImageView logo;
    private TextView user;
    private TextInputEditText password;

    private Button login, register;
    private SwitchCompat openSession;

    public static ProgressBar loading;


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private RequestServer request;

    public static Message mMessage, respuesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // comprobamos formato del dispositivo

        if (esTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        logo = findViewById(R.id.loginLogo);
        user = findViewById(R.id.txtLoginUser);
        password = findViewById(R.id.txtLoginPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.btnRegister);
        openSession = findViewById(R.id.openSession);

        loading = findViewById(R.id.progressBar);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        loading.setVisibility(View.GONE);

        // Comprobamos token en el dispositivo
        if (checkToken()) {
            Intent intent = new Intent(LoginScreen.this, MenuPrincipal.class);
            startActivity(intent);
        } else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (user.length() > 0 && password.length() > 0) {

                        // Comprobamos token en servidor
                        checkTokenOnline();
                        Utils.hideKeyboard(LoginScreen.this );

                    }
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);

                    Bundle extras = new Bundle();
                    extras.putString(EXTRA_USER, user.getText().toString());
                    extras.putString(EXTRA_PASSWORD, password.getText().toString());

                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });

        }
    }

    private boolean checkToken() {

        if (!preferences.getString("activeUser", "").equals("") && preferences.getBoolean("openSession", false)) {
            return true;
        }
        return false;
    }

    /**
     * Construye el mensaje, lo manda al servidor y espera la respuesta para ver si es valida
     */
    private void checkTokenOnline(){
        String sendMessage = user.getText().toString() + "¬" + password.getText().toString();
        mMessage = new Message(null, "Loggin", sendMessage, null);

        request = new RequestServer();
        request.request(mMessage);

        LoginScreen.loading.post(() -> LoginScreen.loading.setVisibility(View.VISIBLE));

        Thread thread = new Thread(() -> {

            while(request.getMessage()==null){

            }
            Log.d("INFO","Token: "+request.getMessage().getToken());
            if (request.getMessage().getToken().equals("ValidToken")) {
                editor.putString("activeUser", user.getText().toString());
                editor.putBoolean("openSession", openSession.isChecked());
                editor.putInt("userType", 0);
                editor.apply();

                Intent intent = new Intent(LoginScreen.this, MenuPrincipal.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginScreen.this, "Error: Usuario desconocido", Toast.LENGTH_SHORT).show();
            }
        });
        thread.start();
    }

}