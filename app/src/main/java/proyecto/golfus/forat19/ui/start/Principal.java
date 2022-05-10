package proyecto.golfus.forat19.ui.start;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import proyecto.golfus.forat19.*;

/**
 * Fragment contenedor
 * @author Antonio Rodriguez Sirgado
 */
public class Principal extends Fragment {

    public Principal() {
    }

    public static Principal newInstance() {
        Principal fragment = new Principal();
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
        return inflater.inflate(R.layout.fragment_principal, container, false);
    }
}