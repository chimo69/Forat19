package proyecto.golfus.forat19.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Course_Types;
import Forat19.Golf_Courses;
import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;


public class CourseFragment extends Fragment implements Observer {

    private SharedPreferences preferences;
    private Golf_Courses golf_course;
    private TextView name, type, holes, par, length,  field, slope, about;
    private CheckBox handicap;
    private Message request;
    private String courseType;

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
        preferences = this.getActivity().getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        if (getArguments() != null){
            Bundle args = this.getArguments();
            golf_course = (Golf_Courses) args.getSerializable("course");
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
        handicap.setEnabled(false);
        loadCourseType(golf_course.getId_golf_course_type());
        showInfo();

        return view;
    }

    private void loadCourseType(int id) {
        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);

        Forat19.Message message = new Forat19.Message(activeToken + "¬" + Utils.getDevice(requireContext()), Global.GET_GOLF_COURSE_TYPE, Integer.toString(id), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    public void showInfo (){
        // rellenamos la información con los datos recibidos
        name.setText(golf_course.getGolf_course());
        type.setText(courseType);
        holes.setText(Integer.toString(golf_course.getHoles()));
        par.setText(Integer.toString(golf_course.getPar()));
        length.setText(Integer.toString(golf_course.getLength()));
        if (golf_course.getHandicap_calculation().equals("Y")){
            handicap.setChecked(true);
        }else{
            handicap.setChecked(false);
        }
        field.setText(Float.toString(golf_course.getField_value()));
        slope.setText(Integer.toString(golf_course.getSlope_value()));
        about.setText(golf_course.getAbout_golf_course());

    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
            Fragment fragment = new PrincipalFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (arg instanceof Message) {

            request = (Message) arg;
            String command = request.getCommand();

            if (command.equals(Global.GET_GOLF_COURSE_TYPE)){
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
}