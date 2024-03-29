package proyecto.golfus.forat19.adapterList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Forat19.Installations;
import proyecto.golfus.forat19.*;

/**
 * Adaptador encargado de rellenar el RecyclerView de instalaciones
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterInstallationsList extends RecyclerView.Adapter<AdapterInstallationsList.ViewHolderList> implements View.OnClickListener {

    ArrayList<Installations> listInstallations;
    ArrayList<Installations> listSearch;
    private View.OnClickListener listener;

    public AdapterInstallationsList(ArrayList<Installations> listInstallations) {
        this.listInstallations = listInstallations;
        Log.d("INFO","Instal recibida: " + listInstallations.get(0).getInstallation());
        listSearch = new ArrayList<>();
        listSearch.addAll(listInstallations);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
        if (listener!=null){
            listener.onClick(view);
        }
    }

    @NonNull
    @Override
    public AdapterInstallationsList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installation_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterInstallationsList.ViewHolderList holder, int position) {
        holder.fillList(listInstallations.get(position));
    }

    @Override
    public int getItemCount() {
        return listInstallations.size();
    }

    public void filter(String txtSearch) {
        int sizeText = txtSearch.length();
        if (sizeText == 0) {
            listInstallations.clear();
            listInstallations.addAll(listSearch);
        } else {
            listInstallations.clear();
            for (Installations i: listSearch) {
                if (i.getInstallation().toLowerCase().contains(txtSearch.toLowerCase())){
                    listInstallations.add(i);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView installation;
        TextView address;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.installationListLogo);
            installation = itemView.findViewById(R.id.installationList_installation);
            address = itemView.findViewById(R.id.installationList_address);

        }

        /**
         * Rellena cada item del recyclerview con los datos recibidos de las instalaciones
         *
         * @param installations instalaciones del listado
         * @author Antonio Rodríguez Sirgado
         */
        public void fillList(Installations installations) {
            installation.setText(installations.getInstallation());
            address.setText(installations.getAddress());

        }

    }
}
