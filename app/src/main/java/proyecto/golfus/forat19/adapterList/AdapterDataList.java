package proyecto.golfus.forat19.adapterList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Forat19.Player_Data;
import proyecto.golfus.forat19.*;

/**
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterDataList extends RecyclerView.Adapter<AdapterDataList.ViewHolderList> {

    ArrayList<Player_Data> listPlayerData;

    @NonNull
    @Override
    public AdapterDataList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDataList.ViewHolderList holder, int position) {
        holder.fillList(listPlayerData.get(position));
    }

    @Override
    public int getItemCount() {
        return listPlayerData.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder{
        TextView typeData;
        EditText entryData;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);

            typeData= itemView.findViewById(R.id.dataPlayer);
            entryData = itemView.findViewById(R.id.dataPlayerEntry);
        }
    public void fillList (Player_Data playerData){
        typeData.setText(playerData.getPlayer_data());
    }
    }

}
