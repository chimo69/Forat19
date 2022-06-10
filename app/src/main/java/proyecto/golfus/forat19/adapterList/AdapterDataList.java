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
 * Adaptador encargado de rellenar el RecyclerView de tipos de datos
 * @author Antonio Rodríguez Sirgado
 */
public class AdapterDataList extends RecyclerView.Adapter<AdapterDataList.ViewHolderList> {

    ArrayList<String> listPlayerData;
    String [] dataEntries;


    public AdapterDataList(ArrayList<String> listPlayerData) {
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

    public void setDataEntries(String [] dataEntries){this.dataEntries = dataEntries;}

    @Override
    public void onBindViewHolder(@NonNull AdapterDataList.ViewHolderList holder, int position) {
        holder.fillList(listPlayerData.get(position), dataEntries[position]);
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

        /**
         * Rellena cada item del recyclerView con los datos recibidos
         * @param playerData tipo de dato
         * @param data dato
         */
        public void fillList(String playerData, String data) {
            typeData.setText(playerData);
            entryData.setText(data);
        }

    }

}
