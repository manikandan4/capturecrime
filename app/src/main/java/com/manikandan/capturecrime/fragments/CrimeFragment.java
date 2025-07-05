package com.manikandan.capturecrime.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.viewmodel.CrimeDetailViewModel;
import com.manikandan.capturecrime.R;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment implements FragmentResultListener {
    // TODO: Improve image handling (camera, cropping, permissions)
    // TODO: Add settings, theming, and authentication entry points
    private CrimeEntity crime;
    private TextInputEditText editCaseId, editTitle, editDate, editLocation, editSuspect, editDescription;
    private MaterialSwitch switchSolved;
    private AutoCompleteTextView dropdownCrimeType;
    private ImageView imagePhoto;
    private MaterialButton btnAttachPhoto, btnSave, btnDelete;
    private View progressBar; // Loading indicator
    private final String[] crimeTypes = {"Theft", "Assault", "Robbery", "Fraud", "Vandalism", "Other"};

    private CrimeDetailViewModel viewModel;

    private static final String ARG_UUID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private UUID crimeId;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String crimeIdStr = getArguments().getString(ARG_UUID);
            if (crimeIdStr != null) {
                try {
                    crimeId = UUID.fromString(crimeIdStr);
                } catch (IllegalArgumentException e) {
                    crimeId = null;
                }
            }
        }
        viewModel = new ViewModelProvider(this).get(CrimeDetailViewModel.class);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        android.database.Cursor cursor = requireActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();
                            crime.photoPath = picturePath;
                            imagePhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        }
                    }
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        editCaseId = v.findViewById(R.id.edit_case_id);
        editTitle = v.findViewById(R.id.edit_title);
        editDate = v.findViewById(R.id.edit_date);
        editLocation = v.findViewById(R.id.edit_location);
        editSuspect = v.findViewById(R.id.edit_suspect);
        editDescription = v.findViewById(R.id.edit_description);
        switchSolved = v.findViewById(R.id.switch_solved);
        dropdownCrimeType = v.findViewById(R.id.dropdown_crime_type);
        imagePhoto = v.findViewById(R.id.image_photo);
        btnAttachPhoto = v.findViewById(R.id.btn_attach_photo);
        btnSave = v.findViewById(R.id.btn_save);
        btnDelete = v.findViewById(R.id.btn_delete);
        progressBar = v.findViewById(R.id.progress_bar);
        // Accessibility
        imagePhoto.setContentDescription(getString(R.string.photo_attachment));
        btnAttachPhoto.setContentDescription(getString(R.string.attach_photo));
        btnSave.setContentDescription(getString(R.string.save));
        btnDelete.setContentDescription(getString(R.string.delete));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, crimeTypes);
        dropdownCrimeType.setAdapter(adapter);
        editDate.setOnClickListener(view -> showDatePicker());
        btnAttachPhoto.setOnClickListener(view -> pickImageFromGallery());
        btnSave.setOnClickListener(view -> saveCrime());
        btnDelete.setOnClickListener(view -> deleteCrime());
        editTitle.setOnFocusChangeListener((v1, hasFocus) -> { if (!hasFocus && editTitle.getText() != null) crime.title = editTitle.getText().toString(); });
        editLocation.setOnFocusChangeListener((v1, hasFocus) -> { if (!hasFocus && editLocation.getText() != null) crime.location = editLocation.getText().toString(); });
        editSuspect.setOnFocusChangeListener((v1, hasFocus) -> { if (!hasFocus && editSuspect.getText() != null) crime.suspect = editSuspect.getText().toString(); });
        editDescription.setOnFocusChangeListener((v1, hasFocus) -> { if (!hasFocus && editDescription.getText() != null) crime.description = editDescription.getText().toString(); });
        dropdownCrimeType.setOnItemClickListener((parent, view1, position, id) -> crime.description = crimeTypes[position]);
        switchSolved.setOnCheckedChangeListener((buttonView, isChecked) -> crime.solved = isChecked);
        if (crimeId != null) {
            viewModel.getCrimeById(crimeId).observe(getViewLifecycleOwner(), crimeEntity -> {
                if (crimeEntity == null) return;
                crime = crimeEntity;
                bindCrimeToUI();
            });
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            crime = new com.manikandan.capturecrime.data.CrimeEntity(UUID.randomUUID(), "", new Date(), false, "", "", "", "");
            bindCrimeToUI();
            btnDelete.setVisibility(View.GONE);
        }
        return v;
    }

    private void bindCrimeToUI() {
        editCaseId.setText(crime.id.toString());
        editTitle.setText(crime.title);
        editDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", crime.date));
        switchSolved.setChecked(crime.solved);
        editLocation.setText(crime.location);
        editSuspect.setText(crime.suspect);
        editDescription.setText(crime.description);
        if (!TextUtils.isEmpty(crime.description)) {
            for (String type : crimeTypes) {
                if (crime.description.equals(type)) {
                    dropdownCrimeType.setText(type, false);
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(crime.photoPath)) {
            imagePhoto.setImageBitmap(BitmapFactory.decodeFile(crime.photoPath));
        } else {
            imagePhoto.setImageResource(android.R.drawable.ic_menu_camera);
        }
    }

    private void showDatePicker() {
        FragmentManager fm = this.getParentFragmentManager();
        DatePickerFragment dialog = DatePickerFragment.newInstance(crime.date, DIALOG_DATE);
        fm.setFragmentResultListener(DIALOG_DATE, this, this);
        dialog.show(fm, DIALOG_DATE);
    }

    private void pickImageFromGallery() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Snackbar.make(requireView(), "Error picking image", Snackbar.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void saveCrime() {
        if (editTitle.getText() == null || TextUtils.isEmpty(editTitle.getText().toString())) {
            editTitle.setError("Title is required");
            Snackbar.make(requireView(), "Please enter a title", Snackbar.LENGTH_SHORT).show();
            return;
        } else {
            editTitle.setError(null);
        }
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        try {
            crime.title = editTitle.getText() != null ? editTitle.getText().toString() : "";
            crime.date = new Date();
            crime.location = editLocation.getText() != null ? editLocation.getText().toString() : "";
            crime.suspect = editSuspect.getText() != null ? editSuspect.getText().toString() : "";
            crime.description = editDescription.getText() != null ? editDescription.getText().toString() : "";
            viewModel.updateCrime(crime);
            Snackbar.make(requireView(), "Crime saved", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Snackbar.make(requireView(), "Error saving crime", Snackbar.LENGTH_LONG).show();
        } finally {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
        }
    }

    private void deleteCrime() {
        progressBar.setVisibility(View.VISIBLE);
        btnDelete.setEnabled(false);
        try {
            viewModel.deleteCrime(crime);
            Snackbar.make(requireView(), "Crime deleted", Snackbar.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        } catch (Exception e) {
            Snackbar.make(requireView(), "Error deleting crime", Snackbar.LENGTH_LONG).show();
        } finally {
            progressBar.setVisibility(View.GONE);
            btnDelete.setEnabled(true);
        }
    }

    public static CrimeFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putString(ARG_UUID, uuid != null ? uuid.toString() : null);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (crime != null && (crime.title == null || crime.title.isEmpty())) {
                viewModel.deleteCrime(crime);
            }
            this.setEnabled(false);
            NavHostFragment.findNavController(CrimeFragment.this).popBackStack();
        }
    };

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        if (requestKey.equals(DIALOG_DATE)) {
            Date date = (Date) result.getSerializable(DatePickerFragment.RESULT_DATE_KEY);
            crime.date = date;
            viewModel.updateCrime(crime);
            editDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", crime.date));
        }
    }
}
