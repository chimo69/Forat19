package proyecto.golfus.forat19.ui;

import static proyecto.golfus.forat19.utils.Utils.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Observable;
import java.util.Observer;

import Forat19.*;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * @author Antonio Rodriguez Sirgado
 * Página de Login, primera página que se muestra al abrir la aplicación
 */
public class LoginScreen extends AppCompatActivity implements Observer {


    private ImageView logo;
    private TextView user;
    private TextInputEditText password;
    private Button login, register;
    private SwitchCompat openSession;
    public static ProgressBar loading;
    private String device;
    private View view;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static Message mMessage;

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
        view = findViewById(R.id.layoutLogin);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        loading.setVisibility(View.GONE);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

        // Boton ok Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.length() > 0 && password.length() > 0) {

                    // Comprobamos token en servidor
                    checkTokenOnline();
                    Utils.hideKeyboard(LoginScreen.this);

                }

            }
        });

        // Boton registrar
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);

                Bundle extras = new Bundle();
                extras.putString(Global.EXTRA_USER, user.getText().toString());
                extras.putString(Global.EXTRA_PASSWORD, password.getText().toString());

                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }


    /**
     * @author Antonio Rodríguez Sirgado
     * Construye el mensaje, lo manda al servidor y espera la respuesta para ver si es valida
     */
    private void checkTokenOnline() {
        String sendMessage = user.getText().toString() + "¬" + password.getText().toString();

        mMessage = new Message(null+"¬"+Utils.getDevice(this), Global.LOGIN, sendMessage, null);

        Log.d("INFO", "Token enviado: " + mMessage.getToken());
        Log.d("INFO", "Parametros enviado: " + mMessage.getParameters());
        Log.d("INFO", "Comando enviado: " + mMessage.getCommand());

        RequestServer request = new RequestServer();
        request.request(mMessage);
        request.addObserver(this);

        LoginScreen.loading.post(() -> LoginScreen.loading.setVisibility(View.VISIBLE));

    }

    /**
     * Permanece a la espera de que las variables cambien
     * @author Antonio Rodriguez Sirgado
     * @param o la clase observada
     * @param arg objeto observado
     */
    @Override
    public void update(Observable o, Object arg) {

        // Comprobamos si hemos recibido un objeto Reply que será un error de conexión
        if (arg instanceof Reply){
            Utils.showSnack(view, ((Reply) arg).getTypeError(), Snackbar.LENGTH_LONG);
            LoginScreen.loading.post(() -> LoginScreen.loading.setVisibility(View.GONE));

        }else if (arg instanceof Message){
            Message request = (Message) arg;

            Log.d("INFO", "Token: " + request.getToken());
            Log.d("INFO", "Parametros: " + request.getParameters());
            Log.d("INFO", "Comando: " + request.getCommand());
            Log.d("INFO", "Respuesta: " + request.getMessageText());

            if (request.getParameters().equals(Global.OK)) {
                String activeUser = ((Users) request.getObject()).getUsername();
                int typeUser = ((Users)request.getObject()).getId_usertype();
                String activeToken = request.getToken();
                int activeID = ((Users) request.getObject()).getId_user();

                editor.putString(Global.PREF_ACTIVE_USER, activeUser);
                editor.putBoolean(Global.PREF_OPEN_KEEP_SESSION_OPEN, openSession.isChecked());
                editor.putString(Global.PREF_ACTIVE_TOKEN,activeToken);
                editor.putInt(Global.PREF_TYPE_USER, typeUser);
                editor.putInt(Global.PREF_ACTIVE_ID,activeID);
                editor.apply();

                Intent intent = new Intent(LoginScreen.this, MenuPrincipal.class);
                startActivity(intent);

            } else {
                LoginScreen.loading.post(() -> LoginScreen.loading.setVisibility(View.GONE));

                LoginScreen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        user.setText("");
                        password.setText("");
                        Utils.showToast(LoginScreen.this, R.string.Error_user_password, Toast.LENGTH_SHORT);
                        user.requestFocus();
                    }
                });

            }
        }

    }
}