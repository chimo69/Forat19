package proyecto.golfus.forat19.adapterList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;

/**
 * Adaptador encargado de rellenar el RecyclerView
 *
 * @author Antonio Rodriguez Sirgado
 */
public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolderList>implements View.OnClickListener{

    ArrayList<Users> listUsers;
    ArrayList<Users> listSearch;
    private View.OnClickListener listener;


    public AdapterList(ArrayList<Users> listUsers) {

        this.listUsers = listUsers;
        listSearch = new ArrayList<>();
        listSearch.addAll(listUsers);
    }

    @NonNull
    @Override
    public AdapterList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterList.ViewHolderList holder, int position) {
        holder.fillList(listUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public void filter(String txtSearch) {
        int sizeText = txtSearch.length();
        if (sizeText == 0) {
            listUsers.clear();
            listUsers.addAll(listSearch);
        } else {
            listUsers.clear();
            for (Users l: listSearch) {
                if (l.getUsername().toLowerCase().contains(txtSearch.toLowerCase())||l.getName().toLowerCase().contains(txtSearch.toLowerCase())){
                    listUsers.add(l);
                }
            }
        }
        notifyDataSetChanged();
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

    public class ViewHolderList extends RecyclerView.ViewHolder {
        TextView username;
        TextView name;
        TextView id;
        ImageView imageActive;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            id = itemView.findViewById(R.id.id);
            imageActive = itemView.findViewById(R.id.imageActive);
        }

        /**
         * Rellena cada item del recyclerview con los datos recibidos del usuario
         *
         * @param users usuarios del listado
         * @author Antonio Rodríguez Sirgado
         */
        public void fillList(Users users) {
            username.setText(users.getUsername());
            name.setText(users.getName());

            id.setText(String.format("%06d", users.getId_user()));
            if (users.getActive().equals("S")) {
                id.setBackgroundColor(itemView.getResources().getColor(R.color.green));
            } else {
                id.setBackgroundColor(itemView.getResources().getColor(R.color.error));
            }
            if (users.getId_usertype() == Global.TYPE_ADMIN_USER) {
                imageActive.setVisibility(View.VISIBLE);
            } else {
                imageActive.setVisibility(View.INVISIBLE);
            }
        }


    }
}
