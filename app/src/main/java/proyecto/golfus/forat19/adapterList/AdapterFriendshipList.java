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

import Forat19.User_Relationships;
import proyecto.golfus.forat19.*;

/**
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterFriendshipList extends RecyclerView.Adapter<AdapterFriendshipList.ViewHolderList> implements View.OnClickListener {

    ArrayList<User_Relationships> listFriends;
    private View.OnClickListener listener;
    private int whichUser;
    Context context;

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public AdapterFriendshipList(ArrayList<User_Relationships> listFriends, Context context, int whichUser){
        this.listFriends = listFriends;
        this.context= context;
        this.whichUser = whichUser;
    }
    @Override
    public void onClick(View v) {
        if (listener !=null){
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public AdapterFriendshipList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_user_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFriendshipList.ViewHolderList holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.fillList(listFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return listFriends.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder{

        TextView username;
        TextView name;
        CardView cv;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            cv = itemView.findViewById(R.id.item_cv_normalUser);
        }

        public void fillList (User_Relationships userRelationships){
            if (whichUser==0){
                username.setText(userRelationships.getRelated_user().getUsername());
                name.setText(userRelationships.getRelated_user().getName());
            } else {
                username.setText(userRelationships.getUser().getUsername());
                name.setText(userRelationships.getUser().getName());
            }
        }
    }
}
