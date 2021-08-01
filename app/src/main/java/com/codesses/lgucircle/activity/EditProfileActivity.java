package com.codesses.lgucircle.activity;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.CheckEmptyFields;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    //   Context
    AppCompatActivity mContext;

    //   Widgets
    @BindView(R.id.toolbar)
    Toolbar Tool_Bar;

    @BindView(R.id.e_fname)
    EditText E_FName;
    @BindView(R.id.e_lname)
    EditText E_LName;
    @BindView(R.id.e_email)
    TextView E_Email;
    @BindView(R.id.sp_country_code)
    CountryCodePicker Sp_Country_Code;
    @BindView(R.id.e_phone_no)
    EditText E_Phone_No;
    @BindView(R.id.bio)
    EditText Bio;
    @BindView(R.id.update_btn)
    Button Update_Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mContext = this;
        ButterKnife.bind(mContext);

        setSupportActionBar(Tool_Bar);
        setTitle(R.string.edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        TODO: Get Current Data
        getCurrentUserData();


//        TODO: Click Listener
        Update_Btn.setOnClickListener(this::updateData);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }


    private void getCurrentUserData() {
        FirebaseRef.getUserRef()
                .child(FirebaseRef.getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        E_FName.setText(user.getFirst_name());
                        E_LName.setText(user.getLast_name());
                        E_Email.setText(user.getEmail());
                        E_Phone_No.setText(user.getPhone().substring(3, user.getPhone().length()));
                        Bio.setText(user.getBio());
//                        Sp_Country_Code.setCountryForNameCode();
                        Sp_Country_Code.setCountryForPhoneCode(Integer.parseInt(user.getPhone().substring(1, 3)));
//                        app:ccp_defaultNameCode="PK"


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mContext, "Alert!: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateData(View view) {
        String firstName = E_FName.getText().toString().trim();
        String lastName = E_LName.getText().toString().trim();
        String phoneNo = E_Phone_No.getText().toString().trim();
        String bio = Bio.getText().toString().trim();

        if (CheckEmptyFields.isEditText(mContext, firstName, E_FName) &&
                CheckEmptyFields.isEditText(mContext, lastName, E_LName) &&
                CheckEmptyFields.isEditText(mContext, phoneNo, E_Phone_No)) {

            Map<String, Object> map = new HashMap<>();

            if (bio.isEmpty()) {
                map.put("first_name", firstName);
                map.put("last_name", lastName);
                map.put("phone", Sp_Country_Code.getSelectedCountryCodeWithPlus() + phoneNo);
                map.put("bio", "");
            } else {
                map.put("first_name", firstName);
                map.put("last_name", lastName);
                map.put("phone", Sp_Country_Code.getSelectedCountryCodeWithPlus() + phoneNo);
                map.put("bio", bio);
            }


            FirebaseRef.getUserRef()
                    .child(FirebaseRef.getUserId())
                    .updateChildren(map)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
//                                onBackPressed();
                            Toast.makeText(mContext, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            Gson gson = new Gson();
                            User user = gson.fromJson(SharedPrefManager.getInstance(mContext).getSharedData(SharedPrefKey.USER), User.class);
                            user.setFirst_name(firstName);
                            user.setLast_name(lastName);
                            user.setPhone(Sp_Country_Code.getSelectedCountryCodeWithPlus() + phoneNo);
                            user.setBio(bio);
                            SharedPrefManager.getInstance(mContext).storeSharedData(SharedPrefKey.USER, user);

                        } else {
                            Toast.makeText(mContext, "Alert!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}