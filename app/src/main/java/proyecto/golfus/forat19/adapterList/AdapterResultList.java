package proyecto.golfus.forat19.adapterList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Forat19.Golf_Game_Results;
import proyecto.golfus.forat19.*;

/**
 * Adaptador encargado de mostrar los resultados de los partidos
 *
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterResultList extends RecyclerView.Adapter<AdapterResultList.ViewHolderList> implements View.OnClickListener {
    private List<Golf_Game_Results> listResults;
    private List<Golf_Game_Results> listSearch;

    private View.OnClickListener listener;

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public AdapterResultList(List<Golf_Game_Results> listResults) {
        this.listResults = listResults;
        listSearch = new ArrayList<>();
        listSearch.addAll(listResults);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public AdapterResultList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterResultList.ViewHolderList holder, int position) {
        holder.fillList(listResults.get(position));
    }

    @Override
    public int getItemCount() {
        return listResults.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {


        TextView hole, player;
        EditText shot, note;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            shot = itemView.findViewById(R.id.et_itemResult_shot);
            note = itemView.findViewById(R.id.et_itemResult_note);
            hole = itemView.findViewById(R.id.txt_itemResult_numberHole);
            player = itemView.findViewById(R.id.txt_itemResult_player);
        }

        public void fillList(Golf_Game_Results golfGameResults) {
            hole.setText(String.valueOf(golfGameResults.getGolf_course_hole().getId_golf_course_hole()));
            shot.setText(String.valueOf(golfGameResults.getValue()));
            note.setText(golfGameResults.getNotes());
            player.setText(golfGameResults.getPlayer().getUser().getUsername());
            shot.setEnabled(false);
            note.setEnabled(false);
        }
    }


    /**
     * Hace un filtrador por nombre de usuario
     * @param player nombre de usuario
     * @author Antonio Rodríguez Sirgado
     */
    public void filterPlayer(String player) {
        int sizeText = player.length();

        if (sizeText == 0) {
            listResults.clear();
            listResults.addAll(listSearch);
        } else {
            listResults.clear();
            for (Golf_Game_Results ggr : listSearch) {
                if (ggr.getPlayer().getUser().getUsername().toLowerCase().contains(player.toLowerCase())) {
                    listResults.add(ggr);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Hace un filtrado por numero de hoyo
     * @param hole numero de hoyo
     * @author Antonio Rodríguez Sirgado
     */
    public void filterHole(String hole) {
        int sizeText = hole.length();

        if (sizeText == 0) {
            listResults.clear();
            listResults.addAll(listSearch);
        } else {
            listResults.clear();
            for (Golf_Game_Results ggr : listSearch) {
                if (String.valueOf(ggr.getGolf_course_hole().getId_golf_course_hole()).contains(hole)) {
                    listResults.add(ggr);
                }
            }
        }
        notifyDataSetChanged();
    }
}
