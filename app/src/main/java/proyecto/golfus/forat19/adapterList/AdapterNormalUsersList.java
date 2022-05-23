package proyecto.golfus.forat19.adapterList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Forat19.Users;
import proyecto.golfus.forat19.*;

/**
 * Adaptador encargado de rellenar el RecyclerView de usuarios
 *
 * @author Antonio Rodriguez Sirgado
 */
public class AdapterNormalUsersList extends RecyclerView.Adapter<AdapterNormalUsersList.ViewHolderNormalList> implements View.OnClickListener {

    ArrayList<Users> listUsers;
    ArrayList<Users> listSearch;
    Context context;

    private View.OnClickListener listener;

    public AdapterNormalUsersList(ArrayList<Users> listUsers, Context context) {

        this.listUsers = listUsers;
        this.context=context;
        listSearch = new ArrayList<>();
        listSearch.addAll(listUsers);
    }

    @NonNull
    @Override
    public ViewHolderNormalList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_user_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderNormalList(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNormalList holder, int position) {
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
            for (Users l : listSearch) {
                if (l.getUsername().toLowerCase().contains(txtSearch.toLowerCase()) || l.getName().toLowerCase().contains(txtSearch.toLowerCase())) {
                    listUsers.add(l);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    public class ViewHolderNormalList extends RecyclerView.ViewHolder {
        TextView username;
        TextView name;
        CardView cv;

        public ViewHolderNormalList(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            cv = itemView.findViewById(R.id.item_cv_normalUser);
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


}
