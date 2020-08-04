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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edm_messenger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 *  Class: GroupChatFragment.java
 *  Description: Create a fragment for the list of the group. It is view for the all group that
 *               current user have.
 */
public class GroupChatFragment extends Fragment {
    private View groupView;
    private ListView groupListView;
    private ArrayAdapter<String> groupListAdapter;
    private ArrayList<String> groupNames;
    private CurrentUser currentUser;

    public GroupChatFragment(){

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        groupView = inflater.inflate(R.layout.group_chat_fragment, container, false);

        return groupView;

    }

    @Override
    public void onStart() {

        super.onStart();

        init();

        addOnClick();

        displayGroups();

    }

    /**
     * connect all XML components to Java code
     */
    private void init(){

        currentUser = CurrentUser.getInstance();

        groupNames = new ArrayList<>();

        groupListView = (ListView) groupView.findViewById(R.id.group_list_view);

        groupListAdapter = new ArrayAdapter<String>(getContext() , android.R.layout.simple_list_item_1 , groupNames);

        groupListView.setAdapter(groupListAdapter);

    }

    /**
     * add on click listener to some XML components
     */
    private void addOnClick(){

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String groupName = parent.getItemAtPosition(position).toString();

                Intent groupIntent = new Intent(getContext(), GroupChatActivity.class);

                groupIntent.putExtra("GROUP_NAME",groupName);

                startActivity(groupIntent);
            }

        });
    }

    /**
     * display all group chats
     */
    private void displayGroups() {

        Database.getGroupsRef()
                .child(CurrentUser.getId())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Iterator it = dataSnapshot.getChildren().iterator();

                        HashSet<String> groups = new HashSet<>();

                        while(it.hasNext()){
                            groups.add(((DataSnapshot)it.next()).getValue().toString());
                        }

                        groupNames.clear();

                        groupNames.addAll(groups);

                        groupListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                });
    }

}
