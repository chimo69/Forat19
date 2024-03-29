package proyecto.golfus.forat19.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import Forat19.Installations;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment que muestra al Admin los datos de las instalaciones para poder gestionarlas
 * @author Antonio Rodríguez Sirgado
 */
public class InstallationFragment extends Fragment {

    private Installations installation;
    private ImageButton btnPhone, btnMail, btnWeb, btnMap;
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
        btnMail = view.findViewById(R.id.btn_installation_mail);
        btnMap = view.findViewById(R.id.btn_installation_map);
        btnPhone = view.findViewById(R.id.btn_installation_phone);
        btnWeb = view.findViewById(R.id.btn_installation_web);

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

        // boton de ir a web
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(web.getText().toString()));
                startActivity(intent);
            }
        });

        // boton de llamar
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},123);
                }else{
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+phone.getText().toString()));
                    startActivity(intent);
                }
            }
        });

        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {mail.getText().toString()};

                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, TO);

                try {
                    startActivity(intent);
                }catch (android.content.ActivityNotFoundException ex){
                    Utils.showToast(getActivity(),getString(R.string.mail_manager_error), Toast.LENGTH_SHORT);
                }
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude="41.565";
                String longitude="2.33376";
                Uri uri = Uri.parse("geo:"+latitude+","+longitude);
                Intent intent = new Intent (Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        return view;
    }
}