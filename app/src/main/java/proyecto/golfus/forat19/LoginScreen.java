package proyecto.golfus.forat19;

import static proyecto.golfus.forat19.utils.Services.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import proyecto.golfus.forat19.utils.Comunicaciones;
import Forat19.*;

public class LoginScreen extends AppCompatActivity {

    private TextView user;
    private TextInputEditText password;

    private Button login, register;
    private SwitchCompat openSession;

    public static ProgressBar loading;

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PASSWORD = "password";

    SharedPreferences preferences;
    private SharedPreferences.Editor editor;


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

        user = findViewById(R.id.txtLoginUser);
        password = findViewById(R.id.txtLoginPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.btnRegister);
        openSession = findViewById(R.id.openSession);

        loading=(ProgressBar) findViewById(R.id.progressBar);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();


        // Comprobamos token en el dispositivo
        if (checkToken()) {
            Intent intent = new Intent(LoginScreen.this, PrincipalScreen.class);
            startActivity(intent);
        } else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    loading.setVisibility(View.VISIBLE);

                    if (user.length()>0 && password.length()>0){
                        // Comprobamos token en servidor
                        if (checkTokenOnline()) {
                            loading.setVisibility(View.GONE);
                            editor.putString("activeUser", user.getText().toString());
                            editor.putBoolean("openSession", openSession.isChecked());
                            editor.apply();

                            Intent intent = new Intent(LoginScreen.this, PrincipalScreen.class);
                            startActivity(intent);
                        } else {

                            Toast.makeText(LoginScreen.this, "Error: Usuario desconocido", Toast.LENGTH_SHORT).show();

                        }
                    }
                    //loading.setVisibility(View.INVISIBLE);
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

        if (!preferences.getString("user", "").equals("") && preferences.getBoolean("openSession", false)) {
            return true;
        }
        return false;
    }

    private boolean checkTokenOnline(){

        Comunicaciones com = new Comunicaciones();
        Log.d("ERROR", "Enviando datos");

        //Forat19.Users user = new Users(1,"chimo69","Antonio Rodriguez","",1,"S",null,null,null);

        String sendMessage=user.getText().toString()+"¬"+password.getText().toString();
        Forat19.Message message = new Message(null,"Loggin",sendMessage,null);

        Log.d("INFO:","ENVIO: "+sendMessage);

        return com.checkToken(message);
    }


}