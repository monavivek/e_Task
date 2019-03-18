package com.saanjh.e_task.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabLayout extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public TabLayout(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
           }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AssignTaskFragment tab1 = new AssignTaskFragment();
                return tab1;
            case 1:
              MyAssignedTaskListFragment tab2=new MyAssignedTaskListFragment();
                return tab2;
            case 2:
                MyTaskListFragment tab3 = new MyTaskListFragment();
                return tab3;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}