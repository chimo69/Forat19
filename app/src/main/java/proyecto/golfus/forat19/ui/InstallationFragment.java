package proyecto.golfus.forat19.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import Forat19.Installations;
import proyecto.golfus.forat19.R;

/**
 * Fragment que muestra al Admin los datos de las instalaciones para poder gestionarlas
 * @author Antonio Rodríguez Sirgado
 */
public class InstallationFragment extends Fragment {

    private Installations installation;
    private TextView installationName, address, zip, city, region, country, web, mail, phone, fax, mobile, howToGet, about, services;
    private ImageView logo;

    public InstallationFragment() {
        // Required empty public constructor
    }


    public static InstallationFragment newInstance(String param1, String param2) {
        InstallationFragment fragment = new InstallationFragment();
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
        city=view.findViewById(R.id.installation_city);
        region = view.findViewById(R.id.installation_Region);
        country= view.findViewById(R.id.installation_country);
        web = view.findViewById(R.id.installation_website);
        mail = view.findViewById(R.id.installation_mail);
        phone = view.findViewById(R.id.installation_phone);
        fax = view.findViewById(R.id.installation_fax);
        mobile = view.findViewById(R.id.installation_mobile);
        howToGet = view.findViewById(R.id.installation_howToGet);
        about = view.findViewById(R.id.installation_about);
        services = view.findViewById(R.id.installation_services);
        logo = view.findViewById(R.id.installation_logo);

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

        return view;
    }
}