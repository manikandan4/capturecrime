package com.manikandan.capturecrime;

import androidx.fragment.app.Fragment;

import com.manikandan.capturecrime.fragments.CrimeFragment;

public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}