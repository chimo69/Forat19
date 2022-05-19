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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import Forat19.Golf_Games;
import proyecto.golfus.forat19.*;
import proyecto.golfus.forat19.utils.Utils;

/**
 * @Author Antonio Rodríguez Sirgado
 */

public class AdapterGameList extends RecyclerView.Adapter<AdapterGameList.ViewHolderList> implements View.OnClickListener {
    ArrayList<Golf_Games> listGames;
    private View.OnClickListener listener;
    Context context;

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public AdapterGameList(ArrayList<Golf_Games> listGames, Context context) {
        this.context = context;
        this.listGames = listGames;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public AdapterGameList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterGameList.ViewHolderList holder, int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.fillList(listGames.get(position));
    }

    @Override
    public int getItemCount() {
        return listGames.size();
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {

        TextView course, hour, date;
        CardView cv;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv_item_gameList);
            course = itemView.findViewById(R.id.txt_ItemGame_course);
            hour = itemView.findViewById(R.id.txt_itemGame_hour);
            date = itemView.findViewById(R.id.txt_itemGame_data);
        }

        public void fillList(Golf_Games golf_games) {

            String dateGame = Utils.changeDateFormat(golf_games.getGame_date());
            String hourGame = golf_games.getGame_hour();
            course.setText(golf_games.getGolf_course().getGolf_course());
            date.setText(dateGame);
            hour.setText(hourGame);
        }
    }


}
