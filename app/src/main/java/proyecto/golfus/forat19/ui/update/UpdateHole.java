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

import Forat19.Golf_Course_Holes;
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
 * Fragment para la actualizacion de hoyos
 *
 * @author Antonio Rodríguez Sirgado
 */
public class UpdateHole extends Fragment implements Observer {


    private static final String ARG_PARAM1 = "hole";
    private static final String ARG_PARAM2 = "course";
    private TextView about, holeNumber;
    private Button update;
    private Message request;

    private Golf_Course_Holes hole;
    private Golf_Courses course;


    public UpdateHole() {
        // Required empty public constructor
    }

    public UpdateHole(Golf_Courses course, Golf_Course_Holes hole) {
        this.hole = hole;
        this.course = course;
    }

    public static UpdateHole newInstance(Golf_Courses course, Golf_Course_Holes hole) {
        UpdateHole fragment = new UpdateHole();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, hole);
        args.putSerializable(ARG_PARAM2, course);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hole = (Golf_Course_Holes) getArguments().getSerializable(ARG_PARAM1);
            course = (Golf_Courses) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_hole, container, false);

        about = view.findViewById(R.id.holeEditAbout);
        update = view.findViewById(R.id.btn_updateHole);
        holeNumber = view.findViewById(R.id.holeNumber);

        about.setText(hole.getAbout_golf_course_hole());
        holeNumber.setText(String.valueOf(hole.getId_golf_course_hole()));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUpdateHole();
            }
        });
        return view;
    }

    /**
     * <b>Envia mensaje para la actualizacion del hoyo</b><br>
     * Mensaje = (token¬device, updateGolfCourseHole, idCourse¬idHole, hole)
     */
    private void sendUpdateHole() {
        hole.setAbout_golf_course_hole(about.getText().toString());
        int idHole = hole.getId_golf_course_hole();
        int idCourse = hole.getId_golf_course();
        //Utils.sendRequest(getActivity(),Global.UPDATE_GOLF_COURSE_HOLE, idCourse + "¬" + idHole, hole);
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.UPDATE_GOLF_COURSE_HOLE, idCourse + "¬" + idHole, hole);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * Permanece a la espera de que el objeto observado varie
     *
     * @param o   clase observada
     * @param arg objeto observado
     * @author Antonio Rodriguez Sirgado
     */
    @Override
    public void update(Observable o, Object arg) {
        if (getActivity() == null) {
            return;
        }
        // comprueba si ha recibido un objeto Reply que será un error de conexión
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), ((Reply) arg).getTypeError(), Snackbar.LENGTH_LONG);
            Fragment fragment = new Principal();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {

            request = (Message) arg;
            String command = request.getCommand();
            String parameter = request.getParameters();

            if (command.equals(Global.UPDATE_GOLF_COURSE_HOLE) && parameter.equals(Global.OK)) {
                Fragment fragment = new Course();
                Bundle args = new Bundle();
                args.putSerializable("course", course);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();

            }
        }
    }
}