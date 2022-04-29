package proyecto.golfus.forat19.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Installations;
import Forat19.Message;

import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.adapterList.AdapterInstallationsList;
import proyecto.golfus.forat19.adapterList.AdapterUsersList;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Pantalla que muestra el listado de instalaciones
 * @author Antonio Rodríguez Sirgado
 */
public class InstallationsList extends Fragment implements Observer, SearchView.OnQueryTextListener {

    private SharedPreferences preferences;
    private Message request;
    private ArrayList<Installations> listInstallations;
    private RecyclerView recyclerView;
    private static ProgressBar loading;
    private SearchView searchInstallationList;
    private AdapterInstallationsList adapterList;

    public InstallationsList() {
    }

    public static InstallationsList newInstance(String param1, String param2) {
        InstallationsList fragment = new InstallationsList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getActivity().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installation_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerListInstallations);
        loading = view.findViewById(R.id.installationList_loading);
        searchInstallationList = view.findViewById(R.id.searchInstallationList);
        loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        searchInstallationList.setOnQueryTextListener(this);
        loading.setVisibility(View.VISIBLE);

        loadInstallations();

        return view;

    }

    public void loadInstallations(){
        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);

        Forat19.Message message = new Forat19.Message(activeToken + "¬" + Utils.getDevice(requireContext()), Global.LIST_INSTALLATIONS, Integer.toString(activeID), null);
        Log.d("INFO", "Enviando token: " + activeToken);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterList.filter(newText);
        return false;
    }

    /**
     * Permanece a la espera de que las variables cambien
     * @author Antonio Rodriguez Sirgado
     * @param o la clase observada
     * @param arg objeto observado
     */
    @Override
    public void update(Observable o, Object arg) {
        // comprueba si ha recibido un objeto Reply que será un error de conexión
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            Fragment fragment = new PrincipalFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {


            request = (Message) arg;
            String command = request.getCommand();

            Log.d("INFO", "Token recibido: " + request.getToken());
            Log.d("INFO", "Parametros recibido: " + request.getParameters());
            Log.d("INFO", "Comando recibido: " + request.getCommand());

            listInstallations = (ArrayList<Installations>) request.getObject();



            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapterList = new AdapterInstallationsList(listInstallations);
                    recyclerView.setAdapter(adapterList);
                    adapterList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            /*
                            if (listInstallations.get(recyclerView.getChildAdapterPosition(view)).getId_user()!=0){
                                Log.d("INFO","Usuario seleccionado: "+listUsers.get(recyclerView.getChildAdapterPosition(view)).getName());

                                Fragment fragment = new AccountAdmin();
                                Bundle args = new Bundle();
                                args.putSerializable("user", listUsers.get(recyclerView.getChildAdapterPosition(view)));

                                fragment.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                            }*/

                        }
                    });
                }
            });

            InstallationsList.loading.post(() -> InstallationsList.loading.setVisibility(View.INVISIBLE));


        }
    }
}