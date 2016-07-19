package com.sd.v1;


import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sd.utils.CommonResources;
import com.sd.utils.tools.UITools;
import com.sd.v1.Fragments.LevelListFragment;
import com.sd.v1.Fragments.LevelListFragmentGrid;

import java.util.ArrayList;
import java.util.List;


public class LevelsTabsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String mWord;
    private String mWordDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_levels_tabs);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("LET US GO !!");
        setSupportActionBar(toolbar);

        CommonResources.loadResources(this);
        View v  = getWindow().getDecorView().findViewById(android.R.id.content);
        UITools.applyTypeface(CommonResources.getNormalTypeface(), v);


        mWord = getIntent().getStringExtra(WordsList.WORD);
        mWordDescription = getIntent().getStringExtra(WordsList.WORD_DESCRIPTION);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(LevelListFragmentGrid.newInstance(mWord, mWordDescription, 1), LevelListFragmentGrid.EASY);
        adapter.addFragment(LevelListFragmentGrid.newInstance(mWord , mWordDescription , 2), LevelListFragmentGrid.MEDIUM);
        adapter.addFragment(LevelListFragmentGrid.newInstance(mWord , mWordDescription , 3), LevelListFragmentGrid.HARD);
        viewPager.setAdapter(adapter);
    }

    private void setupTabs(View v) {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_level_text, null);
        UITools.applyTypeface(CommonResources.getNormalTypeface(), tabOne);
        tabOne.setText(LevelListFragmentGrid.EASY);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_level_text, null);
        UITools.applyTypeface(CommonResources.getNormalTypeface(), tabTwo);
        tabTwo.setText(LevelListFragmentGrid.MEDIUM);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_level_text, null);
        UITools.applyTypeface(CommonResources.getNormalTypeface(), tabThree);
        tabThree.setText(LevelListFragmentGrid.EASY);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
