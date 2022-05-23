package proyecto.golfus.forat19.ui.add;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Course_Holes;
import Forat19.Golf_Course_Types;
import Forat19.Golf_Courses;
import Forat19.Installations;
import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.ui.screens.Course;
import proyecto.golfus.forat19.ui.start.Principal;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para agregar un recorrido y sus hoyos
 *
 * @author Antonio Rodríguez Sirgado
 */
public class AddCourse extends Fragment implements Observer {

    private Message request;

    private ArrayList<TextView> txtInfoCourse = new ArrayList<>();
    private ArrayList<TextView> txtHole = new ArrayList<>();
    private Installations installation;
    private Golf_Courses newGolfCourse;
    private TextView title;
    private Spinner combobox_type_course, combobox_holeNumber;
    private EditText name, slope, field_value, about;
    private EditText h1_par, h1_handicap, h1_length, h1_about;
    private EditText h2_par, h2_handicap, h2_length, h2_about;
    private EditText h3_par, h3_handicap, h3_length, h3_about;
    private EditText h4_par, h4_handicap, h4_length, h4_about;
    private EditText h5_par, h5_handicap, h5_length, h5_about;
    private EditText h6_par, h6_handicap, h6_length, h6_about;
    private EditText h7_par, h7_handicap, h7_length, h7_about;
    private EditText h8_par, h8_handicap, h8_length, h8_about;
    private EditText h9_par, h9_handicap, h9_length, h9_about;
    private EditText h10_par, h10_handicap, h10_length, h10_about;
    private EditText h11_par, h11_handicap, h11_length, h11_about;
    private EditText h12_par, h12_handicap, h12_length, h12_about;
    private EditText h13_par, h13_handicap, h13_length, h13_about;
    private EditText h14_par, h14_handicap, h14_length, h14_about;
    private EditText h15_par, h15_handicap, h15_length, h15_about;
    private EditText h16_par, h16_handicap, h16_length, h16_about;
    private EditText h17_par, h17_handicap, h17_length, h17_about;
    private EditText h18_par, h18_handicap, h18_length, h18_about;

    private CardView hole10, hole11, hole12, hole13, hole14, hole15, hole16, hole17, hole18;

    ArrayList<Golf_Course_Types> object_course_types;
    String[] numberHolesOptions = {"9", "18"};


    private FloatingActionButton addHoles;
    private int typeSelected, numberHolesSelected = 18;

    public AddCourse() {
    }

    public static AddCourse newInstance(String param1, String param2) {
        AddCourse fragment = new AddCourse();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = this.getArguments();
            installation = (Installations) args.getSerializable("installation");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_course, container, false);

        title = view.findViewById(R.id.addCourseTitle);
        name = view.findViewById(R.id.addCourse_name);
        slope = view.findViewById(R.id.addCourse_Slope);
        field_value = view.findViewById(R.id.addCourse_FieldValue);

        about = view.findViewById(R.id.addCourse_about);
        addHoles = view.findViewById(R.id.btn_addHoles);
        combobox_type_course = view.findViewById(R.id.addCourse_type);
        combobox_holeNumber = view.findViewById(R.id.addHoleNumber);

        // Añadimos los textViews a un ArrayList
        txtInfoCourse.add(name);
        txtInfoCourse.add(slope);
        txtInfoCourse.add(field_value);

