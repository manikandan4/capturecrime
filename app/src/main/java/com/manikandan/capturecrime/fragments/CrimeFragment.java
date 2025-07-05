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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.viewmodel.CrimeDetailViewModel;
import com.manikandan.capturecrime.R;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment implements FragmentResultListener {
    private CrimeEntity crime;
    private TextView mTitleLabel;
    private TextInputEditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private CrimeDetailViewModel viewModel;

    private static final String ARG_UUID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private UUID crimeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            crimeId = (UUID) getArguments().getSerializable(ARG_UUID);
        }
        viewModel = new ViewModelProvider(this).get(CrimeDetailViewModel.class);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mDateButton = v.findViewById(R.id.btn_crime_date);
        mTitleLabel = v.findViewById(R.id.txt_crime_title_label);
        mTitleField = v.findViewById(R.id.edit_crime_input);
        mSolvedCheckBox = v.findViewById(R.id.chx_crime_solved);
        if (crimeId != null) {
            viewModel.getCrimeById(crimeId).observe(getViewLifecycleOwner(), new Observer<CrimeEntity>() {
                @Override
                public void onChanged(CrimeEntity crimeEntity) {
                    if (crimeEntity == null) return;
                    crime = crimeEntity;
                    updateUI();
                }
            });
        }
        return v;
    }

    private void updateUI() {
        if (crime == null) return;
        int[] dateArr = DatePickerFragment.getDateFormatted(crime.date);
        dateArr[1] = dateArr[1] + 1;
        mDateButton.setText(dateArr[0] + "-" + dateArr[1] + "-" + dateArr[2]);
        mDateButton.setOnClickListener(view -> {
            FragmentManager fm = this.getParentFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.date, DIALOG_DATE);
            fm.setFragmentResultListener(DIALOG_DATE, this, this);
            dialog.show(fm, DIALOG_DATE);
        });
        mTitleLabel.setText(crime.title);
        mTitleField.setText(crime.title);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.title = s.toString();
                mTitleLabel.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mTitleField.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && crime != null) {
                viewModel.updateCrime(crime);
            }
        });
        mSolvedCheckBox.setChecked(crime.solved);
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            crime.solved = isChecked;
            viewModel.updateCrime(crime);
        });
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
            if (crime != null && (crime.title == null || crime.title.isEmpty())) {
                viewModel.deleteCrime(crime);
            }
            this.setEnabled(false);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    };

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        if (requestKey.equals(DIALOG_DATE)) {
            Date date = (Date) result.getSerializable(DatePickerFragment.RESULT_DATE_KEY);
            crime.date = date;
            viewModel.updateCrime(crime);
            int[] dateArr = DatePickerFragment.getDateFormatted(crime.date);
            dateArr[1] = dateArr[1] + 1;
            mDateButton.setText(dateArr[0] + "-" + dateArr[1] + "-" + dateArr[2]);
        }
    }
}
