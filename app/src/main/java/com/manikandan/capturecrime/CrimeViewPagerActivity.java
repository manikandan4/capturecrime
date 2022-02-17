package com.manikandan.capturecrime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.manikandan.capturecrime.fragments.CrimeFragment;
import com.manikandan.capturecrime.models.Crime;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.List;
import java.util.UUID;

public class CrimeViewPagerActivity extends FragmentActivity {
    private static final String EXTRA_CRIME_ID = "com.manikandan.capturecrime.crimeID";
    private List<Crime> crimeList;
    private ViewPager2 viewPager;
    private DotsIndicator dotsIndicator = null;
    private swipeStateAdapter swipeAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_crime_pager);
        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        dotsIndicator = findViewById(R.id.dots_indicator);
        viewPager = findViewById(R.id.fragment_crime_viewPager);
        updateUI();

        for (int i = 0; i < crimeList.size(); i++) {
            if (crimeList.get(i).getmID().equals(uuid)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        dotsIndicator.setViewPager2(viewPager);
    }

    private void updateUI() {
        crimeList = CrimeLab.getCrimeLab(this).getCrimes();
        if (swipeAdapter == null) {
            swipeAdapter = new swipeStateAdapter(this);
            viewPager.setAdapter(swipeAdapter);
            viewPager.setOffscreenPageLimit(1);
        } else {
            swipeAdapter.notifyDataSetChanged();
        }
    }

    private class swipeStateAdapter extends FragmentStateAdapter {
        public swipeStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public swipeStateAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public swipeStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Crime crime = crimeList.get(position);
            return CrimeFragment.newInstance(crime.getmID());
        }

        @Override
        public int getItemCount() {
            return crimeList.size();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    /*@Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }*/
}