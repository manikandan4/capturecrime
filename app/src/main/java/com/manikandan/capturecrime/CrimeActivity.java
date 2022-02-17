package com.manikandan.capturecrime;

import androidx.fragment.app.Fragment;

import com.manikandan.capturecrime.fragments.CrimeFragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID = "com.manikandan.capturecrime.crimeID";

    @Override
    protected Fragment createFragment() {
        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(uuid);
    }
}