package proyecto.golfus.forat19.adapterList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Golf_Course_Types;
import Forat19.Golf_Courses;
import Forat19.Installations;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.ui.MainActivity;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * @Author Antonio Rodríguez Sirgado
 */
public class AdapterCoursesList extends RecyclerView.Adapter<AdapterCoursesList.ViewHolderList> implements View.OnClickListener {
    ArrayList<Golf_Courses> listCourses;
    ArrayList<Golf_Courses> listSearch;
    private View.OnClickListener listener;

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    public AdapterCoursesList(ArrayList<Golf_Courses> listCourses) {
        this.listCourses = listCourses;
        listSearch = new ArrayList<>();
        listSearch.addAll(listCourses);
    }

    @NonNull
    @Override
    public AdapterCoursesList.ViewHolderList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_list, null, false);
        view.setOnClickListener(this);
        return new ViewHolderList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCoursesList.ViewHolderList holder, int position) {
    holder.fillList(listCourses.get(position));
    }

    @Override
    public int getItemCount() {
        return listCourses.size();
    }



    public class ViewHolderList extends RecyclerView.ViewHolder {

        TextView name;
        TextView type;

        public ViewHolderList(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.course_list_name);
        }

        /**
         * Rellena cada item del recyclerview con los datos recibidos de los recorridos
         *
         * @author Antonio Rodríguez Sirgado
         */
        public void fillList(Golf_Courses golf_courses) {
            name.setText(golf_courses.getGolf_course());

        }

    }


}
