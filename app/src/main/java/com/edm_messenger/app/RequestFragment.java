/*
 * Name: Zuhao Zhang
 *       Adrian Wong
 *       Yuxiang Liu
 *       Zhe Xu
 *       Vu Nguyen
 *       Boram Wong
 *       Srinivas Venkatraman
 *       Apurva Goenka
 *       Steven Kan
 *       Edgar Matias
 *
 * Project: EDM-Messenger
 * Date: 9/23/2019
 * Description: UCSD Fall 2019 Gary's CSE 110 Team EDM's messenger project
 */
package com.edm_messenger.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edm_messenger.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Class: RequestFragment.java
 * Description: Contains the fragment for the request. Handle the request from new friends.
 *
 */
public class RequestFragment extends Fragment {

    //**********************************************************************************************
    public static class Holder extends  RecyclerView.ViewHolder{
        private TextView nameTextView;
        private TextView statusTextView;
        private CircleImageView profileCircleImageView;
        private Button acceptButton;
        private Button cancelButton;

        /**
         *
         * @param itemView
         * Holder class to display all add friend requests from other user
         */
        public Holder(@NonNull View itemView) {

            super(itemView);

            nameTextView = (TextView)  itemView.findViewById(R.id.person_name_text_view);

            statusTextView = (TextView) itemView.findViewById(R.id.person_status_text_view);

            profileCircleImageView = (CircleImageView) itemView.findViewById(R.id.person_profile_circle_image_view);

            acceptButton = (Button) itemView.findViewById(R.id.accept_button); // recycle accept button

            cancelButton = (Button) itemView.findViewById(R.id.cancel_button);

        }

    }

    //**********************************************************************************************
    private View requestView;
    private RecyclerView recycleList;
    private LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerAdapter<User, Holder> adapter;

    // Empty constructor
    public RequestFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // re-use friend_chat_fragment
        requestView = inflater.inflate(R.layout.friend_chat_fragment, container, false);

        return requestView;

    }

    @Override
    public void onStart() {

        super.onStart();

        init();

        addOnClick();

    }

    /**
     * connect all XML components to Java code.
     */
    private void init() {

        linearLayoutManager = new LinearLayoutManager(this.getContext());

        recycleList = (RecyclerView) requestView.findViewById(R.id.friend_recycle_view);

        recycleList.setLayoutManager(linearLayoutManager);

    }

    /**
     * User Firebase recycler to display all add friend requests
     */
    private void addOnClick() {

        FirebaseRecyclerOptions<User> options  = new FirebaseRecyclerOptions
                .Builder<User>()
                .setQuery(Database.getRequestsRef().child(CurrentUser.getId()), User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, Holder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull Holder holder, int i, @NonNull User user) {

                holder.nameTextView.setText(user.getName());

                holder.statusTextView.setText(user.getStatus());

                Picasso.get()
                        .load(user.getProfilePicDir())
                        .placeholder(R.drawable.profile)
                        .into(holder.profileCircleImageView);

                Button acceptButton = holder.acceptButton;

                Button cancelButton = holder.cancelButton;

                String friendId = getRef(i).getKey();

                acceptButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        adapter.getRef(i).removeValue();

                        addFriendToContact(friendId, user);

                    }

                });

                cancelButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        adapter.getRef(i).removeValue();

                        cancelRequest(friendId);

                    }

                });

            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.user_display_layout, parent, false);

                return new Holder(view);
            }
        };

        recycleList.setAdapter(adapter);

        adapter.startListening();

        linearLayoutManager.smoothScrollToPosition(recycleList,null,adapter.getItemCount());

    }

    /**
     *
     * @param friendId
     * @param user
     * when the current user clicks accept -> update the database
     */
    private void addFriendToContact(String friendId, User user) {

        String myId = CurrentUser.getId();

        Database.getFriendsRef()
                .child(myId)
                .child(friendId)
                .setValue(friendId);

        Database.getFriendsRef()
                .child(friendId)
                .child(myId)
                .setValue(myId);

        Database.getRequestsRef()
                .child(myId)
                .child(friendId)
                .removeValue();

        // create a unique chat id for both users
        String uniqueChatId = myId + friendId;

        Database.getChatIdRef()
                .child(myId)
                .child(friendId)
                .setValue(uniqueChatId);

        Database.getChatIdRef()
                .child(friendId)
                .child(myId)
                .setValue(uniqueChatId);
    }

    private void cancelRequest(String friendId) {

        String myId = CurrentUser.getId();

        Database.getFriendsRef()
                .child(myId)
                .child(friendId)
                .removeValue();

    }

}
