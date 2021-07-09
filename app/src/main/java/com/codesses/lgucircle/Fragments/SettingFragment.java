package com.codesses.lgucircle.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.codesses.lgucircle.Authentication.LoginActivity;
import com.codesses.lgucircle.Dialogs.ChangePassDialog;
import com.codesses.lgucircle.Dialogs.ProgressDialog;
import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.activity.IncubationActivity;
import com.codesses.lgucircle.activity.Services.ServicesActivity;
import com.codesses.lgucircle.activity.YourprofileActivity;
import com.codesses.lgucircle.databinding.FragmentSettingBinding;
import com.codesses.lgucircle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingFragment extends Fragment implements View.OnClickListener {

    //    TODO: Context
    FragmentActivity mContext;
    FragmentSettingBinding binding;

    //    TODO: Widgets
    @BindView(R.id.profile_img)
    ImageView Profile_Img;
    @BindView(R.id.user_name)
    TextView User_Name;
    @BindView(R.id.user_email)
    TextView User_Email;
    @BindView(R.id.your_profile_cv)
    CardView Your_Profile_Cv;
    @BindView(R.id.change_pass_cv)
    CardView Change_Pass_Cv;
    @BindView(R.id.contact_us_cv)
    CardView Contact_Us_Cv;
    @BindView(R.id.about_cv)
    CardView About_Cv;
    @BindView(R.id.sign_out_cv)
    CardView Sign_Out_Cv;
    @BindView(R.id.incubation_cv)
    CardView Incubation_Cv;
    @BindView(R.id.freelance_cv)
    CardView Freelance_Cv;


    private Intent intent;
    private Uri selectedImg;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        getCurrentUserData();
        Log.d("LifeCycle", "onAttach");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCurrentUserData();
        Log.d("LifeCycle", "onActivityCreated");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LifeCycle", "onCreate");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO: Inflate the layout for this fragment
        binding = FragmentSettingBinding.bind(inflater.inflate(R.layout.fragment_setting, container, false));

        mContext = getActivity();
        ButterKnife.bind(this, binding.getRoot());

        Log.d("LifeCycle", "onCreateView");


//        TODO: Click Listeners
        binding.yourProfileCv.setOnClickListener(this);
        binding.changePassCv.setOnClickListener(this);
        binding.contactUsCv.setOnClickListener(this);
        binding.aboutCv.setOnClickListener(this);
        binding.signOutCv.setOnClickListener(this);
        binding.incubationCv.setOnClickListener(this);
        binding.freelanceCv.setOnClickListener(this);
        binding.add.setOnClickListener(this);


        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                galleryPermission();
                break;
            case R.id.your_profile_cv:
                intent = new Intent(mContext, YourprofileActivity.class);
                intent.putExtra(Constants.USER_ID, FirebaseRef.getUserId());
                startActivity(intent);
                break;

            case R.id.change_pass_cv:
                openChangePassDialog();
                break;

            case R.id.contact_us_cv:
                Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.about_cv:
                Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.sign_out_cv:
                new AlertDialog.Builder(mContext)
                        .setMessage("Are you sure you want to sign out?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                updateToken();
                            }
                        }).create().show();

                break;

            case R.id.incubation_cv:
                intent = new Intent(mContext, IncubationActivity.class);
                startActivity(intent);
                break;

            case R.id.freelance_cv:
                intent = new Intent(mContext, ServicesActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void updateToken() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fcmToken", "");
        FirebaseRef.getUserRef()
                .child(FirebaseRef.getCurrentUserId())
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseRef.getAuth().signOut();
                            intent = new Intent(getActivity(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getActivity().finish();
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void galleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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


    /***********************************
     * Methods Call In Current Fragment
     */
    private void getCurrentUserData() {
        Gson gson = new Gson();
        User user = gson.fromJson(SharedPrefManager.getInstance(mContext).getSharedData(SharedPrefKey.USER), User.class);
        if (user.getProfile_img() != null) {
            Picasso.get().load(user.getProfile_img()).into(Profile_Img);
        }
        User_Name.setText(user.getFirst_name() + " " + user.getLast_name());
        User_Email.setText(user.getEmail());
//        FirebaseRef.getUserRef()
//                .child(FirebaseRef.getCurrentUserId())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User model = dataSnapshot.getValue(User.class);
//
//
//                    }
//
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    private void openChangePassDialog() {
        ChangePassDialog changePassDialog = new ChangePassDialog();
        //                TODO: ReAuthenticate Password
//        reAuthenticatePassword(oldPass, newPass)
        changePassDialog.show(getActivity().getSupportFragmentManager(), "Change Pass Dialog");
    }

    private void reAuthenticatePassword(String oldPass, String newPass) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(FirebaseRef.getUserEmail(), oldPass);

        FirebaseRef.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                FirebaseRef.getCurrentUser().updatePassword(newPass).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

                        passSaveInDatabase(newPass);
//                        Toast.makeText(getActivity(), getString(R.string.pass_updated), Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getActivity(), "Alert! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            } else {
                Toast.makeText(getActivity(), "Alert! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void passSaveInDatabase(String pass) {
        Map<String, Object> map = new HashMap<>();

        map.put("password", pass);

        FirebaseRef.getUserRef()
                .child(FirebaseRef.getUserId())
                .updateChildren(map)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(getActivity(), getString(R.string.pass_updated), Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getActivity(), "Alert! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

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
                        ProgressDialog.ShowProgressDialog(mContext, R.string.profile, R.string.uploading);
                        uploadToStorage(data.getData());
                    }
                }
            });

    private void uploadToStorage(Uri data) {
        StorageReference reference = FirebaseRef.getProfileStorage().child(FirebaseRef.getUserId());
        UploadTask uploadTask = reference.putFile(data);

        uploadTask.addOnProgressListener(taskSnapshot -> {

//                Toast.makeText(PostUploadAV.this, "", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot ->
                reference.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profileUpload(task.getResult().toString());
                        ProgressDialog.DismissProgressDialog();

                    } else {
                        ProgressDialog.DismissProgressDialog();
                        Log.e("ERROR_TAG", task.getException().getMessage());
                    }
                })).addOnFailureListener(e -> {
            ProgressDialog.DismissProgressDialog();
            Log.e("ERROR_TAG", e.getMessage());
        });

    }

    private void profileUpload(String profileImgUrl) {
        Map<String, Object> map = new HashMap<>();

        map.put("profile_img", profileImgUrl);

        FirebaseRef.getUserRef().child(FirebaseRef.getUserId())
                .updateChildren(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();

//                        Update user data to shared preference

                    } else {
                        Log.e("ERROR_TAG", task.getException().getMessage());
                    }
                    ProgressDialog.DismissProgressDialog();
                });
    }

}