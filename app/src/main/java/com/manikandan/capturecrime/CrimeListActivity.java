package com.manikandan.capturecrime;

import androidx.fragment.app.Fragment;

import com.manikandan.capturecrime.fragments.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}