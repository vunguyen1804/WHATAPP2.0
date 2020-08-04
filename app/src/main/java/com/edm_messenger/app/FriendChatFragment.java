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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edm_messenger.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendChatFragment extends Fragment {

    /**
     *  Class: FriendChatFragment.java
     *  Description: Holder class to display friends on user's friends list
     */
    public static class Holder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView lastSeen;
        private CircleImageView profileCircleImageView;
        private Button acceptButton;
        private Button cancelButton;
        private ImageView onlineStatus;

        public Holder(@NonNull View itemView) {

            super(itemView);

            onlineStatus = (ImageView) itemView.findViewById(R.id.online_status_image_view);

            onlineStatus.setVisibility(View.VISIBLE);

            nameTextView = (TextView) itemView.findViewById(R.id.person_name_text_view);

            lastSeen = (TextView) itemView.findViewById(R.id.person_status_text_view);

            profileCircleImageView = (CircleImageView) itemView.findViewById(R.id.person_profile_circle_image_view);

            acceptButton = (Button) itemView.findViewById(R.id.accept_button); // recycle accept button

            acceptButton.setVisibility(View.INVISIBLE);

            cancelButton = (Button) itemView.findViewById(R.id.cancel_button);

            cancelButton.setVisibility(View.INVISIBLE);

        }
    }
    //**********************************************************************************************

    private static String ONLINE = "online";
    private static String LAST_SEEN = "Last seen ";

    private View friendChatView;
    private RecyclerView recycleList;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<String, Holder> adapter;
    private CurrentUser currentUser;

    public FriendChatFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        friendChatView = inflater.inflate(R.layout.friend_chat_fragment, container, false);

        return friendChatView;

    }

    @Override
    public void onStart() {

        super.onStart();

        init();

        addOnClick();

    }

    /**
     * connect XML component to Java code
     */
    private void init(){

        currentUser = CurrentUser.getInstance();

        linearLayoutManager = new LinearLayoutManager(this.getContext());

        recycleList = (RecyclerView) friendChatView.findViewById(R.id.friend_recycle_view);

        recycleList.setLayoutManager(linearLayoutManager);

    }

    /**
     * Display all friends using Firebase Recycler
     */
    private void addOnClick(){

        FirebaseRecyclerOptions<String> options  = new FirebaseRecyclerOptions
                .Builder<String>()
                .setQuery(Database.getFriendsRef().child(CurrentUser.getId()), String.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<String, Holder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull Holder holder, int i, @NonNull String id) {

                Database.getUsersRef()
                        .child(id)
                        .addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                User user = dataSnapshot.getValue(User.class);

                                holder.nameTextView.setText(user.getName());

                                Picasso.get()
                                        .load(user.getProfilePicDir())
                                        .placeholder(R.drawable.profile)
                                        .into(holder.profileCircleImageView);

                                addOnClickToHolder(holder, id, user);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}

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
     * @param holder
     * @param id
     * @param user
     * whenever a holder is created, update the online status (red/green dot) and add listener to that holder
     */
    private void addOnClickToHolder(Holder holder, String id , User user) {

        updateOnlineStatus(holder, id);

        clickHolder(holder, user, id);

    }

    /**
     *
     * @param holder
     * @param id
     * update friend's status indicator (red/green dot)
     */
    private void updateOnlineStatus(Holder holder, String id) {

        Database.getOnlineStatusRef()
                .child(id)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            String status = dataSnapshot.getValue().toString();

                            if(status.equals(ONLINE)){

                                holder.onlineStatus.setImageResource(R.drawable.online_icon);

                                holder.lastSeen.setText(ONLINE);

                            }else{

                                holder.onlineStatus.setImageResource(R.drawable.offline_icon);

                                holder.lastSeen.setText(LAST_SEEN + status);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });

    }

    /**
     *
     * @param holder
     * @param user
     * @param id
     * when current user click on a holder, transitioning to friend chat activity
     */
    private void clickHolder(Holder holder, User user, String id) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent friendChatIntent = new Intent(holder.itemView.getContext(), FriendChatActivity.class);

                friendChatIntent.putExtra("FRIEND_ID", id);

                friendChatIntent.putExtra("NAME", user.getName());

                friendChatIntent.putExtra("PROFILE_DIR", user.getProfilePicDir());

                startActivity(friendChatIntent);

            }
        });
    }

}
