package proyecto.golfus.forat19.ui;

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
public class PrincipalFragment extends Fragment {

    public PrincipalFragment() {
    }

    public static PrincipalFragment newInstance() {
        PrincipalFragment fragment = new PrincipalFragment();

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