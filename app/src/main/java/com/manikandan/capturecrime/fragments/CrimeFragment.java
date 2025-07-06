package com.manikandan.capturecrime.fragments;

import android.app.Activity;
import android.content.Intent;
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

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.manikandan.capturecrime.data.CrimeEntity;
import com.manikandan.capturecrime.viewmodel.CrimeDetailViewModel;
import com.manikandan.capturecrime.R;
import com.manikandan.capturecrime.utils.ImageUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
    private UUID crimeId;
    private boolean isNewCrime = false; // Track if this is a new crime

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
                        try {
                            crime.photoPath = ImageUtils.copyImageToAppStorage(requireContext(), selectedImage);
                            viewModel.updateCrime(crime); // Immediately persist image change
                            loadCrimeImage(crime.photoPath);
                        } catch (IOException e) {
                            Snackbar.make(requireView(), "Failed to save image", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(requireView(), "No image selected", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(requireView(), "Image selection cancelled", Snackbar.LENGTH_SHORT).show();
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
        // Make date field non-editable
        editDate.setFocusable(false);
        editDate.setClickable(true);
        editDate.setKeyListener(null);
        editDate.setOnClickListener(view -> showMaterialDatePicker());
        btnAttachPhoto.setOnClickListener(view -> pickImageFromGallery());
        btnSave.setOnClickListener(view -> saveCrime());
        btnDelete.setOnClickListener(view -> deleteCrime());

        // Remove problematic focus change listeners that were causing data loss
        // Fields will be read directly in saveCrime() method

        switchSolved.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (crime != null) {
                crime.solved = isChecked;
            }
        });
        btnAttachPhoto.setEnabled(false); // Disable attach photo until crime is loaded
        if (crimeId != null) {
            viewModel.getCrimeById(crimeId).observe(getViewLifecycleOwner(), crimeEntity -> {
                if (crimeEntity == null) return;
                crime = crimeEntity;
                bindCrimeToUI();
                btnAttachPhoto.setEnabled(true); // Enable after loaded from DB
            });
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            // For new crime, don't save to DB immediately
            isNewCrime = true;
            crime = new com.manikandan.capturecrime.data.CrimeEntity(UUID.randomUUID(), "", new Date(), false, "", "", "", "");
            bindCrimeToUI();
            btnAttachPhoto.setEnabled(false); // Disable until crime is saved
            btnDelete.setVisibility(View.GONE);
        }
        return v;
    }

    private void bindCrimeToUI() {
        // Only bind data if fields are currently empty to avoid overwriting user input
        if (TextUtils.isEmpty(editCaseId.getText())) {
            editCaseId.setText(crime.id.toString());
        }
        if (TextUtils.isEmpty(editTitle.getText())) {
            editTitle.setText(crime.title);
        }
        if (TextUtils.isEmpty(editLocation.getText())) {
            editLocation.setText(crime.location);
        }
        if (TextUtils.isEmpty(editSuspect.getText())) {
            editSuspect.setText(crime.suspect);
        }

        // Always update date display
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editDate.setText(sdf.format(crime.date));

        // Always update switch state
        switchSolved.setChecked(crime.solved);

        // Only set crime type if dropdown is empty and we have data
        if (TextUtils.isEmpty(dropdownCrimeType.getText()) && !TextUtils.isEmpty(crime.description)) {
            for (String type : crimeTypes) {
                if (crime.description.equals(type)) {
                    dropdownCrimeType.setText(type, false);
                    break;
                }
            }
        }

        // Always update image
        if (!TextUtils.isEmpty(crime.photoPath)) {
            loadCrimeImage(crime.photoPath);
        } else {
            imagePhoto.setImageResource(android.R.drawable.ic_menu_camera);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showMaterialDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(crime.date != null ? crime.date.getTime() : System.currentTimeMillis())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            crime.date = new Date(selection);
            // Don't update the crime object immediately to avoid interfering with other fields
            // Just update the UI display
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            editDate.setText(sdf.format(crime.date));
        });
        datePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void pickImageFromGallery() {
        // Don't allow image attachment for unsaved new crimes
        if (isNewCrime) {
            Snackbar.make(requireView(), "Please save the crime first before adding images", Snackbar.LENGTH_LONG).show();
            return;
        }

        try {
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Snackbar.make(requireView(), "Error picking image", Snackbar.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void saveCrime() {
        performSave();
    }

    private void performSave() {
        // Get current text from all fields directly, regardless of focus state
        String title = getCurrentFieldText(editTitle);
        String location = getCurrentFieldText(editLocation);
        String suspect = getCurrentFieldText(editSuspect);
        String crimeType = getCurrentFieldText(dropdownCrimeType);

        // Validate mandatory fields - validate each field independently
        boolean titleError = false;
        boolean crimeTypeError = false;

        // Check title field independently
        if (TextUtils.isEmpty(title)) {
            editTitle.setError("Title is required");
            titleError = true;
        } else {
            editTitle.setError(null);
        }

        // Check crime type field independently
        if (TextUtils.isEmpty(crimeType)) {
            dropdownCrimeType.setError("Crime type is required");
            crimeTypeError = true;
        } else {
            dropdownCrimeType.setError(null);
        }

        // Set focus to first field with error
        if (titleError) {
            editTitle.requestFocus();
        } else if (crimeTypeError) {
            dropdownCrimeType.requestFocus();
        }

        // Show error if any validation failed
        if (titleError || crimeTypeError) {
            String errorMessage = "Please fill in all required fields:";
            if (titleError) errorMessage += " Title";
            if (crimeTypeError) errorMessage += " Crime Type";

            Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        try {
            // Update crime object with latest values
            crime.title = title;
            crime.location = location;
            crime.suspect = suspect;
            crime.description = crimeType; // Store the selected crime type
            crime.solved = switchSolved.isChecked();

            if (isNewCrime) {
                viewModel.insertCrime(crime);
                isNewCrime = false;
                btnAttachPhoto.setEnabled(true); // Enable after saving
            } else {
                viewModel.updateCrime(crime);
            }

            Snackbar.make(requireView(), "Crime saved successfully", Snackbar.LENGTH_SHORT).show();

            // Navigate back to list after successful save
            NavHostFragment.findNavController(this).popBackStack();

        } catch (Exception e) {
            Snackbar.make(requireView(), "Error saving crime", Snackbar.LENGTH_LONG).show();
        } finally {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
        }
    }

    /**
     * Robust method to get current text from any text field
     */
    private String getCurrentFieldText(android.widget.TextView textView) {
        if (textView == null) return "";

        // Try multiple approaches to get the text
        String text = "";

        // Method 1: getText()
        if (textView.getText() != null) {
            text = textView.getText().toString().trim();
        }

        // Method 2: getEditableText() for EditText fields
        if (TextUtils.isEmpty(text) && textView instanceof android.widget.EditText) {
            android.text.Editable editable = ((android.widget.EditText) textView).getEditableText();
            if (editable != null) {
                text = editable.toString().trim();
            }
        }

        // Method 3: Direct content access (last resort)
        if (TextUtils.isEmpty(text)) {
            CharSequence charSeq = textView.getText();
            if (charSeq != null) {
                text = charSeq.toString().trim();
            }
        }

        return text;
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

    private void loadCrimeImage(String uriString) {
        progressBar.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(uriString)) {
            imagePhoto.setImageResource(android.R.drawable.ic_menu_camera);
            progressBar.setVisibility(View.GONE);
            return;
        }
        // Load from file path
        Glide.with(this)
            .load(new java.io.File(uriString))
            .placeholder(android.R.drawable.ic_menu_camera)
            .error(android.R.drawable.ic_menu_camera)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(new RequestListener<>() {
                @Override
                public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, @NonNull Object model, @NonNull Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(requireView(), "Failed to load image", Snackbar.LENGTH_SHORT).show();
                    return false;
                }
                @Override
                public boolean onResourceReady(@NonNull android.graphics.drawable.Drawable resource, @NonNull Object model, @NonNull Target<android.graphics.drawable.Drawable> target, @NonNull com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            })
            .into(imagePhoto);
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            // For new crimes that haven't been saved, just go back without saving
            if (isNewCrime) {
                // Don't save empty crime to database
                NavHostFragment.findNavController(CrimeFragment.this).popBackStack();
                return;
            }

            // For existing crimes, save any changes before going back
            if (crime != null) {
                // Get latest values from fields
                String title = editTitle.getText() != null ? editTitle.getText().toString().trim() : "";
                String location = editLocation.getText() != null ? editLocation.getText().toString().trim() : "";
                String suspect = editSuspect.getText() != null ? editSuspect.getText().toString().trim() : "";
                String crimeType = dropdownCrimeType.getText() != null ? dropdownCrimeType.getText().toString().trim() : "";

                // Only update if mandatory fields are filled
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(crimeType)) {
                    crime.title = title;
                    crime.location = location;
                    crime.suspect = suspect;
                    crime.description = crimeType;
                    crime.solved = switchSolved.isChecked();
                    viewModel.updateCrime(crime);
                }
            }

            this.setEnabled(false);
            NavHostFragment.findNavController(CrimeFragment.this).popBackStack();
        }
    };

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        // No longer needed, handled by MaterialDatePicker
    }
}
