package proyecto.golfus.forat19.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.android.material.navigation.NavigationView;

import proyecto.golfus.forat19.R;

public class MenuPrincipal extends AppCompatActivity  {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView userMenu;
    private View view;
    private int userType;

    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // comprobamos el tipo de usuario para diferenciar opciones de menu
        // 0 - Admin
        // 1 - User

        switch (userType) {
            case 0:
                navigationView.getMenu().setGroupVisible(R.id.adminOption, true);
                break;
            case 1:
                navigationView.getMenu().setGroupVisible(R.id.adminOption, false);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        setToolBar();
        preferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        String activeUser = preferences.getString("activeUser", "");
        userType = preferences.getInt("userType", 0);
        editor = preferences.edit();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navView);

        view = navigationView.getHeaderView(0);
        userMenu = view.findViewById(R.id.userMenu);
        userMenu.setText(activeUser);

        // Capturamos las pulsaciones del menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.closeSession:
                        closeSession();
                        break;
                }
                return false;
            }
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
        confirmation.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putString("activeUser", "");
                editor.putBoolean("openSession", false);
                editor.apply();

                Intent intent = new Intent(MenuPrincipal.this, LoginScreen.class);
                startActivity(intent);
            }
        });
        confirmation.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        confirmation.show();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}