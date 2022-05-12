package proyecto.golfus.forat19.adapterList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Forat19.Users;
import proyecto.golfus.forat19.R;

/**
 * Adaptador encargado de rellenar el recyclerView de jugadores de un juego
 *
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterPlayersList extends RecyclerView.Adapter<AdapterPlayersList.ViewHolderList> implements View.OnClickListener {
    ArrayList<Users> listUsers;
    private View.OnClickListener listener;

    public AdapterPlayersList(ArrayList<Users> listPlayers){
        this.listUsers = listPlayers;

    }
    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public AdapterPlayersList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_user_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPlayersList.ViewHolderList holder, int position) {
        holder.fillList(listUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {

        TextView username;
        TextView name;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
        }

        public void fillList(Users users) {
            username.setText(users.getUsername());
            name.setText(users.getName());
        }

    }
}
