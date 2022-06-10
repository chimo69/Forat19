package proyecto.golfus.forat19.adapterList;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import proyecto.golfus.forat19.*;

/**
 * @author Antonio Rodríguez Sirgado
 */
public class AdapterUserList extends RecyclerView.Adapter<AdapterUserList.ViewHolderList> implements View.OnClickListener {
    List<String> listUser;
    private View.OnClickListener listener;
    private static int lastPosition=-1;

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public AdapterUserList(List<String> listUser) {
        this.listUser = listUser;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public AdapterUserList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserList.ViewHolderList holder, int position) {
        holder.fillList(listUser.get(position));
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {

        TextView txtUser;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txt_itemUser_user);
        }

        /**
         * Rellena cada item del recyclerview con los datos recibidos
         *
         * @param user usuario
         * @author Antonio Rodríguez Sirgado
         */
        public void fillList(String user) {
            txtUser.setText(user);
        }
    }
}
