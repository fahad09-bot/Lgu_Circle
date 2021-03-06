package com.codesses.lgucircle.Dialogs;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Singleton.VolleySingleton;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.CurrentDateAndTime;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.FragmentEventsUploadBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class EventsUpload extends DialogFragment {

    FragmentEventsUploadBinding binding;
    FragmentActivity fragmentActivity;
    Uri selectedImg;
    String stringImageUri = "";
    ArrayList<String> spinnerList = new ArrayList<>();
    private String selectedDepartment = "";
    private String dateTime;
    private String imageUrl;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareSpinnerList();
        setStyle(STYLE_NORMAL, R.style.MyEventBottomSheet);
    }

    private void prepareSpinnerList() {
        spinnerList.add("Select department");
        spinnerList.add("Computer Science");
        spinnerList.add("Physics");
        spinnerList.add("Biological");
        spinnerList.add("Islamic");
        spinnerList.add("Mathematics");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentActivity = getActivity();
        binding = FragmentEventsUploadBinding.
                bind(inflater
                        .inflate(
                                R.layout
                                        .fragment_events_upload,
                                container,
                                false));

        // Inflate the layout for this fragment

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(fragmentActivity, android.R.layout.simple_spinner_item, spinnerList);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dep.setAdapter(stringArrayAdapter);
        binding.dep.setSelection(0);

        binding.eventInfo.setVerticalScrollBarEnabled(true);
        binding.eventInfo.setMovementMethod(new ScrollingMovementMethod());
        binding.addImage.setOnClickListener(this::galleryPermission);
        binding.datePicker.setOnClickListener(this::showDatePicker);
        binding.dep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = spinnerList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.eventUpload.setOnClickListener(this::createEvent);

        setTextChangeListeners();
        return binding.getRoot();
    }

    private void setTextChangeListeners() {
        binding.eventInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0)
                    binding.eventInfo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.eventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0)
                    binding.eventName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void showDatePicker(View view) {
        SwitchDateTimeDialogFragment DTFragment = SwitchDateTimeDialogFragment.newInstance(
                getString(R.string.pickup_date),
                getString(R.string.pickup_date),
                getString(R.string.cancel)
        );


        // Assign values
        DTFragment.startAtCalendarView();
        DTFragment.set24HoursMode(false);
        DTFragment.setMinimumDateTime(new GregorianCalendar(
                CurrentDateAndTime.getYear(),
                CurrentDateAndTime.getMonth(),
                CurrentDateAndTime.getDay())
                .getTime());


        DTFragment.setDefaultDateTime(new GregorianCalendar(
                CurrentDateAndTime.getYear(),
                CurrentDateAndTime.getMonth(),
                CurrentDateAndTime.getDay(),
                CurrentDateAndTime.getHour(),
                CurrentDateAndTime.getMin())
                .getTime());


        // Set listener
        DTFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {

                dateTime = new SimpleDateFormat("MMMM dd, yyyy hh:mm a").format(date);

                binding.datePicker.setText(dateTime);
                binding.datePicker.setError(null);


            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Date is get on negative button click
                dateTime = "";
            }
        });


        // Show
        DTFragment.show(fragmentActivity.getSupportFragmentManager(), "dialog_time");

    }

    private void galleryPermission(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fragmentActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permission, ApplicationUtils.IMAGE_PERMISSION_CODE);
            } else {
                galleryImagePick();
            }
        } else {
            galleryImagePick();
        }
    }

    private void galleryImagePick() {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_PICK);

        intent.setType("image/*");

        someActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        selectedImg = data.getData();
                        stringImageUri = selectedImg.toString();
                        binding.image.setVisibility(View.VISIBLE);
                        Picasso.get().load(stringImageUri).resize(500, 500).into(binding.image);
                    }
                }
            });


    private void createEvent(View view) {

        String eventInfo = binding.eventInfo.getText().toString().trim();
        String eventName = binding.eventName.getText().toString().trim();
        if (TextUtils.isEmpty(eventName))
            binding.eventName.setError("Please provide event name");
        if (TextUtils.isEmpty(eventInfo))
            binding.eventInfo.setError("Please provide event info");
        else if (TextUtils.isEmpty(dateTime))
            binding.datePicker.setError("Please select date time");
        else if (TextUtils.equals(selectedDepartment, "Select department"))
            Toast.makeText(fragmentActivity, "Please select department", Toast.LENGTH_SHORT).show();
        else if (stringImageUri.isEmpty())
            uploadWithoutImage();
        else
            uploadWithImage();
    }

    private void uploadWithImage() {
        uploadImage();
    }

    private void uploadImage() {
        ProgressDialog.ShowProgressDialog(fragmentActivity, R.string.uploading, R.string.please_wait);
        StorageReference reference = FirebaseRef.getEventsStorage();
        UploadTask uploadTask = reference.putFile(selectedImg);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

//                Toast.makeText(PostUploadAV.this, "", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            imageUrl = task.getResult().toString();
                            uploadEvent();

                        } else {
                            Toast.makeText(fragmentActivity, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(e -> {
            ProgressDialog.DismissProgressDialog();
            Toast.makeText(fragmentActivity, "Warning: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadEvent() {
        HashMap<String, Object> hashMap = new HashMap<>();
        String event_id = FirebaseRef.getEventRef().push().getKey();
        hashMap.put("e_id", event_id);
        hashMap.put("name", binding.eventName.getText().toString());
        hashMap.put("info", binding.eventInfo.getText().toString());
        hashMap.put("date", dateTime);
        hashMap.put("department", selectedDepartment);
        hashMap.put("type", 1);
        hashMap.put("image", imageUrl);

        FirebaseRef.getEventRef()
                .child(event_id)
                .setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendNotification();
                        }
                    }
                });
        binding.eventInfo.setText("");
        binding.datePicker.setText("Date time");
        binding.image.setVisibility(View.GONE);
        binding.dep.setSelection(0);
        ProgressDialog.DismissProgressDialog();
        dismiss();
    }

    private void sendNotification() {
        String nId = FirebaseRef.getNotificationRef().push().getKey();

        Map<String, Object> map = new HashMap<>();

        map.put("n_id", nId);
        map.put("timestamp", System.currentTimeMillis());
        map.put("type", 4);
        map.put("sent_by", FirebaseRef.getUserId());
        map.put("title", binding.eventName.getText().toString());
        map.put("message", "Up coming event");
        map.put("is_read", 0);

        assert nId != null;
        FirebaseRef.getNotificationRef().child(nId).updateChildren(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendPushNotification();
                    } else {
                        Log.e("error_tag", task.getException().getMessage());
                    }
                });

    }

    private void sendPushNotification() {
        try {
            JSONObject mainObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            mainObject.put("to", "/topics/" + "events");

//            Notification body
            notificationObject.put("title", "events");
            notificationObject.put("body", fragmentActivity.getString(R.string.new_message));

//            Custom payload
//            dataObject.put("c_id", userId);
//            dataObject.put("user_image", user.getProfile_img());
//            dataObject.put("user_name", user.getFull_name());
            mainObject.put("notification", notificationObject);
            mainObject.put("data", dataObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    ApplicationUtils.FCM_URL,
                    mainObject,
                    response -> {

                        Log.d("FCM_RESPONSE", "sendPushNotification: " + response);
                        Toast.makeText(fragmentActivity, "Posted", Toast.LENGTH_SHORT).show();

                    },
                    error -> Log.d("FCM_RESPONSE", "Error " + error)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("content-type", "application/json");
                    headers.put("authorization", "key=AAAABefXTZo:APA91bFhvO8QxjODhBLgZSyqgnzIRYTOl02b2coksyna5790lvige4VHhKhdjD88dArcjHjgBbAfMl2oKNBKxJqTjLTT4aOqJRy-XFH70vftrxlBUXJU-H6hHWLYeGyLJK1GeoMpJjwB");
                    return headers;
                }
            };

            VolleySingleton.getInstance(fragmentActivity).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void uploadWithoutImage() {
        ProgressDialog.ShowProgressDialog(fragmentActivity, R.string.sending, R.string.please_wait);
        HashMap<String, Object> hashMap = new HashMap<>();
        String event_id = FirebaseRef.getEventRef().push().getKey();
        hashMap.put("e_id", event_id);
        hashMap.put("name", binding.eventName.getText().toString());
        hashMap.put("info", binding.eventInfo.getText().toString());
        hashMap.put("date", dateTime);
        hashMap.put("department", selectedDepartment);
        hashMap.put("type", 0);
        hashMap.put("image", "");

        FirebaseRef.getEventRef().child(event_id).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendNotification();
                        }
                    }
                });

        binding.eventInfo.setText("");
        binding.datePicker.setText("Date time");
        binding.image.setVisibility(View.GONE);
        binding.dep.setSelection(0);
        ProgressDialog.DismissProgressDialog();
        dismiss();
    }


}