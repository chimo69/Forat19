package proyecto.golfus.forat19.ui;

import static proyecto.golfus.forat19.utils.Utils.esTablet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;

public class MenuPrincipal extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
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
        activeUser = preferences.getString("activeUser", "");
        userType = preferences.getInt("userType", Global.TYPE_NORMAL_USER);
        openSession = preferences.getBoolean("openSession", false);

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
                case R.id.closeSession:
                    closeSession();
                    break;
                case R.id.about:
                   /*Snackbar.make(view, "Esto es otra prueba", Snackbar.LENGTH_LONG)
                            .setAction("Acción", view -> Log.i("Snackbar", "Pulsada acción snackbar!"))
                            .show();*/

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
            editor.putString(Global.PREF_ACTIVE_USER, "");
            editor.putBoolean(Global.PREF_OPEN_KEEP_SESSION_OPEN, false);
            editor.apply();

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


    private void logoutUser() {

        String token = preferences.getString("Token", "");
        Message message = new Message(token, "Logout", null, null);

        RequestServer request = new RequestServer();
        request.request(message);

        Thread thread = new Thread(() -> {

            while (request.getMessage() == null) {

            }

            Log.d("INFO", "Token: " + request.getMessage().getToken());
            Log.d("INFO", "Parametros: " + request.getMessage().getParameters());
            Log.d("INFO", "Comando: " + request.getMessage().getCommand());


            if (request.getMessage().getParameters().equals("User logged out")) {

                editor.putString(Global.PREF_ACTIVE_USER, "");
                editor.putInt(Global.PREF_TYPE_USER, Global.TYPE_NORMAL_USER);
                editor.apply();

                Intent intent = new Intent(MenuPrincipal.this, LoginScreen.class);
                startActivity(intent);

            } else if (request.getMessage().getParameters().equals("Error")) {

            }
        });
        thread.start();


    }

}