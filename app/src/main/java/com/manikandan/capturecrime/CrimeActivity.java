package com.manikandan.capturecrime;

import androidx.core.content.ContextCompat;
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

    @Override
    protected void updateToolbarTitle() {
        toolbar.setTitle("Crime Detail");
        toolbar.setTitleTextAppearance(this,com.google.android.material.R.style.TextAppearance_Material3_TitleLarge);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));
    }
}