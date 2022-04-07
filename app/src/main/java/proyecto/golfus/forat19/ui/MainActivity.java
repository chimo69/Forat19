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

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;

public class MainActivity extends AppCompatActivity {

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
        String token = preferences.getString("Token", "");
        Message message = new Message(token, "ValidateToken", null, null);

        RequestServer request = new RequestServer();
        request.request(message);

        Thread thread = new Thread(() -> {

            while (request.getMessage() == null) {

            }
            Log.d("INFO", "Token: " + request.getMessage().getToken());
            Log.d("INFO", "Parametros: " + request.getMessage().getParameters());
            Log.d("INFO", "Comando: " + request.getMessage().getCommand());


            if (request.getMessage().getParameters().equals("TokenValidated")) {

                Log.d("INFO", "Usuario: " + ((Users) request.getMessage().getObject()).getUsername());
                Log.d("INFO", "Tipo: " + ((Users) request.getMessage().getObject()).getId_usertype());

                String user = ((Users) request.getMessage().getObject()).getUsername();
                int typeUser = ((Users) request.getMessage().getObject()).getId_usertype();
                String activeToken = request.getMessage().getToken();

                editor.putString(Global.PREF_ACTIVE_USER, user);
                editor.putInt(Global.PREF_TYPE_USER, typeUser);
                editor.putString(Global.PREF_ACTIVE_TOKEN,token);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                startActivity(intent);

            } else if (request.getMessage().getParameters().equals("Error")) {

                Log.d("INFO", "Motivo error: " + request.getMessage().getParameters());
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);

            }
        });
        thread.start();


    }
}