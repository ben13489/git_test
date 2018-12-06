package nutn.ilt.projectfor50;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mu on 2017/8/11.
 */

public class Menu1 extends Fragment {
    private AppBarLayout appBarLayout;
    private TabLayout tableLayout;
    private ViewPager viewPager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu1,container,false);
        View conteneditor = (View)container.getParent();
        appBarLayout=conteneditor.findViewById(R.id.appbar);
        tableLayout=new TabLayout(getActivity());
        tableLayout.setTabTextColors(Color.parseColor("#FFFFFF"),Color.parseColor("#FFFFFF"));
        appBarLayout.addView(tableLayout);

        viewPager=view.findViewById(R.id.pager);
        ViewPagerAddapter pagerAddapter = new ViewPagerAddapter(getFragmentManager());
        viewPager.setAdapter(pagerAddapter);
        tableLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        appBarLayout.removeView(tableLayout);
    }

    public class ViewPagerAddapter extends FragmentStatePagerAdapter{
        public ViewPagerAddapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }
        String[] title = {"最新消息","概況","推薦"};

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new Tab1();
                case 1:
                    return new Tab2();
                case 2:
                    return new Tab3();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("分析推薦");
    }
}
