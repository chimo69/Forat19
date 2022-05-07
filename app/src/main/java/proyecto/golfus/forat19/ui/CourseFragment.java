package proyecto.golfus.forat19.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Course_Holes;
import Forat19.Golf_Course_Types;
import Forat19.Golf_Courses;
import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para mostrar la informacion del recorrido seleccionado
 *
 * @author Antonio Rodríguez Sirgado
 */
public class CourseFragment extends Fragment implements View.OnClickListener , Observer {

    private Golf_Courses golf_course;
    private ArrayList<Golf_Course_Holes> holesList;
    private TextView name, type, holes, par, length, field, slope, about;
    private Button h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, h13, h14, h15, h16, h17, h18;
    private CheckBox handicap;
    private Message request;
    private String courseType;
    private CardView infoHole;

    public CourseFragment() {
        // Required empty public constructor
    }


    public static CourseFragment newInstance(String param1, String param2) {
        CourseFragment fragment = new CourseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = this.getArguments();
            golf_course = (Golf_Courses) args.getSerializable("course");
            holesList = (ArrayList<Golf_Course_Holes>) golf_course.getList_golf_course_holes();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        name = view.findViewById(R.id.golfCourseName);
        type = view.findViewById(R.id.golfCourseType);
        holes = view.findViewById(R.id.golfCourseHoles);
        par = view.findViewById(R.id.golfCoursePar);
        length = view.findViewById(R.id.golfCourseLength);
        handicap = view.findViewById(R.id.golfCourseHandicap);
        field = view.findViewById(R.id.golfCourseField);
        slope = view.findViewById(R.id.golfCourseSlope);
        about = view.findViewById(R.id.golfCourseAbout);
        infoHole = view.findViewById(R.id.cardHoles);

        h1 = view.findViewById(R.id.btn_hole1);
        h2 = view.findViewById(R.id.btn_hole2);

        h1.setOnClickListener(this);
        h2.setOnClickListener(this);

        handicap.setEnabled(false);
        loadCourseType(golf_course.getId_golf_course_type());

        showInfo();

        if (golf_course.getHoles()>0) {
            h1.setVisibility(View.VISIBLE);
            h2.setVisibility(View.VISIBLE);
            h3.setVisibility(View.VISIBLE);
            h4.setVisibility(View.VISIBLE);
            h5.setVisibility(View.VISIBLE);
            h6.setVisibility(View.VISIBLE);
            h7.setVisibility(View.VISIBLE);
            h8.setVisibility(View.VISIBLE);
            h9.setVisibility(View.VISIBLE);
        }

        /*if (golf_course.getHoles()>9){
            h10.setVisibility(View.VISIBLE);
            h11.setVisibility(View.VISIBLE);
            h12.setVisibility(View.VISIBLE);
            h13.setVisibility(View.VISIBLE);
            h14.setVisibility(View.VISIBLE);
            h15.setVisibility(View.VISIBLE);
            h16.setVisibility(View.VISIBLE);
            h17.setVisibility(View.VISIBLE);
            h18.setVisibility(View.VISIBLE);
        }*/

        return view;
    }

    /**
     * Carga el tipo de recorrido
     *
     * @param id recorrido a mostrar
     */
    private void loadCourseType(int id) {

        Forat19.Message message = new Forat19.Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(requireContext()), Global.GET_GOLF_COURSE_TYPE, Integer.toString(id), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * Muestra la información del recorrido en pantalla
     */
    public void showInfo() {
        // rellenamos la información con los datos recibidos
        name.setText(golf_course.getGolf_course());
        type.setText(courseType);
        holes.setText(Integer.toString(golf_course.getHoles()));
        par.setText(Integer.toString(golf_course.getPar()));
        length.setText(Integer.toString(golf_course.getLength()));
        if (golf_course.getHandicap_calculation().equals("Y")) {
            handicap.setChecked(true);
        } else {
            handicap.setChecked(false);
        }
        field.setText(Float.toString(golf_course.getField_value()));
        slope.setText(Integer.toString(golf_course.getSlope_value()));
        about.setText(golf_course.getAbout_golf_course());

    }

    /**
     * Carga el fragment de hoyo seleccionado     *
     * @param idHole hoyo a mostrar.
     */
    public void loadHole(int idHole) {

        Golf_Course_Holes holeSelected = holesList.get(idHole - 1);
        Fragment fragment = new HoleFragment();
        Bundle args = new Bundle();
        args.putSerializable("hole", holeSelected);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holeContainer, fragment).commit();
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
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            Fragment fragment = new PrincipalFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {

            request = (Message) arg;
            String command = request.getCommand();

            if (command.equals(Global.GET_GOLF_COURSE_TYPE)) {
                courseType = ((Golf_Course_Types) request.getObject()).getGolf_course_type();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_hole1:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(1);
                break;
            case R.id.btn_hole2:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(2);
                break;
            case R.id.btn_hole3:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(3);
                break;
            case R.id.btn_hole4:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(4);
                break;
            case R.id.btn_hole5:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(5);
                break;
            case R.id.btn_hole6:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(6);
                break;
            case R.id.btn_hole7:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(7);
                break;
            case R.id.btn_hole8:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(8);
                break;
            case R.id.btn_hole9:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(9);
                break;
            case R.id.btn_hole10:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(10);
                break;
            case R.id.btn_hole11:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(11);
                break;
            case R.id.btn_hole12:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(12);
                break;
            case R.id.btn_hole13:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(13);
                break;
            case R.id.btn_hole14:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(14);
                break;
            case R.id.btn_hole15:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(15);
                break;
            case R.id.btn_hole16:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(16);
                break;
            case R.id.btn_hole17:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(17);
                break;
            case R.id.btn_hole18:
                infoHole.setVisibility(View.VISIBLE);
                loadHole(18);
                break;
        }
    }
}