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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.firebase.auth.FirebaseAuth;

/**
 *  Class: TabController.java
 *  Description: Handle the table view while user click on the tab view. It will three different
 *               fragments for Friend/Group/Request.
 *
 */
public class TabController extends FragmentStatePagerAdapter {

    //Constructor
    public TabController(FragmentManager fm){
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    //Getter
    @Override
    public Fragment getItem(int position) {

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            return new Fragment();

        switch (position){
            case 0:
                return new FriendChatFragment();
            case 1:
                return new GroupChatFragment();
            case 2:
                return new RequestFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "friend";
            case 1:
                return "group";
            case 2:
                return "request";
            default:
                return null;
        }

    }

}



