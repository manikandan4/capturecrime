package com.manikandan.capturecrime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.viewmodel.CrimeListViewModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.List;
import java.util.UUID;

public class CrimeViewPagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID = "com.manikandan.capturecrime.crimeID";
    private List<CrimeEntity> crimeList;
    private ViewPager2 viewPager;
    private DotsIndicator dotsIndicator = null;
    private swipeStateAdapter swipeAdapter = null;
    public Toolbar toolbar = null;
    private CrimeListViewModel viewModel;
    private UUID initialCrimeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_crime_pager);
        initialCrimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        dotsIndicator = findViewById(R.id.dots_indicator);
        viewPager = findViewById(R.id.fragment_crime_viewPager);
        toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Crime Details");
        toolbar.setLogo(R.drawable.ic_key_svgrepo_com);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(CrimeListViewModel.class);
        viewModel.getAllCrimes().observe(this, new Observer<List<CrimeEntity>>() {
            @Override
            public void onChanged(List<CrimeEntity> crimes) {
                crimeList = crimes;
                updateUI();
                if (crimeList != null && initialCrimeId != null) {
                    for (int i = 0; i < crimeList.size(); i++) {
                        if (crimeList.get(i).id.equals(initialCrimeId)) {
                            int index = i;
                            viewPager.postDelayed(() -> viewPager.setCurrentItem(index, true), 200);
                            break;
                        }
                    }
                }
            }
        });
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
    }

    private void updateUI() {
        if (crimeList == null) return;
        if (swipeAdapter == null) {
            swipeAdapter = new swipeStateAdapter(this);
            viewPager.setAdapter(swipeAdapter);
            viewPager.setOffscreenPageLimit(1);
            if (dotsIndicator != null) {
                dotsIndicator.setViewPager2(viewPager);
            }
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
            CrimeEntity crime = crimeList.get(position);
            return com.manikandan.capturecrime.fragments.CrimeFragment.newInstance(crime.id);
        }

        @Override
        public int getItemCount() {
            return crimeList == null ? 0 : crimeList.size();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}