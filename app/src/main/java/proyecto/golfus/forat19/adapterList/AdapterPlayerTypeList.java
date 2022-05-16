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

import Forat19.Player_Types;
import Forat19.Players;
import proyecto.golfus.forat19.*;

/**
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterPlayerTypeList extends RecyclerView.Adapter<AdapterPlayerTypeList.ViewHolderList> implements View.OnClickListener {

    ArrayList<Players> listPlayerTypes;
    Context context;
    private View.OnClickListener listener;

    public AdapterPlayerTypeList (ArrayList<Players> listPlayerTypes, Context context){
        this.listPlayerTypes=listPlayerTypes;
        this.context=context;
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public AdapterPlayerTypeList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_type, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPlayerTypeList.ViewHolderList holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition));
        holder.fillList(listPlayerTypes.get(position));
    }

    @Override
    public int getItemCount() {
        return listPlayerTypes.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder{

        TextView typePlayer;
        CardView cv;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            typePlayer = itemView.findViewById(R.id.type_player);
            cv = itemView.findViewById(R.id.item_cvTypePlayer);
        }

        public void fillList (Players playerTypes){
            typePlayer.setText(playerTypes.getPlayer_type().getPlayer_type());
        }
    }
}
