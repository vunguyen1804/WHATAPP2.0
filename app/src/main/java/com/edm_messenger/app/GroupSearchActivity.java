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
import android.os.PersistableBundle;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edm_messenger.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *  Class: GroupSearchActivity.java
 *  Description: Activity that handle the group search functionality.
 */
public class GroupSearchActivity extends AppCompatActivity {
    private static final String EMPTY_STRING = "";

    private HashSet<String> myGroups = new HashSet<>();
    private ArrayList<String> groups = new ArrayList<>();
    private ArrayList<String> groupList = new ArrayList<>();
    private GroupAdapter groupAdapter;
    private CurrentUser currentUser;
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.group_search_activity);

    }

    @Override
    protected void onStart() {

        super.onStart();

        setContentView(R.layout.group_search_activity);

        init();

        getMyGroups();

        getAllGroup();

        addOnClickToSearch();

    }

    /**
     * connect all XML components to Java code
     */
    private void init() {

        currentUser = CurrentUser.getInstance();

        searchView = (SearchView) findViewById(R.id.group_search_view);

        groupAdapter = new GroupAdapter(groupList, myGroups, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.group_search_recycle_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(groupAdapter);

    }

    /**
     * add onClickListener when user interacts with the search bar
     */
    private void addOnClickToSearch() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                displayGroups(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                displayGroups(newText);

                return true;

            }

        });

    }

    /**
     *
     * @param query
     * display play all groups that contain query
     */
    private void displayGroups(String query) {

        ArrayList<String> newList = new ArrayList<>();

        query = query.toLowerCase();

        if(query.isEmpty()){

            newList = groups;

        }else {

            for (String group : groups) {

                if (group.toLowerCase().contains(query)) {
                    newList.add(group);
                }

            }

        }

        groupList.clear();

        groupList.addAll(newList);

        groupAdapter.notifyDataSetChanged();

    };


    /**
     * go to Firebase to collect all group chats that the current user is a member of
     */
    private void getMyGroups(){

        Database.getGroupsRef().child(CurrentUser.getId()).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                myGroups.add(dataSnapshot.getKey());

                groupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                myGroups.remove(dataSnapshot.getKey());

                groupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });

    }

    /**
     * get all group chats from database
     */
    private void getAllGroup(){
        Database.getGroupChatRef().addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                groups.add(dataSnapshot.getKey());

                groupList.add(dataSnapshot.getKey());

                groupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                groups.remove(dataSnapshot.getKey());

                groupList.remove(dataSnapshot.getKey());

                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });
    }

}
