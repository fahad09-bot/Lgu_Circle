package com.codesses.lgucircle.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.codesses.lgucircle.Authentication.LoginActivity;
import com.codesses.lgucircle.Dialogs.ChangePassDialog;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.activity.YourprofileActivity;
import com.codesses.lgucircle.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingFragment extends Fragment implements View.OnClickListener {

    //    TODO: Context
    FragmentActivity mContext;

    //    TODO: Widgets
    @BindView(R.id.profile_img)
    ImageView Profile_Img;
    @BindView(R.id.user_name)
    TextView User_Name;
    @BindView(R.id.user_email)
    TextView User_Email;
    @BindView(R.id.user_phone_no)
    TextView User_Phone_No;
    @BindView(R.id.your_profile_cv)
    CardView Your_Profile_Cv;
    @BindView(R.id.change_pass_cv)
    CardView Change_Pass_Cv;
    @BindView(R.id.contact_us_cv)
    CardView Contact_Us_Cv;
    @BindView(R.id.feed_back_cv)
    CardView Feed_Back_Cv;
    @BindView(R.id.about_cv)
    CardView About_Cv;
    @BindView(R.id.sign_out_cv)
    CardView Sign_Out_Cv;


    private Intent intent;

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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mContext = getActivity();
        ButterKnife.bind(this, view);

        Log.d("LifeCycle", "onCreateView");


//        TODO: Click Listeners
        Your_Profile_Cv.setOnClickListener(this);
        Change_Pass_Cv.setOnClickListener(this);
        Contact_Us_Cv.setOnClickListener(this);
        Feed_Back_Cv.setOnClickListener(this);
        About_Cv.setOnClickListener(this);
        Sign_Out_Cv.setOnClickListener(this);


        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.your_profile_cv:
                intent = new Intent(mContext, YourprofileActivity.class);
                intent.putExtra("User_Id", FirebaseRef.getUserId());
                startActivity(intent);

                break;
            case R.id.change_pass_cv:

                openChangePassDialog();

                break;
            case R.id.contact_us_cv:

                Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();

                break;
            case R.id.feed_back_cv:

                Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();

                break;
            case R.id.about_cv:

                Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();

                break;
            case R.id.sign_out_cv:

                FirebaseRef.getAuth().signOut();

                intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();
                startActivity(intent);

                break;
        }
    }


    /***********************************
     * Methods Call In Current Fragment
     */
    private void getCurrentUserData() {
        FirebaseRef.getUserRef()
                .child(FirebaseRef.getCurrentUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User model = dataSnapshot.getValue(User.class);

                        if (!TextUtils.isEmpty(model.getProfile_img())) {
                            Picasso.get().load(model.getProfile_img()).into(Profile_Img);
                        }
                        User_Name.setText(model.getFirst_name() + " " + model.getLast_name());
                        User_Email.setText(model.getEmail());
                        User_Phone_No.setText(model.getPhone());
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
}