        combobox_holeNumber.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, numberHolesOptions));

        //hole 1
        h1_par = view.findViewById(R.id.hole1_par);
        h1_handicap = view.findViewById(R.id.hole1_handicap);
        h1_length = view.findViewById(R.id.hole1_length);
        h1_about = view.findViewById(R.id.hole1_about);
        txtHole.add(h1_par);
        txtHole.add(h1_handicap);
        txtHole.add(h1_length);

        //hole 2
        h2_par = view.findViewById(R.id.hole2_par);
        h2_handicap = view.findViewById(R.id.hole2_handicap);
        h2_length = view.findViewById(R.id.hole2_length);
        h2_about = view.findViewById(R.id.hole2_about);
        txtHole.add(h2_par);
        txtHole.add(h2_handicap);
        txtHole.add(h2_length);

        //hole 3
        h3_par = view.findViewById(R.id.hole3_par);
        h3_handicap = view.findViewById(R.id.hole3_handicap);
        h3_length = view.findViewById(R.id.hole3_length);
        h3_about = view.findViewById(R.id.hole3_about);
        txtHole.add(h3_par);
        txtHole.add(h3_handicap);
        txtHole.add(h3_length);

        //hole 4
        h4_par = view.findViewById(R.id.hole4_par);
        h4_handicap = view.findViewById(R.id.hole4_handicap);
        h4_length = view.findViewById(R.id.hole4_length);
        h4_about = view.findViewById(R.id.hole4_about);
        txtHole.add(h4_par);
        txtHole.add(h4_handicap);
        txtHole.add(h4_length);

        //hole 5
        h5_par = view.findViewById(R.id.hole5_par);
        h5_handicap = view.findViewById(R.id.hole5_handicap);
        h5_length = view.findViewById(R.id.hole5_length);
        h5_about = view.findViewById(R.id.hole5_about);
        txtHole.add(h5_par);
        txtHole.add(h5_handicap);
        txtHole.add(h5_length);

        //hole 6
        h6_par = view.findViewById(R.id.hole6_par);
        h6_handicap = view.findViewById(R.id.hole6_handicap);
        h6_length = view.findViewById(R.id.hole6_length);
        h6_about = view.findViewById(R.id.hole6_about);
        txtHole.add(h6_par);
        txtHole.add(h6_handicap);
        txtHole.add(h6_length);

        //hole 7
        h7_par = view.findViewById(R.id.hole7_par);
        h7_handicap = view.findViewById(R.id.hole7_handicap);
        h7_length = view.findViewById(R.id.hole7_length);
        h7_about = view.findViewById(R.id.hole7_about);
        txtHole.add(h7_par);
        txtHole.add(h7_handicap);
        txtHole.add(h7_length);

        //hole 8
        h8_par = view.findViewById(R.id.hole8_par);
        h8_handicap = view.findViewById(R.id.hole8_handicap);
        h8_length = view.findViewById(R.id.hole8_length);
        h8_about = view.findViewById(R.id.hole8_about);
        txtHole.add(h8_par);
        txtHole.add(h8_handicap);
        txtHole.add(h8_length);

        //hole 9
        h9_par = view.findViewById(R.id.hole9_par);
        h9_handicap = view.findViewById(R.id.hole9_handicap);
        h9_length = view.findViewById(R.id.hole9_length);
        h9_about = view.findViewById(R.id.hole9_about);
        txtHole.add(h9_par);
        txtHole.add(h9_handicap);
        txtHole.add(h9_length);

        //hole 10
        h10_par = view.findViewById(R.id.hole10_par);
        h10_handicap = view.findViewById(R.id.hole10_handicap);
        h10_length = view.findViewById(R.id.hole10_length);
        h10_about = view.findViewById(R.id.hole10_about);
        txtHole.add(h10_par);
        txtHole.add(h10_handicap);
        txtHole.add(h10_length);

        //hole 11
        h11_par = view.findViewById(R.id.hole11_par);
        h11_handicap = view.findViewById(R.id.hole11_handicap);
        h11_length = view.findViewById(R.id.hole11_length);
        h11_about = view.findViewById(R.id.hole11_about);
        txtHole.add(h11_par);
        txtHole.add(h11_handicap);
        txtHole.add(h11_length);

        //hole 12
        h12_par = view.findViewById(R.id.hole12_par);
        h12_handicap = view.findViewById(R.id.hole12_handicap);
        h12_length = view.findViewById(R.id.hole12_length);
        h12_about = view.findViewById(R.id.hole12_about);
        txtHole.add(h12_par);
        txtHole.add(h12_handicap);
        txtHole.add(h12_length);

        //hole 13
        h13_par = view.findViewById(R.id.hole13_par);
        h13_handicap = view.findViewById(R.id.hole13_handicap);
        h13_length = view.findViewById(R.id.hole13_length);
        h13_about = view.findViewById(R.id.hole13_about);
        txtHole.add(h13_par);
        txtHole.add(h13_handicap);
        txtHole.add(h13_length);

        //hole 14
        h14_par = view.findViewById(R.id.hole14_par);
        h14_handicap = view.findViewById(R.id.hole14_handicap);
        h14_length = view.findViewById(R.id.hole14_length);
        h14_about = view.findViewById(R.id.hole14_about);
        txtHole.add(h14_par);
        txtHole.add(h14_handicap);
        txtHole.add(h14_length);

        //hole 15
        h15_par = view.findViewById(R.id.hole15_par);
        h15_handicap = view.findViewById(R.id.hole15_handicap);
        h15_length = view.findViewById(R.id.hole15_length);
        h15_about = view.findViewById(R.id.hole15_about);
        txtHole.add(h15_par);
        txtHole.add(h15_handicap);
        txtHole.add(h15_length);

        //hole 16
        h16_par = view.findViewById(R.id.hole16_par);
        h16_handicap = view.findViewById(R.id.hole16_handicap);
        h16_length = view.findViewById(R.id.hole16_length);
        h16_about = view.findViewById(R.id.hole16_about);
        txtHole.add(h16_par);
        txtHole.add(h16_handicap);
        txtHole.add(h16_length);

        //hole 17
        h17_par = view.findViewById(R.id.hole17_par);
        h17_handicap = view.findViewById(R.id.hole17_handicap);
        h17_length = view.findViewById(R.id.hole17_length);
        h17_about = view.findViewById(R.id.hole17_about);
        txtHole.add(h17_par);
        txtHole.add(h17_handicap);
        txtHole.add(h17_length);

        //hole 18
        h18_par = view.findViewById(R.id.hole18_par);
        h18_handicap = view.findViewById(R.id.hole18_handicap);
        h18_length = view.findViewById(R.id.hole18_length);
        h18_about = view.findViewById(R.id.hole18_about);
        txtHole.add(h18_par);
        txtHole.add(h18_handicap);
        txtHole.add(h18_length);

        hole10 = view.findViewById(R.id.hole10);
        hole11 = view.findViewById(R.id.hole11);
        hole12 = view.findViewById(R.id.hole12);
        hole13 = view.findViewById(R.id.hole13);
        hole14 = view.findViewById(R.id.hole14);
        hole15 = view.findViewById(R.id.hole15);
        hole16 = view.findViewById(R.id.hole16);
        hole17 = view.findViewById(R.id.hole17);
        hole18 = view.findViewById(R.id.hole18);

        title.setText(installation.getInstallation());

        name.requestFocus();

        loadCourseTypes();

        // combobox tipo de recorrido
        combobox_type_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSelected = object_course_types.get(position).getId_golf_course_type();
                Log.d(Global.TAG, "Tipo recorrido seleccionado: " + typeSelected);

                if (typeSelected == Global.TYPE_PANDP || typeSelected == Global.TYPE_PAR3) {
                    enablePar(false);
                } else {
                    enablePar(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // combobox cantidad de hoyos
        combobox_holeNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberHolesSelected = Integer.parseInt((String) combobox_holeNumber.getSelectedItem());
                if (numberHolesSelected == 18) {
                    show18Holes(true);
                } else {
                    show18Holes(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Boton de añadir hoyos
        addHoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(getActivity());
                fillAllData();
            }
        });
        return view;
    }

    /**
     * Crea un objeto Golf_Course_holes y lo rellena con los datos de pantalla, despues crea un objeto Golf_Course y lo rellena con los holes
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void fillAllData() {
        ArrayList<Golf_Course_Holes> holes = new ArrayList<>();
        int idInst = installation.getId_installation();
        String nameCourse = name.getText().toString();
        String aboutInst = about.getText().toString();


        if (checkErrorsLocal()) {
            Float fieldValueInst = Float.parseFloat(field_value.getText().toString());
            int slopeInst = Integer.parseInt(slope.getText().toString());

            //hole 1
            int par = Integer.parseInt(h1_par.getText().toString());
            int handicap = Integer.parseInt(h1_handicap.getText().toString());
            int length = Integer.parseInt(h1_length.getText().toString());
            String about = h1_about.getText().toString();
            Golf_Course_Holes hole1;
            hole1 = new Golf_Course_Holes(0, 1, par, handicap, length, about);
            holes.add(hole1);

            /*
            // Bucle falseado para probar rellenar todos los campos
            for (int h = 1; h < numberHolesSelected + 1; h++) {
                hole1 = new Golf_Course_Holes(0, h, par, handicap, length, about);
                holes.add(hole1);
            }*/


            //hole 2
            par = Integer.parseInt(h2_par.getText().toString());
            handicap = Integer.parseInt(h2_handicap.getText().toString());
            length = Integer.parseInt(h2_length.getText().toString());
            about = h2_about.getText().toString();
            Golf_Course_Holes hole2 = new Golf_Course_Holes(0, 2, par, handicap, length, about);
            holes.add(hole2);


            //hole 3
            par = Integer.parseInt(h3_par.getText().toString());
            handicap = Integer.parseInt(h3_handicap.getText().toString());
            length = Integer.parseInt(h3_length.getText().toString());
            about = h3_about.getText().toString();
            Golf_Course_Holes hole3 = new Golf_Course_Holes(0, 3, par, handicap, length, about);
            holes.add(hole3);

            //hole 4
            par = Integer.parseInt(h4_par.getText().toString());
            handicap = Integer.parseInt(h4_handicap.getText().toString());
            length = Integer.parseInt(h4_length.getText().toString());
            about = h4_about.getText().toString();
            Golf_Course_Holes hole4 = new Golf_Course_Holes(0, 4, par, handicap, length, about);
            holes.add(hole4);

            //hole 5
            par = Integer.parseInt(h5_par.getText().toString());
            handicap = Integer.parseInt(h5_handicap.getText().toString());
            length = Integer.parseInt(h5_length.getText().toString());
            about = h5_about.getText().toString();
            Golf_Course_Holes hole5 = new Golf_Course_Holes(0, 5, par, handicap, length, about);
            holes.add(hole5);

            //hole 6
            par = Integer.parseInt(h6_par.getText().toString());
            handicap = Integer.parseInt(h6_handicap.getText().toString());
            length = Integer.parseInt(h6_length.getText().toString());
            about = h6_about.getText().toString();
            Golf_Course_Holes hole6 = new Golf_Course_Holes(0, 6, par, handicap, length, about);
            holes.add(hole6);

            //hole 7
            par = Integer.parseInt(h7_par.getText().toString());
            handicap = Integer.parseInt(h7_handicap.getText().toString());
            length = Integer.parseInt(h7_length.getText().toString());
            about = h7_about.getText().toString();
            Golf_Course_Holes hole7 = new Golf_Course_Holes(0, 7, par, handicap, length, about);
            holes.add(hole7);

            //hole 8
            par = Integer.parseInt(h8_par.getText().toString());
            handicap = Integer.parseInt(h8_handicap.getText().toString());
            length = Integer.parseInt(h8_length.getText().toString());
            about = h8_about.getText().toString();
            Golf_Course_Holes hole8 = new Golf_Course_Holes(0, 8, par, handicap, length, about);
            holes.add(hole8);

            if (numberHolesSelected == 18) {
                //hole 9
                par = Integer.parseInt(h9_par.getText().toString());
                handicap = Integer.parseInt(h9_handicap.getText().toString());
                length = Integer.parseInt(h9_length.getText().toString());
                about = h9_about.getText().toString();
                Golf_Course_Holes hole9 = new Golf_Course_Holes(0, 9, par, handicap, length, about);
                holes.add(hole9);

                //hole 10
                par = Integer.parseInt(h10_par.getText().toString());
                handicap = Integer.parseInt(h10_handicap.getText().toString());
                length = Integer.parseInt(h10_length.getText().toString());
                about = h10_about.getText().toString();
                Golf_Course_Holes hole10 = new Golf_Course_Holes(0, 10, par, handicap, length, about);
                holes.add(hole10);

                //hole 11
                par = Integer.parseInt(h11_par.getText().toString());
                handicap = Integer.parseInt(h11_handicap.getText().toString());
                length = Integer.parseInt(h11_length.getText().toString());
                about = h11_about.getText().toString();
                Golf_Course_Holes hole11 = new Golf_Course_Holes(0, 11, par, handicap, length, about);
                holes.add(hole11);

                //hole 12
                par = Integer.parseInt(h12_par.getText().toString());
                handicap = Integer.parseInt(h12_handicap.getText().toString());
                length = Integer.parseInt(h12_length.getText().toString());
                about = h12_about.getText().toString();
                Golf_Course_Holes hole12 = new Golf_Course_Holes(0, 12, par, handicap, length, about);
                holes.add(hole12);

                //hole 13
                par = Integer.parseInt(h13_par.getText().toString());
                handicap = Integer.parseInt(h13_handicap.getText().toString());
                length = Integer.parseInt(h13_length.getText().toString());
                about = h13_about.getText().toString();
                Golf_Course_Holes hole13 = new Golf_Course_Holes(0, 13, par, handicap, length, about);
                holes.add(hole13);

                //hole 14
                par = Integer.parseInt(h14_par.getText().toString());
                handicap = Integer.parseInt(h14_handicap.getText().toString());
                length = Integer.parseInt(h14_length.getText().toString());
                about = h14_about.getText().toString();
                Golf_Course_Holes hole14 = new Golf_Course_Holes(0, 14, par, handicap, length, about);
                holes.add(hole14);

                //hole 15
                par = Integer.parseInt(h15_par.getText().toString());
                handicap = Integer.parseInt(h15_handicap.getText().toString());
                length = Integer.parseInt(h15_length.getText().toString());
                about = h15_about.getText().toString();
                Golf_Course_Holes hole15 = new Golf_Course_Holes(0, 15, par, handicap, length, about);
                holes.add(hole15);

                //hole 16
                par = Integer.parseInt(h16_par.getText().toString());
                handicap = Integer.parseInt(h16_handicap.getText().toString());
                length = Integer.parseInt(h16_length.getText().toString());
                about = h16_about.getText().toString();
                Golf_Course_Holes hole16 = new Golf_Course_Holes(0, 16, par, handicap, length, about);
                holes.add(hole16);

                //hole 17
                par = Integer.parseInt(h17_par.getText().toString());
                handicap = Integer.parseInt(h17_handicap.getText().toString());
                length = Integer.parseInt(h17_length.getText().toString());
                about = h17_about.getText().toString();
                Golf_Course_Holes hole17 = new Golf_Course_Holes(0, 17, par, handicap, length, about);
                holes.add(hole17);

                //hole 18
                par = Integer.parseInt(h18_par.getText().toString());
                handicap = Integer.parseInt(h18_handicap.getText().toString());
                length = Integer.parseInt(h18_length.getText().toString());
                about = h18_about.getText().toString();
                Golf_Course_Holes hole18 = new Golf_Course_Holes(0, 18, par, handicap, length, about);
                holes.add(hole18);
            }


            newGolfCourse = new Golf_Courses(0, idInst, nameCourse, typeSelected, 0, 0, 0, "N", fieldValueInst, slopeInst, aboutInst, holes);
            sendNewCourse();

        }


    }


    /**
     * <b>Carga los tipos de recorrido para mostrarlos en un combobox</b><br>
     * Mensaje = (token¬device, ListGolfCourseType, id usuario, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadCourseTypes() {

        //Utils.sendRequest(getActivity(),Global.LIST_GOLF_COURSES_TYPE,null,null);
        Forat19.Message message = new Forat19.Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(requireContext()), Global.LIST_GOLF_COURSES_TYPE, Utils.getActiveId(getActivity()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
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
        // comprueba si ha recibido un objeto Reply que será un error de conexión
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (arg instanceof Reply) {
                    Utils.showSnack(getView(), ((Reply) arg).getTypeError(), Snackbar.LENGTH_LONG);
                    Fragment fragment = new Principal();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

                } else if (arg instanceof Message) {

                    request = (Message) arg;
                    String command = request.getCommand();
                    String parameter = request.getParameters();

                    switch (command) {
                        case Global.LIST_GOLF_COURSES_TYPE:
                            ArrayList<String> course_types = new ArrayList<>();
                            object_course_types = (ArrayList<Golf_Course_Types>) request.getObject();

                            for (int i = 0; i < object_course_types.size(); i++) {
                                course_types.add(object_course_types.get(i).getGolf_course_type());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, course_types);
                            combobox_type_course.setAdapter(adapter);
                            break;
                        case Global.ADD_GOLF_COURSE:
                            if (parameter.equals(Global.OK)) {

                                Utils.showToast(getActivity(), getString(R.string.course_added_ok), Toast.LENGTH_SHORT);
                                Fragment fragment = new Course();
                                Bundle args = new Bundle();
                                args.putSerializable("course", newGolfCourse);
                                fragment.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

                            } else if (parameter.equals("Error:1")) {

                                Log.d(Global.TAG, "Slope: " + ((Golf_Courses) request.getObject()).getSlope_value());
                                Log.d(Global.TAG, "Name: " + ((Golf_Courses) request.getObject()).getGolf_course());
                                Log.d(Global.TAG, "Valor campo: " + ((Golf_Courses) request.getObject()).getField_value());

                                Utils.showSnack(getView(), R.string.course_added_error, Snackbar.LENGTH_LONG);

                                checkErrorsServer((Golf_Courses) request.getObject());

                            } else {
                                Utils.showSnack(getView(), R.string.course_added_error, Snackbar.LENGTH_LONG);
                            }
                            break;
                    }

                }
            }
        });


    }

    /**
     * Muestra un error y pone el foco en el TextView enviado
     *
     * @param textView Donde debe poner el foco
     * @author Antonio Rodríguez Sirgado
     */
    public void showError(TextView textView, String textError) {
        Utils.showSnack(getView(), textError, Snackbar.LENGTH_LONG);
        textView.requestFocus();
    }

    /**
     * <b>Envia el mensaje con el nuevo recorrido creado</b><br>
     * Mensaje = (token¬device, addGolfCourse, id usuario, course)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void sendNewCourse() {

        Forat19.Message message = new Forat19.Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(requireContext()), Global.ADD_GOLF_COURSE, Utils.getActiveId(getActivity()), newGolfCourse);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * Muestra u oculta el grupo de 8 o 19 hoyos
     * @author Antonio Rodríguez Sirgado
     * @param visible estado en que quieres que esté
     */
    public void show18Holes(Boolean visible) {
        if (visible) {
            hole10.setVisibility(View.VISIBLE);
            hole11.setVisibility(View.VISIBLE);
            hole12.setVisibility(View.VISIBLE);
            hole13.setVisibility(View.VISIBLE);
            hole14.setVisibility(View.VISIBLE);
            hole15.setVisibility(View.VISIBLE);
            hole16.setVisibility(View.VISIBLE);
            hole17.setVisibility(View.VISIBLE);
            hole18.setVisibility(View.VISIBLE);
        } else {
            hole10.setVisibility(View.GONE);
            hole11.setVisibility(View.GONE);
            hole12.setVisibility(View.GONE);
            hole13.setVisibility(View.GONE);
            hole14.setVisibility(View.GONE);
            hole15.setVisibility(View.GONE);
            hole16.setVisibility(View.GONE);
            hole17.setVisibility(View.GONE);
            hole18.setVisibility(View.GONE);
        }

    }

    /**
     * Comprueba si estan introducidos todos los campos
     * @return devuelve true si esta todo correcto
     */
    private Boolean checkErrorsLocal() {

        for (TextView t : txtInfoCourse) {
            if (t.getText().toString().isEmpty()) {
                t.requestFocus();
                showError(t, getString(R.string.missing_field_to_be_filled_in));
                return false;
            }
        }

        for (int i = 0; i < numberHolesSelected; i++) {
            if (txtHole.get(i * 3).getText().toString().isEmpty()) {
                txtHole.get(i * 3).requestFocus();
                showError(txtHole.get(i * 3), getString(R.string.missing_field_to_be_filled_in));
                return false;
            } else if (txtHole.get((i * 3) + 1).getText().toString().isEmpty()) {
                txtHole.get((i * 3) + 1).requestFocus();
                showError(txtHole.get((i * 3) + 1), getString(R.string.missing_field_to_be_filled_in));
                return false;
            } else if (txtHole.get((i * 3) + 2).getText().toString().isEmpty()) {
                txtHole.get((i * 3) + 2).requestFocus();
                showError(txtHole.get((i * 3) + 2), getString(R.string.missing_field_to_be_filled_in));
                return false;
            }
        }

        return true;
    }

    /**
     * Comprueba los datos en el servidor
     * @param golf_courses recorrido a comprobar
     * @author Antonio Rodríguez Sirgado
     */
    private void checkErrorsServer(Golf_Courses golf_courses) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (golf_courses.getGolf_course().equals("*")) {
                    showError(name, "Error, el nombre debe ser como minimo de x caracteres");
                    return;
                } else if (golf_courses.getSlope_value() == -1) {
                    showError(slope, "Error, el slope debe ser entre 55-155");
                    return;
                } else if (golf_courses.getField_value() == -1) {
                    showError(field_value, "Error, el valor debe ser inferior a 100");
                    return;
                } else if (golf_courses.getAbout_golf_course().equals("*")) {
                    showError(about, "Error, tamaño de texto excesivo");
                    return;
                }

                ArrayList<Golf_Course_Holes> hole = (ArrayList<Golf_Course_Holes>) golf_courses.getList_golf_course_holes();

                Log.d(Global.TAG, "Comprobando hoyos: " + numberHolesSelected);
                for (int i = 0; i < numberHolesSelected; i++) {
                    if (hole.get(i).getPar() == -1) {
                        Log.d(Global.TAG, "Error en el Par / hoyo: " + (i + 1));
                        showError(txtHole.get((i * 3)), "El par debe ser 3,4 o 5");
                        i = numberHolesSelected;
                    } else if (hole.get(i).getHandicap() == -1) {
                        Log.d(Global.TAG, "Error en el Handicap / hoyo: " + (i + 1));
                        showError(txtHole.get((i * 3) + 1), "El handicap debe ser como máximo " + numberHolesSelected);
                        txtHole.get((i * 3) + 1).setError("El handicap debe ser como máximo " + numberHolesSelected);
                        i = numberHolesSelected;
                    }
                }

            }
        });
    }

    /**
     * Dependiendo de el tipo de recorrido activa o no los campos de Par
     * @param enabled estado de los campos Par
     */
    private void enablePar(Boolean enabled) {
        if (enabled) {
            for (int i = 0; i < numberHolesSelected; i++) {
                txtHole.get(i * 3).setText("");
                txtHole.get(i * 3).setEnabled(true);
            }
        } else {
            for (int i = 0; i < numberHolesSelected; i++) {
                txtHole.get(i * 3).setText("3");
                txtHole.get(i * 3).setEnabled(false);
            }

        }

    }
}