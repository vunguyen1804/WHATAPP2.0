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

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edm_messenger.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendSearchActivity extends AppCompatActivity {

    /**
     *  Class: FriendSearchActivity.java
     *  Description: Holder class to lookup and display friends on friends search activity.
     */
    public static class Holder extends RecyclerView.ViewHolder {
        private TextView nameTextView, statusTextView;
        private CircleImageView profileCircleImageView;
        private Button button;

        public Holder(@NonNull View itemView) {

            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.person_name_text_view);

            statusTextView = (TextView) itemView.findViewById(R.id.person_status_text_view);

            profileCircleImageView = (CircleImageView) itemView.findViewById(R.id.person_profile_circle_image_view);

            button = (Button) itemView.findViewById(R.id.accept_button); // recycle accept button

            button.setVisibility(View.VISIBLE);

            Button cancelButton = (Button) itemView.findViewById(R.id.cancel_button);

            cancelButton.setVisibility(View.INVISIBLE);

        }
    }
    //**********************************************************************************************

    private static final String ADD_FRIEND_STATE = "Add Friend";
    private static final String CANCEL_STATE = "Cancel Request";
    private static final String UNFRIEND_STATE = "Unfriend";
    private static final String EMPTY_STRING = "";

    private RecyclerView recycleList;
    private LinearLayoutManager linearLayoutManager;
    private HashSet<String> friendsId = new HashSet<>();
    private HashSet<String> sentRequests = new HashSet<>();
    private FirebaseRecyclerAdapter<User, Holder> adapter;
    private CurrentUser currentUser;
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.friend_search_activity);

    }

    @Override
    protected void onStart() {

        super.onStart();

        setContentView(R.layout.friend_search_activity);

        init();

        getFriendIds();

        getSentRequests();

        displayUsers(EMPTY_STRING);

        addOnClickToSearch();

    }

    /**
     * Connect all XML components to Java code
     */
    private void init() {

        currentUser = CurrentUser.getInstance();

        recycleList = (RecyclerView) findViewById(R.id.search_recycle_list);

        linearLayoutManager = new LinearLayoutManager(this);

        recycleList.setLayoutManager(linearLayoutManager);

        searchView = (SearchView) findViewById(R.id.search_view);

    }

    /**
     * add on click when current user interacts with the search bar
     */
    private void addOnClickToSearch() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                displayUsers(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                displayUsers(newText);

                return true;

            }
        });

    }

    // Display all users (including current user, friends, and other users)
    private void displayUsers(String query) {

        Query firebaseQuery = Database.getUsersRef().orderByChild(User.NAME_KEY).startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions
                .Builder<User>()
                .setQuery(firebaseQuery, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, Holder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull Holder holder, int i, @NonNull User user) {

                String myId = CurrentUser.getId();

                String friendId = getRef(i).getKey();

                holder.nameTextView.setText(user.getName());

                holder.statusTextView.setText(user.getStatus());

                Picasso.get().load(user.getProfilePicDir()).placeholder(R.drawable.profile).into(holder.profileCircleImageView);

                holder.button.setVisibility(View.VISIBLE);

                if (friendId.equals(myId)) {

                    displayMyself(holder);

                } else if (friendsId.contains(friendId)) {

                    setButtonToUnfriend(holder.button);

                    displayFriend(myId, friendId, user, holder);

                } else if (sentRequests.contains(friendId)) {

                    setButtonToCancel(holder.button);

                    displayOther(friendId, user, holder);

                } else {

                    setButtonToAddFriend(holder.button);

                    displayOther(friendId, user, holder);
                }
            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_display_layout, parent, false);

                return new Holder(view);

            }

        };

        recycleList.setAdapter(adapter);

        adapter.startListening();

        linearLayoutManager.smoothScrollToPosition(recycleList, null, adapter.getItemCount());

    }

    /**
     *
     * @param holder
     * display current user
     */
    private void displayMyself(Holder holder) {

        holder.button.setVisibility(View.INVISIBLE);

        holder.button.setVisibility(View.INVISIBLE);

    }

    /**
     *
     * @param myId
     * @param friendId
     * @param user
     * @param holder
     * display a friend
     */
    private void displayFriend(String myId, String friendId, User user, Holder holder) {

        Button button = holder.button;

        Database.getFriendsRef().child(friendId).child(myId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    setButtonToUnfriend(button);

                    button.setOnClickListener(null);

                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (DialogInterface.BUTTON_POSITIVE == which) {
                                        unfriend(friendId, holder, user);
                                    }

                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(FriendSearchActivity.this);

                            builder.setMessage("Do you want to unfriend this person?")
                                    .setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener)
                                    .show();
                        }

                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

    }

    /**
     *
     * @param friendId
     * @param user
     * @param holder
     * display other users
     */
    private void displayOther(String friendId, User user, Holder holder) {

        Button button = holder.button;

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (button.getText().toString().equals(ADD_FRIEND_STATE)) {

                    setButtonToCancel(button);

                    sendRequest(friendId, user, holder);

                } else {

                    setButtonToAddFriend(button);

                    cancelRequest(friendId);
                }
            }
        });
    }

    /**
     * collecting all friend ids which is used to display friends and other users differently
     */
    private void getFriendIds() {

        Database.getFriendsRef()
                .child(CurrentUser.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // collect all friend ids
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                friendsId.add(data.getKey());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                });

        Database.getFriendsRef().child(CurrentUser.getId()).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                friendsId.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                friendsId.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

    }

    /**
     * get all the add friend requests sent from other users to current users
     */
    private void getSentRequests() {

        String myId = CurrentUser.getId();

        Database.getSentRequestsRef()
                .child(myId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            sentRequests.add(data.getKey());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                });

        Database.getSentRequestsRef().child(myId).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                sentRequests.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                sentRequests.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    /**
     *
     * @param friendId
     * @param user
     * @param holder
     * send an add friend request to other users.
     */
    private void sendRequest(String friendId, User user, Holder holder) {

        String myId = CurrentUser.getId();

        Database.getRequestsRef()
                .child(friendId)
                .child(myId)
                .setValue(CurrentUser.toPerson());

        Database.getSentRequestsRef()
                .child(myId)
                .child(friendId)
                .setValue(EMPTY_STRING);

        sentRequests.add(friendId);

        Button button = holder.button;

        Database.getFriendsRef()
                .child(myId)
                .child(friendId)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            setButtonToUnfriend(button);

                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (DialogInterface.BUTTON_POSITIVE == which)
                                                unfriend(friendId, holder, user);

                                        }

                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendSearchActivity.this);

                                    builder.setMessage("Are you sure?")
                                            .setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener)
                                            .show();

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });

    }

    /**
     *
     * @param button
     * Change the button of the current hold to add friend
     */
    void setButtonToAddFriend(Button button) {

        button.setText(ADD_FRIEND_STATE);

        button.setBackgroundColor(getResources().getColor(R.color.green));

    }

    /**
     *
     * @param button
     * Change the button of the current hold to cancel
     */
    void setButtonToCancel(Button button) {

        button.setText(CANCEL_STATE);

        button.setBackgroundColor(getResources().getColor(R.color.red));

    }

    /**
     *
     * @param button
     * Change the button of the current hold to unfriend
     */
    void setButtonToUnfriend(Button button) {

        button.setText(UNFRIEND_STATE);

        button.setBackgroundColor(getResources().getColor(R.color.blue2));

    }

    /**
     *
     * @param friendId
     * delete an add friend request from Firebase when user click cancel button
     */
    private void cancelRequest(String friendId) {

        String myId = CurrentUser.getId();

        Database.getRequestsRef().child(friendId).child(myId).removeValue();

        Database.getSentRequestsRef().child(myId).child(friendId).removeValue();

    }

    /**
     *
     * @param friendId
     * @param holder
     * @param user
     * delete data from database when current user no long want to be friend with a particular user.
     */
    public void unfriend(String friendId, Holder holder, User user) {

        String myId = CurrentUser.getId();

        String chatId = Database.getChatIdRef()
                .child(myId)
                .child(friendId)
                .getKey();

        friendsId.remove(friendId);

        Button button = holder.button;

        // delete contact
        Database.getFriendsRef()
                .child(myId)
                .child(friendId)
                .removeValue();


        Database.getFriendsRef()
                .child(friendId)
                .child(myId)
                .removeValue();

        // delete chat
        Database.getPrivateChatRef()
                .child(chatId)
                .removeValue();

        setButtonToAddFriend(button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (button.getText().toString().equals(ADD_FRIEND_STATE)) {

                    setButtonToCancel(button);

                    sendRequest(friendId, user, holder);

                } else {

                    setButtonToAddFriend(button);

                    cancelRequest(friendId);

                }
            }
        });
    }

}
