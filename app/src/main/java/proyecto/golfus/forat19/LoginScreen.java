package proyecto.golfus.forat19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class LoginScreen extends AppCompatActivity {

    private TextView user, password;
    private Button login, register;
    private Switch openSession;
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PASSWORD = "password";
    SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        user = (TextView) findViewById(R.id.txtUser);
        password = (TextView) findViewById(R.id.txtName);
        login = (Button) findViewById(R.id.btnLogin);
        register = (Button) findViewById(R.id.btnRegister);
        openSession = (Switch) findViewById(R.id.openSession);

        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();


        if (checkToken()) {
            Intent intent = new Intent(LoginScreen.this, PrincipalScreen.class);
            startActivity(intent);
        } else {

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkTokenOnline()) {
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
        }
        return false;
    }

    private boolean checkTokenOnline() {
        return false; // TODO
    }


}