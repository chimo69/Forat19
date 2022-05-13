package proyecto.golfus.forat19.adapterList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import Forat19.Users;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;

/**
 * Adaptador encargado de rellenar el RecyclerView de usuarios *
 * @author Antonio Rodriguez Sirgado
 */
public class AdapterAdminUsersList extends RecyclerView.Adapter<AdapterAdminUsersList.ViewHolderList>implements View.OnClickListener{

    ArrayList<Users> listUsers;
    ArrayList<Users> listSearch;
    private View.OnClickListener listener;
    Context context;

    public AdapterAdminUsersList(ArrayList<Users> listUsers, Context context) {

        this.listUsers = listUsers;
        this.context= context;
        listSearch = new ArrayList<>();
        listSearch.addAll(listUsers);
    }

    @NonNull
    @Override
    public AdapterAdminUsersList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAdminUsersList.ViewHolderList holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition));
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
        CardView cv;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            id = itemView.findViewById(R.id.id);
            imageActive = itemView.findViewById(R.id.imageActive);
            cv = itemView.findViewById(R.id.item_cv_userAdmin);
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

            if (users.getActive().equals("Y")) {
                id.setBackgroundColor(itemView.getResources().getColor(R.color.green));
            } else {
                id.setBackgroundColor(itemView.getResources().getColor(R.color.error));
            }
            if (users.getId_user()==0){
                id.setBackgroundColor(itemView.getResources().getColor(R.color.grey));
            }
            if (users.getId_user_type() == Global.TYPE_ADMIN_USER) {
                imageActive.setVisibility(View.VISIBLE);
            } else {
                imageActive.setVisibility(View.INVISIBLE);
            }
        }


    }
}
