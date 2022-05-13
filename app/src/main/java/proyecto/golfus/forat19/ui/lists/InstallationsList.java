package proyecto.golfus.forat19.ui.lists;

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
import proyecto.golfus.forat19.ui.screens.Installation;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Pantalla que muestra el listado de instalaciones
 *
 * @author Antonio Rodríguez Sirgado
 */
public class InstallationsList extends Fragment implements Observer, SearchView.OnQueryTextListener {

    private Message request;
    private ArrayList<Installations> listInstallations;
    private RecyclerView recyclerView;
    private static ProgressBar loading;
    private SearchView searchInstallationList;
    private AdapterInstallationsList adapterList;
    private Installations selectedInstallation;


    public InstallationsList() {
    }

    public static InstallationsList newInstance(String param1, String param2) {
        InstallationsList fragment = new InstallationsList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchInstallationList.setOnQueryTextListener(this);
        loading.setVisibility(View.VISIBLE);

        loadInstallations();

        return view;

    }

    /**
     * <b>Pide al servidor informacion de las instalaciones</b>
     * Mensaje = (token¬device, listInstallation, null, null)
     * @author Antonio Rodríguez Sirgado
     */
    public void loadInstallations() {
        //Utils.sendRequest(getActivity(),Global.LIST_INSTALLATIONS,null,null);
        Message message = new Message(Utils.getActiveToken(getActivity())+"¬"+Utils.getDevice(getActivity()),Global.LIST_INSTALLATIONS,null,null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Pide al servidor informacion de los recorridos</b><br>
     * Mensaje = (token¬device, listGolfCourse, id instalacion, null)
     * @author Antonio Rodríguez Sirgado
     */
    public void loadGolfCourse(int installationId) {
        //Utils.sendRequest(getActivity(),Global.LIST_GOLF_COURSES,Integer.toString(installationId),null);
        loading.setVisibility(View.VISIBLE);
        Message message = new Message(Utils.getActiveToken(getActivity())+"¬"+Utils.getDevice(getActivity()),Global.LIST_GOLF_COURSES,Integer.toString(installationId),null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Actua cuando el texto introduccido en el campo de busqueda cambia
     *
     * @param newText texto introducido en el campo de busqueda
     * @return false
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        adapterList.filter(newText);
        return false;
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
        // comprueba si ha recibido un objeto Reply que será un error de conexión
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            Fragment fragment = new Principal();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {

            request = (Message) arg;
            String command = request.getCommand();

            if (command.equals(Global.LIST_INSTALLATIONS)) {

                // Rellenamos el Arraylist con el objeto recibido
                listInstallations = (ArrayList<Installations>) request.getObject();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterList = new AdapterInstallationsList(listInstallations, getContext());
                        recyclerView.setAdapter(adapterList);
                        adapterList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(Global.TAG, "Instalación seleccionada: " + listInstallations.get(recyclerView.getChildAdapterPosition(view)).getInstallation());
                                Log.d(Global.TAG,"-------------------------------------------------");
                                selectedInstallation = listInstallations.get(recyclerView.getChildAdapterPosition(view));
                                loadGolfCourse(listInstallations.get(recyclerView.getChildAdapterPosition(view)).getId_installation());

                            }
                        });
                    }
                });
            } else if (command.equals(Global.LIST_GOLF_COURSES)) {

                Fragment fragment = new Installation();
                Bundle args = new Bundle();

                args.putSerializable("installation", selectedInstallation);
                args.putSerializable("course", request);

                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();

            }

        }
        InstallationsList.loading.post(() -> InstallationsList.loading.setVisibility(View.INVISIBLE));
    }
}