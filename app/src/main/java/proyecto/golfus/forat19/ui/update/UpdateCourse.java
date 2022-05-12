package proyecto.golfus.forat19.ui.update;

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
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.ui.screens.Course;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para la actualizacion de un recorrido
 * @author Antonio Rodríguez Sirgado
 */
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

    /**
     * Permanece a la espera de que el objeto observado varie
     * @author Antonio Rodriguez Sirgado
     * @param o clase observada
     * @param arg objeto observado
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

            if (command.equals(Global.UPDATE_GOLF_COURSE)) {

                Fragment fragment = new Course();
                Bundle args = new Bundle();
                args.putSerializable("course", course);
                fragment.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();

            }
        }
    }

    /**
     * <b>Envia mensaje para la actualizacion del recorrido</b><br>
     * Mensaje = (token¬device, updateGolfCourse, id usuario, course)
     * @author Antonio Rodríguez Sirgado
     */
    public void sendUpdateCourse () {

        course.setAbout_golf_course(about.getText().toString());
        course.setField_value(Float.parseFloat(field.getText().toString()));
        course.setGolf_course(name.getText().toString());
        course.setSlope_value(Integer.parseInt(slope.getText().toString()));

        //Utils.sendRequest(getActivity(),Global.UPDATE_GOLF_COURSE, Utils.getActiveId(getActivity()), course);
        Message message = new Message(Utils.getActiveToken(getActivity())+"¬"+Utils.getDevice(getActivity()),Global.UPDATE_GOLF_COURSE, Utils.getActiveId(getActivity()), course);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);

    }
}