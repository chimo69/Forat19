package proyecto.golfus.forat19.adapterList;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
    String [] dataEntries;


    public AdapterDataList(ArrayList<Player_Data> listPlayerData) {
        this.listPlayerData = listPlayerData;
        dataEntries = new String[listPlayerData.size()];
    }

    @NonNull
    @Override
    public AdapterDataList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_data, null, false);
        return new ViewHolderList(view);
    }

    public String[] getDataEntries() {
        return dataEntries;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDataList.ViewHolderList holder, int position) {
        holder.fillList(listPlayerData.get(position));
    }

    @Override
    public int getItemCount() {
        return listPlayerData.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {
        TextView typeData;
        EditText entryData;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);

            typeData = itemView.findViewById(R.id.dataPlayer);
            entryData = itemView.findViewById(R.id.dataPlayerEntry);
            entryData.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    dataEntries[getAdapterPosition()]= s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        public void fillList(Player_Data playerData) {
            typeData.setText(playerData.getPlayer_data());

        }

    }

}