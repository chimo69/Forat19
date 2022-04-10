package proyecto.golfus.forat19.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccount extends Fragment implements Observer {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView txtUserInfo, txtNameInfo, txtPhoneInfo, txtMailInfo, txtAddressInfo;
    private Button btnDelete;
    private SharedPreferences preferences;


    public MyAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAccount newInstance(String param1, String param2) {
        MyAccount fragment = new MyAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        preferences = this.getActivity().getSharedPreferences("Credentials",Context.MODE_PRIVATE);
        txtUserInfo= view.findViewById(R.id.txtUserInfo);
        txtMailInfo= view.findViewById(R.id.txtMailInfo);
        txtPhoneInfo= view.findViewById(R.id.txtPhoneInfo);
        txtAddressInfo= view.findViewById(R.id.txtAddressInfo);
        txtNameInfo = view.findViewById(R.id.txtNameInfo);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });
        txtUserInfo.setText("Chimo69");
        txtPhoneInfo.setText("654276522");
        txtMailInfo.setText("chimo69@gmail.com");
        txtNameInfo.setText("Antonio Rodriguez Sirgado de todos los santos");
        txtAddressInfo.setText("Av. del Tanct 78 1A, El Vendrell(TGN) ");



        return view;
    }

    private void deleteUser() {
        String token = preferences.getString(Global.PREF_ACTIVE_TOKEN,null);
        Users userToDelete= new Users(1, "chimo", "Antonio Rodriguez", "1234", 1, "S", "chimo69@gmail.com", "654276522", "Avenida del Tancat");
        Message mMessage = new Message(token,Global.DELETE_USER,null,userToDelete);

        RequestServer request = new RequestServer();
        request.request(mMessage);
        request.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {

        Message request = (Message) arg;

        if (request.getParameters().equals(Global.USER_UPDATED)){
            Log.d("INFO",request.getParameters());
        }else if (request.getCommand().equals(Global.ERROR)){
            Log.d("INFO",request.getParameters());
        };

    }
}