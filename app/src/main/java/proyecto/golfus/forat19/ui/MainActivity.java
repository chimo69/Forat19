package proyecto.golfus.forat19.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;


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
 * Clase que coge el token almacenado y lo comprueba en el servidor
 * @author Antonio Rodriguez Sirgado
 */
public class MainActivity extends AppCompatActivity implements Observer {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static Button btn_tryAgain;
    private static ProgressBar loading;
    private ConstraintLayout constraintLayout;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        constraintLayout = findViewById(R.id.layout_main);
        btn_tryAgain = findViewById(R.id.try_again);
        loading = findViewById(R.id.loading);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        loading.setVisibility(View.GONE);
        view = findViewById(R.id.layout_main);

        checkToken();

    }

    /**
     * <b>Comprueba que el token almacenado siga vigente en el servidor y que el usuario haya marcado que quiere dejar la sesion abierta</b><br>
     * Mensaje = (token¬device,ValidateToken,null,null)
     * @author Antonio Rodriguez Sirgado
     */
    private void checkToken() {

        loading.setVisibility(View.VISIBLE);
        String token = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        Boolean session = preferences.getBoolean(Global.PREF_OPEN_KEEP_SESSION_OPEN, false);

        if (session) {
            Message message = new Message(token + "¬" + Utils.getDevice(this), Global.VALIDATE_TOKEN, null, null);
            RequestServer request = new RequestServer();
            request.request(message);
            request.addObserver(this);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginScreen.class);
            startActivity(intent);
        }


    }

    /**
     * Permanece a la espera de que las variables cambien
     * @author Antonio Rodriguez Sirgado
     * @param o la clase observada
     * @param arg objeto observado
     */
    @Override
    public void update(Observable o, Object arg) {

        // Comprobamos si hemos recibido un objeto Reply que sera un error de conexión
        if (arg instanceof Reply) {
            Utils.showSnack(view, ((Reply) arg).getTypeError(), Snackbar.LENGTH_LONG);
            MainActivity.loading.post(() -> MainActivity.loading.setVisibility(View.INVISIBLE));
            MainActivity.btn_tryAgain.post(() -> MainActivity.btn_tryAgain.setVisibility(View.VISIBLE));
            btn_tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.btn_tryAgain.post(() -> MainActivity.btn_tryAgain.setVisibility(View.INVISIBLE));
                    checkToken();
                }
            });

        } else if (arg instanceof Message) {

            Message request = (Message) arg;

            Log.d("INFO", "Token: " + request.getToken());
            Log.d("INFO", "Parametros: " + request.getParameters());
            Log.d("INFO", "Comando: " + request.getCommand());
            Log.d("INFO", "Respuesta: " + request.getMessageText());

            if (request.getCommand().equals(Global.VALIDATE_TOKEN) && request.getParameters().equals(Global.OK)) {

                Log.d("INFO", "Usuario: " + ((Users) request.getObject()).getUsername());
                Log.d("INFO", "Tipo: " + ((Users) request.getObject()).getId_usertype());

                editor.putString(Global.PREF_ACTIVE_USER, ((Users) request.getObject()).getUsername());
                editor.putInt(Global.PREF_TYPE_USER, ((Users) request.getObject()).getId_usertype());
                editor.putInt(Global.PREF_ACTIVE_ID, ((Users) request.getObject()).getId_user());
                editor.apply();

                Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                startActivity(intent);

            } else {

                editor.putString(Global.PREF_ACTIVE_USER, "");
                editor.putInt(Global.PREF_TYPE_USER, Global.TYPE_NORMAL_USER);
                editor.putString(Global.PREF_ACTIVE_TOKEN, null);
                editor.putInt(Global.PREF_ACTIVE_ID, 0);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
            }
        }
    }
}