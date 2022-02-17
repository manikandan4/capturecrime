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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.manikandan.capturecrime.R;
import com.manikandan.capturecrime.models.Crime;

public class CrimeFragment extends Fragment {
    private Crime crime;
    private TextView mTitleLabel;
    private TextInputEditText  mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crime = new Crime();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mDateButton = v.findViewById(R.id.btn_crime_date);
        mDateButton.setText(crime.getmDate().toString());
        mDateButton.setEnabled(false);

        mTitleLabel = v.findViewById(R.id.txt_crime_title_label);
        mTitleField = v.findViewById(R.id.edit_crime_input);
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
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> crime.setMsolved(isChecked));

        return v;
    }
}
