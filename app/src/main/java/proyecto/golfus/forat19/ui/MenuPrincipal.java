package proyecto.golfus.forat19.ui;

import static proyecto.golfus.forat19.utils.Utils.esTablet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;

public class MenuPrincipal extends AppCompatActivity implements Observer {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ProgressBar loadingMenu;
    private TextView userMenu;
    private View view;
    private int userType;
    private String activeUser;

    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private boolean openSession;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // comprobamos el tipo de usuario para diferenciar opciones de menu
        // 0 - Admin
        // 1 - Normal

        switch (userType) {
            case Global.TYPE_ADMIN_USER:
                navigationView.getMenu().setGroupVisible(R.id.adminOption, true);
                break;
            case Global.TYPE_NORMAL_USER:
                navigationView.getMenu().setGroupVisible(R.id.adminOption, false);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (esTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        setToolBar();
        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        activeUser = preferences.getString(Global.PREF_ACTIVE_USER, "");
        userType = preferences.getInt(Global.PREF_TYPE_USER, Global.TYPE_NORMAL_USER);
        openSession = preferences.getBoolean(Global.PREF_OPEN_KEEP_SESSION_OPEN, false);
        loadingMenu = findViewById(R.id.loadingMenu);

        editor = preferences.edit();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navView);

        view = navigationView.getHeaderView(0);
        userMenu = view.findViewById(R.id.userMenu);
        userMenu.setText(activeUser);


        Log.d("INFO", "Usuario activo: " + activeUser);

        // Capturamos las pulsaciones del menu
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.manageUSers:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    loadFragment(new MyAccount());
                    break;
                case R.id.closeSession:
                    closeSession();
                    break;
                case R.id.about:

                    break;
            }
            return false;
        });
    }

    private void setToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void closeSession() {

        AlertDialog.Builder confirmation = new AlertDialog.Builder(this);
        confirmation.setTitle(R.string.attention);
        confirmation.setMessage(R.string.do_you_want_close_session);
        confirmation.setCancelable(true);
        confirmation.setPositiveButton(R.string.yes, (dialog, which) -> {
            loadingMenu.setVisibility(View.VISIBLE);
            logoutUser();
        });
        confirmation.setNegativeButton(R.string.Cancel, (dialog, which) -> {
        });

        confirmation.show();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (openSession) {
                moveTaskToBack(true);
            } else {
                closeSession();
            }
        }
    }

    /**
     * Hace el logout del usuario activo
     * Mensaje = (token, Logout, null, null)
     */
    private void logoutUser() {

        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);

        Message message = new Message(activeToken, Global.LOGOUT, null, null);
        Log.d("INFO","Enviando token: "+activeToken);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    /**
     * Permanece a la espera de que las variables cambien
     *
     * @param o   la clase observada
     * @param arg objeto obserbado
     */
    @Override
    public void update(Observable o, Object arg) {

        Message request = (Message) arg;

        Log.d("INFO", "Token: " + request.getToken());
        Log.d("INFO", "Parametros: " + request.getParameters());
        Log.d("INFO", "Comando: " + request.getCommand());

        String command = request.getCommand();

        switch (command) {
            case Global.LOGOUT:
                if (request.getParameters().equals(Global.USER_LOGED_OUT)) {

                    editor.putString(Global.PREF_ACTIVE_USER, null);
                    editor.putInt(Global.PREF_TYPE_USER, Global.TYPE_NORMAL_USER);
                    editor.putString(Global.PREF_ACTIVE_TOKEN,null);
                    editor.apply();

                    Intent intent = new Intent(MenuPrincipal.this, LoginScreen.class);
                    startActivity(intent);
                }else{
                    Log.d("INFO",request.getParameters());
                }
                break;

        }

    }
}