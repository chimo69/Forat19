package proyecto.golfus.forat19.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import Forat19.Golf_Course_Holes;
import proyecto.golfus.forat19.R;

/**
 * Fragment para mostrar la informacion del hoyo seleccionado
 * @author Antonio Rodríguez Sirgado
 */
public class HoleFragment extends Fragment {

    private Golf_Course_Holes hole;
    private TextView par, handicap, about, length;


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
        length = view.findViewById(R.id.hole1_length);

        par.setText(hole.getPar());
        handicap.setText(hole.getHandicap());
        about.setText(hole.getAbout_golf_course_hole());
        length.setText(hole.getLength());

        return view;
    }
}