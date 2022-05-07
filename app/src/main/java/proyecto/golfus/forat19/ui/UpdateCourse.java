package proyecto.golfus.forat19.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Courses;
import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

public class UpdateCourse extends Fragment implements Observer {

    private TextView name, slope, field, about;
    private Button updateCourse;
    private Golf_Courses course;
    private Message request;

    public UpdateCourse() {
        // Required empty public constructor
    }

    public static UpdateCourse newInstance() {
        UpdateCourse fragment = new UpdateCourse();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = this.getArguments();
            course = (Golf_Courses) args.getSerializable("course");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_course, container, false);
        name = view.findViewById(R.id.editCourseName);
        slope = view.findViewById(R.id.editCourseSlope);
        field = view.findViewById(R.id.editCourseField);
        about = view.findViewById(R.id.editCourseAbout);
        updateCourse = view.findViewById(R.id.btn_updateCourse);

        name.setText(course.getGolf_course());
        slope.setText(String.valueOf(course.getSlope_value()));
        field.setText(String.valueOf(course.getField_value()));
        about.setText(course.getAbout_golf_course());

        updateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUpdateCourse();
            }
        });
        return view;
    }

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

            if (command.equals(Global.UPDATE_GOLF_COURSE)) {

                Fragment fragment = new CourseFragment();
                Bundle args = new Bundle();
                args.putSerializable("course", course);
                fragment.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();

            }
        }
    }

    public void sendUpdateCourse () {

        course.setAbout_golf_course(about.getText().toString());
        course.setField_value(Float.parseFloat(field.getText().toString()));
        course.setGolf_course(name.getText().toString());
        course.setSlope_value(Integer.parseInt(slope.getText().toString()));

        Forat19.Message message = new Forat19.Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(requireContext()), Global.UPDATE_GOLF_COURSE, Utils.getActiveId(getActivity()), course);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }
}