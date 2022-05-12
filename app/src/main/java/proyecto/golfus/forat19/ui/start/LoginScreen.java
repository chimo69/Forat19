package proyecto.golfus.forat19.ui.start;

import static proyecto.golfus.forat19.utils.Utils.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import Forat19.*;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.ui.add.AddUser;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Página de Login, primera página que se muestra al abrir la aplicación
 * @author Antonio Rodriguez Sirgado
 */
public class LoginScreen extends AppCompatActivity implements Observer{


    private ImageView logo;
    private TextView user;
    private TextInputEditText password;
    private Button login, register;
    private SwitchCompat openSession;
    public static ProgressBar loading;
    private View view;

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
                Intent intent = new Intent(LoginScreen.this, AddUser.class);

                Bundle extras = new Bundle();
                extras.putString(Global.EXTRA_USER, user.getText().toString());
                extras.putString(Global.EXTRA_PASSWORD, password.getText().toString());

                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }


    /**
     * <b>Construye el mensaje, lo manda al servidor y espera la respuesta para ver si es válida</b><br>
     * Mensaje = (null¬device,Login,user¬password,null)
     * @author Antonio Rodríguez Sirgado
     */
    private void checkTokenOnline() {
        String sendMessage = user.getText().toString() + "¬" + password.getText().toString();
        Utils.sendRequest(this,  Global.LOGIN,sendMessage,null);
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
            Utils.showSnack(view, R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            LoginScreen.loading.post(() -> LoginScreen.loading.setVisibility(View.GONE));

        }else if (arg instanceof Message){
            Message request = (Message) arg;

            if (request.getParameters().equals(Global.OK)) {
                String activeUser = ((Users) request.getObject()).getUsername();
                int typeUser = ((Users)request.getObject()).getId_user_type();
                String activeToken = request.getToken();
                int activeID = ((Users) request.getObject()).getId_user();

                Utils.setSessionStatus(this,openSession.isChecked());
                Utils.setActiveUser(this,activeUser);
                Utils.setActiveToken(this,activeToken);
                Utils.setActiveTypeUser(this,typeUser);
                Utils.setActiveId(this,activeID);

                Intent intent = new Intent(LoginScreen.this, MenuPrincipal.class);
                startActivity(intent);

            } else {
                LoginScreen.loading.post(() -> LoginScreen.loading.setVisibility(View.GONE));

                LoginScreen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        user.setText("");
                        password.setText("");
                        Utils.showSnack(view, R.string.Error_user_password, Snackbar.LENGTH_LONG);
                        user.requestFocus();
                    }
                });

            }
        }

    }

}