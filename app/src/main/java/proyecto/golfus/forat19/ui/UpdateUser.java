package proyecto.golfus.forat19.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.RequestServer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateUser extends Fragment implements Observer {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView txtUser, txtName, txtMail, txtPhone, txtAddress;
    private TextInputLayout tilUser,tilName,tilMail,tilPassword, tilRePassword, tilPhone,tilAddress;
    private TextInputEditText txtPassword, txtRePassword;
    public static ProgressBar registerLoading;
    private Button btnSave;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpdateUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateUser.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateUser newInstance(String param1, String param2) {
        UpdateUser fragment = new UpdateUser();
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
        View view = inflater.inflate(R.layout.fragment_update_user,container,false);
        txtUser = view.findViewById(R.id.registerId);
        return inflater.inflate(R.layout.fragment_update_user, container, false);
    }

    public void loadInformation(){

        Message mMessage = new Message(null, Global.VALIDATE_TOKEN, "", null);
        RequestServer request = new RequestServer();
        request.request(mMessage);
        request.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}