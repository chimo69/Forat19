package proyecto.golfus.forat19.ui;

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

import Forat19.Golf_Course_Holes;
import Forat19.Golf_Courses;
import proyecto.golfus.forat19.R;

/**
 * Fragment para mostrar la informacion del hoyo seleccionado
 * @author Antonio Rodríguez Sirgado
 */
public class HoleFragment extends Fragment implements Observer {

    private Golf_Course_Holes hole;
    private Golf_Courses course;
    private TextView par, handicap, about, length, numHole;
    private String holeAbout;
    private Button update;
    private int holePar, holeHandicap, holeLength, holeNumber;



    public HoleFragment() {
    }

    public static HoleFragment newInstance() {
        HoleFragment fragment = new HoleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hole = (Golf_Course_Holes) getArguments().getSerializable("hole");
            course = (Golf_Courses) getArguments().getSerializable("course");

            Log.d("INFO","hoyo: "+hole.getId_golf_course_hole());
            Log.d("INFO","hoyo handicap: "+hole.getHandicap());
            Log.d("INFO","hoyo par: "+hole.getPar());

            holePar = hole.getPar();
            holeAbout = hole.getAbout_golf_course_hole();
            holeHandicap = hole.getHandicap();
            holeLength = hole.getLength();
            holeNumber = hole.getId_golf_course_hole();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hole, container, false);
        par = view.findViewById(R.id.hole_par);
        handicap = view.findViewById(R.id.hole_handicap);
        about = view.findViewById(R.id.hole_about);
        length = view.findViewById(R.id.hole_length);
        numHole = view.findViewById(R.id.numHole);
        update=view.findViewById(R.id.btn_updateHoleGo);

        par.setText(Integer.toString(holePar));
        handicap.setText(Integer.toString(holeHandicap));
        about.setText(holeAbout);
        length.setText(Integer.toString(holeLength));
        numHole.setText(getString(R.string.Hole)+": "+Integer.toString(holeNumber));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new UpdateHole(course, hole);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holeContainer, fragment).commit();
            }
        });
        return view;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}