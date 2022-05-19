package proyecto.golfus.forat19;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Forat19.Golf_Games;


public class Game extends Fragment {

    private Golf_Games game;

    public Game() {
        // Required empty public constructor
    }

    public Game(Golf_Games gg) {
    game = gg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        Log.d(Global.TAG, "Tipo objeto recibido: "+game.getClass().getName());
        return view;
    }
}