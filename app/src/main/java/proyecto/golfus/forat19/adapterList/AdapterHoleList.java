package proyecto.golfus.forat19.adapterList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import proyecto.golfus.forat19.*;

/**
 * Adaptador para rellenar el recyclerView de Hoyos
 *
 * @author Antonio Rodríguez Sirgado
 */
public class AdapterHoleList extends RecyclerView.Adapter<AdapterHoleList.ViewHolderList> implements View.OnClickListener {
    List<String> listHoles;
    private View.OnClickListener listener;

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public AdapterHoleList(List<String> listHoles) {
        this.listHoles = listHoles;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public AdapterHoleList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hole, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHoleList.ViewHolderList holder, int position) {
        holder.fillList(listHoles.get(position));
    }

    @Override
    public int getItemCount() {
        return listHoles.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {
        TextView txtHole;
        CardView cv;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            txtHole = itemView.findViewById(R.id.txt_itemHole_numberHole);
            cv = itemView.findViewById(R.id.cv_itemHole_numHole);
        }

        /**
         * Rellena cada item del recyclerview con los datos recibidos
         *
         * @param hole hoyo
         * @author Antonio Rodríguez Sirgado
         */
        public void fillList(String hole) {
            txtHole.setText(hole);
        }
    }



}
