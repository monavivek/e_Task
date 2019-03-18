package com.saanjh.e_task.Fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Model.NavDrawerItem;
import com.saanjh.e_task.Adapter.NavDrawerListAdapter;
import com.saanjh.e_task.R;
import java.util.ArrayList;
import java.util.List;

public class DrawerFragment extends Fragment {
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public static NavDrawerListAdapter adapter;
    private View containerView;
    private static String com;
    public static String[] titles = null;
    private static TypedArray navMenuIcons;
    private FragmentDrawerListener drawerListener;
    private ViewFlipper viewFlipper;
    TextView headerusername,companyCode;
 //   int mFlipping = 0;
    private String user_name;
    public DrawerFragment() {
        }
    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }
    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();
        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            System.out.println("titles"+navItem);

                navItem.setTitle(titles[i]);

            System.out.println("hlw="+titles[5]+com);
            navItem.setIcon(navMenuIcons.getResourceId(i,-1));
            data.add(navItem);
        }
        return data;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // drawer labels
        com = AppLockerPreference.getInstance(getActivity()).getM_company();
        user_name = AppLockerPreference.getInstance(getActivity()).getM_username();
        System.out.println(user_name);
        if(user_name.equals("admin")){
            titles = getActivity().getResources().getStringArray(R.array.nav_drawer_items_admin);
            com=AppLockerPreference.getInstance(getActivity()).getM_company();
            navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons_admin);
        }else {
            titles = getActivity().getResources().getStringArray(R.array.nav_drawer_items);
            navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        headerusername=(TextView)layout.findViewById(R.id.navUsername);
        companyCode=(TextView)layout.findViewById(R.id.navCompany_code);
//        viewFlipper = (ViewFlipper)layout.findViewById(R.id.nav_header_container);
//        if(mFlipping == 0){
//            viewFlipper.startFlipping();
//        }
        companyCode.setText("Comapny Code : "+AppLockerPreference.getInstance(getActivity()).getM_company());
        headerusername.setText("Welcome : "+AppLockerPreference.getInstance(getActivity()).getM_username());
        adapter = new NavDrawerListAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setSelected(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                adapter.setSelected(position);
                mDrawerLayout.closeDrawer(containerView);
            }
            @Override
            public void onLongClick(View view, int position) {
                }
        }));
        return layout;
    }
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }
    public static interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }
    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
     }
    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}