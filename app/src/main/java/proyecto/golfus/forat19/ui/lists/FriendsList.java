package proyecto.golfus.forat19.ui.lists;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Forat19.Message;
import Forat19.User_Relationships;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.R;
import proyecto.golfus.forat19.adapterList.AdapterFriendshipList;
import proyecto.golfus.forat19.utils.Reply;
import proyecto.golfus.forat19.utils.RequestServer;
import proyecto.golfus.forat19.utils.Utils;

/**
 * Fragment para mostrar listado de amistades
 *
 * @author Antonio Rodríguez Sirgado
 */
public class FriendsList extends Fragment implements Observer {

    private ArrayList<User_Relationships> listFriends = new ArrayList<>();
    private ArrayList<User_Relationships> listRequest;
    private RecyclerView rw_friends, rw_request;
    private AdapterFriendshipList adapterFriendshipList;
    private AdapterFriendshipList adapterRequestList;
    private Message request;
    private TextView listFriendsInfo, requestFriends;

    public FriendsList(ArrayList<User_Relationships> listRequest) {
        this.listRequest = listRequest;
    }
    public FriendsList(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        listFriendsInfo = view.findViewById(R.id.tv_Friends_list);
        requestFriends = view.findViewById(R.id.tv_Friends_InfoRequest);
        rw_friends = view.findViewById(R.id.rv_friendshipList);
        rw_friends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rw_request = view.findViewById(R.id.rv_friendshipRequest);
        rw_request.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterRequestList = new AdapterFriendshipList(listRequest,getContext(),1);
        rw_request.setAdapter(adapterRequestList);

        if (listRequest.size()==0){
            requestFriends.setVisibility(View.VISIBLE);
        } else {
            requestFriends.setVisibility(View.GONE);
        }

        new ItemTouchHelper(itemTouch).attachToRecyclerView(rw_friends);
        loadFriends();
        return view;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Reply) {
            Utils.showSnack(getView(), R.string.it_was_impossible_to_make_connection, Snackbar.LENGTH_LONG);
        } else {

            request = (Message) arg;
            String command = request.getCommand();

            if (command.equals(Global.LIST_USER_RELATIONSHIP_I)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listFriends = (ArrayList<User_Relationships>) request.getObject();
                        if (listFriends.size()>0){
                        adapterFriendshipList = new AdapterFriendshipList(listFriends, getContext(), 0);
                        rw_friends.setAdapter(adapterFriendshipList);
                        } else {
                            listFriendsInfo.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else if (command.equals(Global.DELETE_USER_RELATIONSHIP)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showSnack(getView(), "Friendship successfully eliminated", Snackbar.LENGTH_LONG);

                    }
                });
            }
        }

    }

    /**
     * <b>Envia el mensaje para cargar las amistades</b><br>
     * Mensaje = (token¬device, listUserRelationshipI, id usuario, null)
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void loadFriends() {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.LIST_USER_RELATIONSHIP_I, String.valueOf(Global.activeUser.getId_user()), null);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }





    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
            confirmation.setTitle(R.string.attention);
            confirmation.setMessage(R.string.Are_you_sure_eliminate_friendship);
            confirmation.setCancelable(true);
            confirmation.setPositiveButton(R.string.yes, (dialog, which) -> {
                deleteFriend(listFriends.get(viewHolder.getAbsoluteAdapterPosition()));
                listFriends.remove(viewHolder.getAbsoluteAdapterPosition());
                adapterFriendshipList.notifyDataSetChanged();
            });
            confirmation.setNegativeButton(R.string.Cancel, (dialog, which) -> {
                adapterFriendshipList.notifyDataSetChanged();
            });

            confirmation.show();
        }
    };

    private void deleteFriend(User_Relationships userRelationships) {

        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.DELETE_USER_RELATIONSHIP, null, userRelationships);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }
}