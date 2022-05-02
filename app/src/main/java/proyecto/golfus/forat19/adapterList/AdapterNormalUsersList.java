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
 * Adaptador encargado de rellenar el RecyclerView de usuarios *
 * @author Antonio Rodriguez Sirgado
 */
public class AdapterNormalUsersList extends RecyclerView.Adapter<AdapterNormalUsersList.ViewHolderList>implements View.OnClickListener{

    ArrayList<Users> listUsers;
    ArrayList<Users> listSearch;
    private final int ADMIN_USER = 0;
    private final int NORMAL_USER= 1;
    private View.OnClickListener listener;

    public AdapterNormalUsersList(ArrayList<Users> listUsers) {

        this.listUsers = listUsers;
        listSearch = new ArrayList<>();
        listSearch.addAll(listUsers);
    }

    @NonNull
    @Override
    public AdapterNormalUsersList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        /*switch (viewType){
            case ADMIN_USER:
        }*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_user_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNormalUsersList.ViewHolderList holder, int position) {
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

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
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
        }

    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return ADMIN_USER;
            case 1:
                return NORMAL_USER;
            default:
                return super.getItemViewType(position);
        }
    }
}
