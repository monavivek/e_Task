package com.saanjh.e_task.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saanjh.e_task.R;

public class TabsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newtask, container, false);

        android.support.design.widget.TabLayout tabLayout = (android.support.design.widget.TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.pencil_icon));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.assign_icon));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.own_icon));
        tabLayout.setTabGravity(android.support.design.widget.TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        final TabLayout adapter = new TabLayout
                (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new android.support.design.widget.TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(android.support.design.widget.TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                viewPager.setOffscreenPageLimit(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.support.design.widget.TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(android.support.design.widget.TabLayout.Tab tab) {

            }
        });

        DrawerFragment.adapter.setSelected(0);

        return rootView;
    }
}
