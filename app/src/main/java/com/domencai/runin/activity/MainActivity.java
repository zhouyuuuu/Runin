package com.domencai.runin.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.domencai.runin.R;
import com.domencai.runin.fragment.DiscoverFragment;
import com.domencai.runin.fragment.HomeFragment;
import com.domencai.runin.fragment.MeFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private List<Fragment> mFragments = new ArrayList<>();
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private BottomNavigationView bottomNavigationView;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewPager();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mViewPager.setCurrentItem(1);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.discover:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.home:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.me:
                        mViewPager.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    //初始化viewpager，加入today，weekly,discover三个碎片
    private void initViewPager()
    {
        mViewPager = (ViewPager) findViewById(R.id.viewPager_main);
        DiscoverFragment discover = new DiscoverFragment();
        HomeFragment home = new HomeFragment();
        MeFragment me = new MeFragment();
        mFragments.add(discover);
        mFragments.add(home);
        mFragments.add(me);
        /**
         * 初始化Adapter
         */
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public int getCount()
            {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0)
            {
                return mFragments.get(arg0);
            }
            //防止销毁fragment
            @Override
            public void destroyItem(ViewGroup container, int position, Object object)
            {
                super.destroyItem(container, position, object);
            }

        };
        mViewPager.setAdapter(mAdapter);
        //翻页监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
