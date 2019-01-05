package com.example.shakil.lasplatica;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by shakil on 02-Jan-19.
 */

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 1:

                GroupChatFragment groupChatFragment = new GroupChatFragment();
                return  groupChatFragment;

            case 2:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;

            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return "Chats";

            case 1:

                GroupChatFragment groupChatFragment = new GroupChatFragment();
                return  "Groups";

            case 2:
                ContactFragment contactFragment = new ContactFragment();
                return "Contacts";

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
