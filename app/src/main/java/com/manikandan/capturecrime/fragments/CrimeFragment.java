package com.manikandan.capturecrime.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.google.android.material.textfield.TextInputEditText;
import com.manikandan.capturecrime.CrimeLab;
import com.manikandan.capturecrime.R;
import com.manikandan.capturecrime.models.Crime;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment implements FragmentResultListener {
    private Crime crime;
    private TextView mTitleLabel;
    private TextInputEditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    private static final String ARG_UUID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UUID uuid = (UUID) getArguments().getSerializable(ARG_UUID);
            crime = CrimeLab.getCrimeLab(getActivity()).getCrimeDetails(uuid);
        } else {
            crime = new Crime();
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mDateButton = v.findViewById(R.id.btn_crime_date);
        mDateButton.setText(crime.getmDate().toString());
        mDateButton.setOnClickListener(view -> {
            FragmentManager fm = this.getParentFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getmDate(), DIALOG_DATE);
            fm.setFragmentResultListener(DIALOG_DATE, this, this);
            dialog.show(fm, DIALOG_DATE);
        });

        mTitleLabel = v.findViewById(R.id.txt_crime_title_label);
        mTitleLabel.setText(crime.getmTitle());
        mTitleField = v.findViewById(R.id.edit_crime_input);
        mTitleField.setText(crime.getmTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setmTitle(s.toString());
                mTitleLabel.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSolvedCheckBox = v.findViewById(R.id.chx_crime_solved);
        mSolvedCheckBox.setChecked(crime.isMsolved());
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> crime.setMsolved(isChecked));

        return v;
    }

    public static CrimeFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_UUID, uuid);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* Enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            if (mTitleLabel.getText().toString().isEmpty()) {
                CrimeLab.getCrimeLab(getActivity()).removeCrime(crime);
            }
            this.setEnabled(false);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    };

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        if (requestKey.equals(DIALOG_DATE)) {
            Date date = (Date) result.getSerializable(DatePickerFragment.RESULT_DATE_KEY);
            crime.setmDate(date);
            mDateButton.setText(date.toString());
        }
    }
}
