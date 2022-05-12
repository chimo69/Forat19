package proyecto.golfus.forat19.adapterList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Forat19.Golf_Courses;
import Forat19.Installations;
import proyecto.golfus.forat19.*;

/**
 * Adaptador encargado de rellenar el RecyclerView de recorridos
 * @author Antonio Rodríguez Sirgado
 */
public class AdapterCoursesList extends RecyclerView.Adapter<AdapterCoursesList.ViewHolderList> implements View.OnClickListener {
    ArrayList<Golf_Courses> listCourses;
    ArrayList<Golf_Courses> listSearch;
    String [] courseTypes = {"Golf", "Par 3", "P&P"};

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

        for (Golf_Courses g : listCourses) {
            Log.d(Global.TAG, "recorrido recibido: " + g.getGolf_course());
        }
        if (listCourses.size()>0){Log.d(Global.TAG,"-------------------------------------------------");}
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
            type = itemView.findViewById(R.id.course_list_type);
        }

        /**
         * Rellena cada item del recyclerview con los datos recibidos de los recorridos
         *
         * @author Antonio Rodríguez Sirgado
         */
        public void fillList(Golf_Courses golf_courses) {
            name.setText(golf_courses.getGolf_course());
            type.setText(courseTypes[golf_courses.getId_golf_course_type()-1]);

        }

    }


}
