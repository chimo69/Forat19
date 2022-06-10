package proyecto.golfus.forat19.ui.lists;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;
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
    private RecyclerView rv_friends, rv_request;
    private AdapterFriendshipList adapterFriendshipList;
    private AdapterFriendshipList adapterRequestList;
    private Message request;
    private TextView txt_listFriendsInfo, txt_requestFriends;

    public FriendsList(ArrayList<User_Relationships> listRequest) {
        this.listRequest = listRequest;
    }

    public FriendsList() {
    }

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
        txt_listFriendsInfo = view.findViewById(R.id.tv_Friends_list);
        txt_requestFriends = view.findViewById(R.id.tv_Friends_InfoRequest);
        rv_friends = view.findViewById(R.id.rv_friendshipList);
        rv_friends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_request = view.findViewById(R.id.rv_friendshipRequest);
        rv_request.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapterRequestList = new AdapterFriendshipList(listRequest, getContext(), 1);
        rv_request.setAdapter(adapterRequestList);

        new ItemTouchHelper(itemTouch).attachToRecyclerView(rv_friends);
        loadFriends();

        Log.d(Global.TAG, "Numero de solicitudes de amistad recibidas: " + listRequest.size());
        if (listRequest.size() > 0) {
            txt_requestFriends.setText(R.string.click_on_the_user_to_accept_the_friendship);
            adapterRequestList = new AdapterFriendshipList(listRequest, getContext(), 1);
            rv_request.setAdapter(adapterRequestList);
            adapterRequestList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                    confirmation.setTitle(R.string.attention);
                    confirmation.setMessage(R.string.accept_friend_request);
                    confirmation.setCancelable(true);
                    confirmation.setPositiveButton(R.string.yes, (dialog, which) -> {
                        acceptRequest(listRequest.get(rv_request.getChildAdapterPosition(v)), Global.ACCEPT);
                        listRequest.remove(listRequest.get(rv_request.getChildAdapterPosition(v)));
                        adapterRequestList.notifyItemRemoved(rv_request.getChildAdapterPosition(v));
                    });
                    confirmation.setNegativeButton(R.string.Cancel, (dialog, which) -> {

                    });
                    confirmation.setNegativeButton(R.string.no, ((dialog, which) -> {
                        acceptRequest(listRequest.get(rv_request.getChildAdapterPosition(v)), Global.REJECT);
                        listRequest.remove(listRequest.get(rv_request.getChildAdapterPosition(v)));
                        adapterRequestList.notifyItemRemoved(rv_request.getChildAdapterPosition(v));
                    }));

                    confirmation.show();
                }
            });
        } else {
            txt_requestFriends.setText(R.string.no_pending_applications);
        }
        return view;
    }


    /**
     * Permanece a la espera de que las variables cambien
     *
     * @param o   la clase observada
     * @param arg objeto observado
     * @author Antonio Rodriguez Sirgado
     */
    @Override
    public void update(Observable o, Object arg) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (arg instanceof Reply) {
                    Utils.showSnack(getView(), ((Reply) arg).getTypeError(), Snackbar.LENGTH_LONG);
                } else {
                    request = (Message) arg;
                    String command = request.getCommand();
                    String parameter = request.getParameters();
                    switch (command) {
                        case Global.LIST_USER_RELATIONSHIP_I:
                            if (parameter.equals(Global.OK)) {
                                listFriends = (ArrayList<User_Relationships>) request.getObject();
                                if (listFriends.size() > 0) {
                                    adapterFriendshipList = new AdapterFriendshipList(listFriends, getContext(), 0);
                                    rv_friends.setAdapter(adapterFriendshipList);
                                    txt_listFriendsInfo.setText(R.string.swipe_to_the_right_to_remove_the_friendship);
                                } else {
                                    txt_listFriendsInfo.setText(R.string.no_friendships_to_show);
                                }
                            }
                            break;
                        case Global.DELETE_USER_RELATIONSHIP:
                            if (parameter.equals(Global.OK)) {
                                Utils.showSnack(getView(), getString(R.string.friendship_successfully_eliminated), Snackbar.LENGTH_LONG);
                            }
                            break;
                        case Global.UPDATE_USER_RELATIONSHIP:
                            if (parameter.equals(Global.OK)) {
                                Utils.showSnack(getView(), getString(R.string.Friendship_successfully_updated), Snackbar.LENGTH_LONG);
                                loadFriends();
                            }
                            break;
                    }
                }
            }
        });
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

    /**
     * <b>Envia el mensaje para borrar la amistad</b><br>
     * Mensaje = (token¬device, DeleteUserRelationship, null, userRelationship)
     *
     * @param userRelationships amistad para borrar
     * @author Antonio Rodríguez Sirgado
     */
    private void deleteFriend(User_Relationships userRelationships) {

        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.DELETE_USER_RELATIONSHIP, null, userRelationships);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    /**
     * <b>Envia mensaje al servidor para aceptar una petición de amistad</b><br>
     * Mensaje = ((token¬device, UpdateUserRelationship, accept or reject (A/R) , user relationShip)
     *
     * @param userRelationships peticion de amistad
     * @param acceptOrReject    aceptada o rechazada
     */
    private void acceptRequest(User_Relationships userRelationships, String acceptOrReject) {
        Message message = new Message(Utils.getActiveToken(getActivity()) + "¬" + Utils.getDevice(getActivity()), Global.UPDATE_USER_RELATIONSHIP, acceptOrReject, userRelationships);
        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver(this);
    }

    // desplaza un amigo hacia la derecha
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

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(getResources().getColor(R.color.error))
                    .addActionIcon(R.drawable.ic_delete)
                    .addSwipeRightLabel(getString(R.string.delete))
                    .setSwipeRightLabelColor(getResources().getColor(R.color.white))
                    .setSwipeRightLabelTextSize(1, 16)
                    .create()
                    .decorate();
        }
    };
}
