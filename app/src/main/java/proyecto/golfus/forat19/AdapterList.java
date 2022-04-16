package proyecto.golfus.forat19;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Forat19.Users;

/**
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolderList>{

    ArrayList<Users> listUsers;

    public AdapterList(ArrayList<Users> listUsers) {
        this.listUsers = listUsers;
    }

    @NonNull
    @Override
    public AdapterList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,null,false);
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
    public class ViewHolderList extends RecyclerView.ViewHolder {
        TextView username;
        TextView name;
        TextView id;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            id=itemView.findViewById(R.id.id);
        }

        public void fillList(Users users) {
            username.setText(users.getUsername());
            name.setText(users.getName());
            id.setText(Integer.toString(users.getId_user()));
        }
    }
}
