package proyecto.golfus.forat19.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;

public class MainActivity extends AppCompatActivity implements Observer {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        loading = findViewById(R.id.loading);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        loading.setVisibility(View.GONE);

        checkToken();

    }

    private void checkToken() {


        loading.setVisibility(View.VISIBLE);
        String token = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);

        Message message = new Message(token, Global.VALIDATE_TOKEN, null, null);

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

        if (request.getParameters().equals(Global.TOKEN_VALIDATED)) {

            Log.d("INFO", "Usuario: " + ((Users) request.getObject()).getUsername());
            Log.d("INFO", "Tipo: " + ((Users) request.getObject()).getId_usertype());

            editor.putString(Global.PREF_ACTIVE_USER, ((Users) request.getObject()).getUsername());
            editor.putInt(Global.PREF_TYPE_USER, ((Users) request.getObject()).getId_usertype());
            editor.putString(Global.PREF_ACTIVE_TOKEN, request.getToken());
            editor.putInt(Global.PREF_ACTIVE_ID,((Users) request.getObject()).getId_user());
            editor.apply();

            Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
            startActivity(intent);

        } else if (request.getCommand().equals(Global.ERROR)) {

            editor.putString(Global.PREF_ACTIVE_USER, "");
            editor.putInt(Global.PREF_TYPE_USER, Global.TYPE_NORMAL_USER);
            editor.putString(Global.PREF_ACTIVE_TOKEN, null);
            editor.putInt(Global.PREF_ACTIVE_ID,0);
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginScreen.class);
            startActivity(intent);

        }
    }
}