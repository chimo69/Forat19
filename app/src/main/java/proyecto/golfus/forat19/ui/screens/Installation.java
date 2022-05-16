package proyecto.golfus.forat19.ui.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import Forat19.Golf_Courses;
import Forat19.Installations;
import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.adapterList.AdapterCoursesList;
import proyecto.golfus.forat19.ui.add.AddCourse;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment que muestra al Admin los datos de las instalaciones para poder gestionarlas
 *
 * @author Antonio Rodríguez Sirgado
 */
public class Installation extends Fragment {

    private Installations installation;
    private Message request;
    private ImageButton btnPhone, btnMail, btnWeb, btnMap;
    private TextView installationName, address, zip, city, region, country, web, mail, phone, fax, mobile, howToGet, about, services, infoCourses;
    private FloatingActionButton addCourse;
    private ArrayList<Golf_Courses> listCourses;
    private ImageView logo;
    private RecyclerView recyclerView;
    private AdapterCoursesList adapterCoursesList;

    public Installation() {
    }

    public static Installation newInstance(String param1, String param2) {
        Installation fragment = new Installation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = this.getArguments();
            installation = (Installations) args.getSerializable("installation");
            request = (Message) args.getSerializable("course");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installation, container, false);
        installationName = view.findViewById(R.id.installation_name);
        address = view.findViewById(R.id.installation_address);
        zip = view.findViewById(R.id.installation_zip);
        city = view.findViewById(R.id.installation_city);
        region = view.findViewById(R.id.installation_Region);
        country = view.findViewById(R.id.installation_country);
        web = view.findViewById(R.id.installation_website);
        mail = view.findViewById(R.id.installation_mail);
        phone = view.findViewById(R.id.installation_phone);
        fax = view.findViewById(R.id.installation_fax);
        mobile = view.findViewById(R.id.installation_mobile);
        howToGet = view.findViewById(R.id.installation_howToGet);
        about = view.findViewById(R.id.installation_about);
        services = view.findViewById(R.id.installation_services);
        logo = view.findViewById(R.id.installation_logo);
        btnMail = view.findViewById(R.id.btn_installation_mail);
        btnMap = view.findViewById(R.id.btn_installation_map);
        btnPhone = view.findViewById(R.id.btn_installation_phone);
        btnWeb = view.findViewById(R.id.btn_installation_web);
        recyclerView = view.findViewById(R.id.recyclerListCourses);
        infoCourses = view.findViewById(R.id.infoCourses);
        addCourse = view.findViewById(R.id.btn_addCourse);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Rellenamos los datos obtenidos del servidor
        installationName.setText(installation.getInstallation());
        address.setText(installation.getAddress());
        zip.setText(installation.getZip_code());
        city.setText(installation.getCity());
        region.setText(installation.getRegion());
        country.setText(installation.getCountry());
        web.setText(installation.getWeb_site());
        mail.setText(installation.getEmail());
        phone.setText(installation.getPhone());
        fax.setText(installation.getFax());
        mobile.setText(installation.getMobile());
        howToGet.setText(installation.getHow_to_get());
        about.setText(installation.getAbout_installation());
        services.setText(installation.getServices());

        Utils.hideKeyboard(getActivity());

        // boton de ir a web
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(web.getText().toString()));

                try {
                    startActivity(intent);

                } catch (android.content.ActivityNotFoundException ex) {
                    Utils.showSnack(getView(), getString(R.string.cant_open_web), Snackbar.LENGTH_SHORT);
                }

            }
        });

        // boton de llamar
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone.getText().toString()));

                    try {
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Utils.showSnack(getView(), getString(R.string.call_could_not_be_made), Snackbar.LENGTH_SHORT);
                    }

                }
            }
        });

        // boton de correo
        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {mail.getText().toString()};

                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, TO);

                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Utils.showSnack(getView(), R.string.mail_manager_error, Snackbar.LENGTH_SHORT);
                }
            }
        });

        // boton de mapa
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = "41.565";
                String longitude = "2.33376";
                Uri uri = Uri.parse("geo:" + latitude + "," + longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");

                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Utils.showSnack(getView(), getString(R.string.Location_could_not_be_opened), Snackbar.LENGTH_SHORT);
                }
            }
        });


        listCourses = (ArrayList<Golf_Courses>) request.getObject();

        // Boton añadir recorrido
        if (Utils.getActiveTypeUser(getActivity()) == Global.TYPE_ADMIN_USER) {
            addCourse.setVisibility(View.VISIBLE);
            addCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new AddCourse();
                    Bundle args = new Bundle();
                    args.putInt("idInstallation", installation.getId_installation());
                    args.putString("nameInstallation", installation.getInstallation());
                    args.putSerializable("installation", installation);
                    fragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "addCourse").addToBackStack(null).commit();
                }
            });
        }


        Log.d(Global.TAG, "Numero de recorridos: " + listCourses.size());
        Log.d(Global.TAG, "-------------------------------------------------");
        adapterCoursesList = new AdapterCoursesList(listCourses, getContext());
        recyclerView.setAdapter(adapterCoursesList);

        adapterCoursesList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.d(Global.TAG, "Recorrido seleccionado: " + listCourses.get(recyclerView.getChildAdapterPosition(view)).getGolf_course());
                Log.d(Global.TAG, "-------------------------------------------------");
                Fragment fragment = new Course();
                Bundle args = new Bundle();
                args.putSerializable("course", listCourses.get(recyclerView.getChildAdapterPosition(view)));
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
            }
        });
        if (adapterCoursesList.getItemCount() == 0) {
            infoCourses.setText(R.string.without_courses);
        }

        recyclerView.setAdapter(adapterCoursesList);

        return view;
    }
}