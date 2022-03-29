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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import proyecto.golfus.forat19.utils.Comunicaciones;

public class LoginScreen extends AppCompatActivity {

    private TextView user;
    private TextInputEditText password;

    private Button login, register;
    private SwitchCompat openSession;
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PASSWORD = "password";
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
        setContentView(R.layout.activity_login_screen);

        user = findViewById(R.id.txtLoginUser);
        password = findViewById(R.id.txtLoginPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.btnRegister);
        openSession = findViewById(R.id.openSession);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (checkToken()) {
            Intent intent = new Intent(LoginScreen.this, PrincipalScreen.class);
            startActivity(intent);
        } else {

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkToken()) {
                        editor.putString("user", user.getText().toString());
                        editor.putBoolean("openSession", openSession.isChecked());
                        editor.apply();

                        Intent intent = new Intent(LoginScreen.this, PrincipalScreen.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginScreen.this, "Error: Usuario desconocido", Toast.LENGTH_SHORT).show();
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

        if (!preferences.getString("user", "").equals("") && preferences.getBoolean("openSession", false)) {
            return true;
        } else {

            return checkTokenOnline();
        }

    }

    private boolean checkTokenOnline(){

        Comunicaciones com = new Comunicaciones();
        Log.d("ERROR","Enviando datos");

        Token token = new Token("Chimo69","1234");

        com.execute(token);


        return false; // TODO

    }


}