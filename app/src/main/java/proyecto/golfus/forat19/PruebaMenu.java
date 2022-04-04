package proyecto.golfus.forat19;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PruebaMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PruebaMenu extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ACTIVE_USER = "active_user";

    private String activeUser;
    private ImageButton btnLogout;
    private TextView txtActiveUser;

    View vista;

    SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    // TODO: Rename and change types of parameters
    private String mParam1;


    public PruebaMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.     *
     * @return A new instance of fragment PruebaMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static PruebaMenu newInstance(String param1) {
        PruebaMenu fragment = new PruebaMenu();
        Bundle args = new Bundle();
        args.putString(ACTIVE_USER, param1);

        fragment.setArguments(args);


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            activeUser = getArguments().getString(ACTIVE_USER);
        }

        preferences= this.getActivity().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        activeUser=preferences.getString("activeUser","");
        editor = preferences.edit();

        Log.d("ERROR", activeUser);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        vista = inflater.inflate(R.layout.fragment_menu, container, false);
        txtActiveUser = (TextView) vista.findViewById(R.id.txtActiveUser);
        txtActiveUser.setText(activeUser);
        btnLogout = (ImageButton) vista.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
       @Override
            public void onClick(View v) {


           editor.putString("activeUser","");
           editor.putBoolean("openSession",false);
           editor.apply();
            Intent intent=new Intent(vista.getContext(),LoginScreen.class);
                startActivity(intent);

            }
        });
        return vista;
    }
}