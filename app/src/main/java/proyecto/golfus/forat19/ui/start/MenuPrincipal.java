package proyecto.golfus.forat19.ui.start;

import static proyecto.golfus.forat19.utils.Utils.esTablet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.ui.add.AddGame;
import proyecto.golfus.forat19.ui.add.AddPlayer;
import proyecto.golfus.forat19.ui.lists.InstallationsList;
import proyecto.golfus.forat19.ui.lists.UsersList;
import proyecto.golfus.forat19.ui.screens.MyAccount;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.Utils;

import proyecto.golfus.forat19.R.id;

/**
 * Pantalla de menú principal
 *
 * @author Antonio Rodriguez Sirgado
 */
public class MenuPrincipal extends AppCompatActivity implements Observer {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static ProgressBar loadingMenu;
    private TextView userMenu;
    private View view;
    private int userType;
    private String activeUser;
    private MenuItem createGame;

    private boolean openSession;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        createGame = menu.findItem(R.id.createGame);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // comprobamos el tipo de usuario para diferenciar opciones de menu
        // 0 - Admin
        // 1 - Normal
        // 2 - Advance

        switch (userType) {
            case Global.TYPE_ADMIN_USER:
                navigationView.getMenu().setGroupVisible(R.id.adminOption, true);
                break;
            case Global.TYPE_NORMAL_USER:
            case Global.TYPE_ADVANCED_USER:
                navigationView.getMenu().setGroupVisible(R.id.adminOption, false);
                break;
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(Global.TAG, "Item pulsado: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Global.activePlayer != null) {
                    Log.d(Global.TAG, "Jugador seleccionado: " + Global.activePlayer.getUser().getUsername());
                    navigationView.getMenu().findItem(R.id.createGame).setVisible(true);
                } else {
                    Log.d(Global.TAG, "Jugador sin seleccionar");
                    navigationView.getMenu().findItem(R.id.createGame).setVisible(false);
                }
                drawerLayout.openDrawer(GravityCompat.START);
                Utils.hideKeyboard(this);

                return true;

            case id.toolbar_home:
                loadFragment(new Principal());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (esTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_menu_principal);
        setToolBar();

        activeUser = Utils.getActiveUser(this);
        userType = Utils.getActiveTypeUser(this);
        openSession = Utils.getSessionStatus(this);
        loadingMenu = findViewById(R.id.loadingMenu);
        loadingMenu.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navView);
        view = navigationView.getHeaderView(0);
        userMenu = view.findViewById(R.id.userMenu);
        userMenu.setText(activeUser);

        // Capturamos las pulsaciones del menu
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.createPlayer:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    loadFragment(new AddPlayer());
                    break;
                case R.id.createGame:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    loadFragment(new AddGame());
                    break;
                case R.id.searchGreen:
                case R.id.manageGreens:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    loadFragment(new InstallationsList());
                    break;
                case R.id.manageUSers:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    loadFragment(new UsersList());
                    break;
                case R.id.myAccount:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    loadFragment(new MyAccount());
                    break;
                case R.id.closeSession:
                    closeSession();
                    break;
                case R.id.about:
                    // TODO sobre nosotros
                    break;
            }
            return false;
        });


    }

    private void setToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    /**
     * muestra mensaje emergente de consulta
     *
     * @author Antonio Rodriguez Sirgado
     */
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
        int count = getSupportFragmentManager().getBackStackEntryCount();

        Log.d("INFO", "BackStacks: " + count);

        if (count == 0) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                loadFragment(new Principal());
            }

        } else {
            super.onBackPressed();
            //getSupportFragmentManager().popBackStack();
        }


    }

    /**
     * <b>Hace el logout del usuario activo</b><br><br>
     * Mensaje = (token¬device, Logout, null, null)
     *
     * @author Antonio Rodriguez Sirgado
     */
    private void logoutUser() {
        Utils.sendRequest(this, Global.LOGOUT, null, null);
    }

    /**
     * Carga el fragmento recibido por parametro
     *
     * @param fragment fragmento recibido
     * @author Antonio Rodríguez Sirgado
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    /**
     * Permanece a la espera de que las variables cambien
     *
     * @param o   la clase observada
     * @param arg objeto observado
     * @author Antonio Rodriguez Sirgado
     */
    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof Reply) {
            Utils.showSnack(view, R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            MenuPrincipal.loadingMenu.post(() -> MenuPrincipal.loadingMenu.setVisibility(View.INVISIBLE));

        } else if (arg instanceof Message) {
            Message request = (Message) arg;
            String command = request.getCommand();

            switch (command) {
                case Global.LOGOUT:
                    if (request.getParameters().equals(Global.OK)) {

                        Utils.setActiveUser(this, null);
                        Utils.setActiveToken(this, null);
                        Utils.setActiveTypeUser(this, Global.TYPE_NORMAL_USER);

                        Intent intent = new Intent(MenuPrincipal.this, LoginScreen.class);
                        startActivity(intent);
                    } else {
                        Log.d(Global.TAG, request.getMessageText());
                    }
                    break;

            }

        }

    }
}