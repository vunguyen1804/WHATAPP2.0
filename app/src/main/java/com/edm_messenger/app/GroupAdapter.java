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

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.edm_messenger.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *  Class: GroupAdapter.java
 *  Description: Adapter class for group object
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupHolder> {

    /**
     * Group Holder class to display groups on group fragment
     */
    public static class GroupHolder extends RecyclerView.ViewHolder {
        private TextView groupNameTextView;
        private Button button;

        public GroupHolder(@NonNull View itemView) {

            super(itemView);

            groupNameTextView = (TextView) itemView.findViewById(R.id.group_name);

            button = (Button) itemView.findViewById(R.id.group_button);

        }

    }
    //**********************************************************************************************

    private static final String LEAVE_STATE = "Leave";
    private static final String JOIN_STATE = "Join";
    private static final int BLUE = 628909;
    private static final int GREEN = 708438;

    private ArrayList<String> groups;
    private HashSet<String> myGroups;
    private Context context;

    public GroupAdapter(ArrayList<String> groups, HashSet<String> myGroups, Context context) {

        this.groups = groups;

        this.myGroups = myGroups;

        this.context = context;

    }


    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_display_layout, parent, false);

        return new GroupHolder(view);
    }

    /**
     *
     * @param holder
     * @param position
     * Display groups that current user in and out differently
     */
    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {

        String groupName = groups.get(position);

        holder.groupNameTextView.setText(groupName);

        if (myGroups.contains(groupName)) {

            displayMyGroups(holder, groupName);

        } else {

            displayOtherGroups(holder, groupName);

        }

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    /**
     *
     * @param holder
     * @param groupName
     * display groups that current user is a member of.
     */
    void displayMyGroups(GroupHolder holder, String groupName) {

        setButtonToLeave(holder.button);

        // leaving a group
        holder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (DialogInterface.BUTTON_POSITIVE == which){

                            setButtonToJoin(holder.button);

                            Database.getGroupsRef().child(CurrentUser.getId()).child(groupName).removeValue();

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupAdapter.this.context);

                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();

            }
        });

    }

    /**
     *
     * @param holder
     * @param groupName
     * display groups that current user is not a member of.
     */
    void displayOtherGroups(GroupHolder holder, String groupName) {

        setButtonToJoin(holder.button);

        holder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                HashMap<String, Object> newValue = new HashMap<>();

                newValue.put(groupName, groupName);

                Database.getGroupsRef().child(CurrentUser.getId()).updateChildren(newValue);

            }

        });

    }

    /**
     *
     * @param button
     * Set current button on current holder to leave
     */
    void setButtonToLeave(Button button) {

        button.setText(LEAVE_STATE);

        button.setBackgroundColor(this.context.getColor(R.color.red));

    }

    /**
     *
     * @param button
     * Set current button on current holder to join
     */
    void setButtonToJoin(Button button) {

        button.setText(JOIN_STATE);

        button.setBackgroundColor(this.context.getColor(R.color.green));

    }

}
