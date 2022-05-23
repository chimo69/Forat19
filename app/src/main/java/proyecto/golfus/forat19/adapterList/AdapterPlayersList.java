package proyecto.golfus.forat19.adapterList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Forat19.Golf_Courses;
import Forat19.Players;
import proyecto.golfus.forat19.R;

/**
 * Adaptador encargado de rellenar el recyclerView de jugadores de un juego
 *
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterPlayersList extends RecyclerView.Adapter<AdapterPlayersList.ViewHolderList> implements View.OnClickListener {
    ArrayList<Players> listPlayers;
    ArrayList<Players> listSearch;
    private View.OnClickListener listener;

    public AdapterPlayersList(ArrayList<Players> listPlayers){
        this.listPlayers = listPlayers;
        listSearch = new ArrayList<>();
        listSearch.addAll(listPlayers);

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
        holder.fillList(listPlayers.get(position));
    }

    @Override
    public int getItemCount() {
        return listPlayers.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.listener = onClickListener;
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {

        TextView username;
        TextView name;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
        }

        public void fillList(Players player) {
            username.setText(player.getUser().getUsername());
            name.setText(player.getUser().getName());
        }

    }
    /**
     * Crea un listado con la busqueda realizada
     * @param txtSearch texto que debe incluir la busqueda
     */
    public void filter(String txtSearch) {
        int sizeText = txtSearch.length();
        if (sizeText == 0) {
            listPlayers.clear();
            listPlayers.addAll(listSearch);
        } else {
            listPlayers.clear();
            for (Players p : listSearch) {
                if (p.getUser().getUsername().toLowerCase().contains(txtSearch.toLowerCase()) || p.getUser().getName().toLowerCase().contains(txtSearch.toLowerCase())) {
                    listPlayers.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }
}
