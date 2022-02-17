package com.manikandan.capturecrime;

import androidx.fragment.app.Fragment;

import com.manikandan.capturecrime.fragments.CrimeFragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}