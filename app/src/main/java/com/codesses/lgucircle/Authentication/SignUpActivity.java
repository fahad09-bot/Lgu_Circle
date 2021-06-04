package com.codesses.lgucircle.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    AppCompatActivity mContext;
    ActivitySignUpBinding binding;

    String selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext,R.layout.activity_sign_up);
        ButterKnife.bind(this);

        binding.alreadyUser.setOnClickListener(this::backPress);
        binding.signupBtn.setOnClickListener(this::getUserData);
        selectedType = binding.radioUser.getText().toString().toLowerCase().trim();
        setGroupListener();
    }

    private void setGroupListener() {
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = group.findViewById(checkedId);

                selectedType = radioButton.getText().toString().toLowerCase().trim();
                if (selectedType.equals(getString(R.string.user).toLowerCase()))
                    binding.rollNo.setVisibility(View.VISIBLE);
                else if (selectedType.equals(getString(R.string.staff).toLowerCase()))
                    binding.rollNo.setVisibility(View.GONE);
            }
        });
    }

    private void backPress(View view) {
        onBackPressed();
    }

    private void getUserData(View view) {
        String email = binding.signupEmail.getText().toString().trim();
        String pass = binding.signupPass.getText().toString().trim();
        String f_name = binding.fName.getText().toString().trim();
        String l_name = binding.lName.getText().toString().trim();
        String dep = binding.dep.getText().toString().trim();
        String dob = binding.dob.getText().toString().trim();
        String phone = binding.phoneNo.getText().toString().trim();
        String roll_no = "";
        if (selectedType.equals(getString(R.string.user).toLowerCase()))
             roll_no = binding.rollNo.getText().toString().trim().toLowerCase();

        if (selectedType.equals(getString(R.string.user).toLowerCase())) {
            String[] emailString = email.split("@");
            if (!emailString[1].equals("lgu.edu.pk")) {
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(f_name) && !TextUtils.isEmpty(l_name) && !TextUtils.isEmpty(dep) && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(roll_no)) {
                    signup(email, pass, f_name, l_name, dep, dob, phone, roll_no);
                } else {
                    Toast.makeText(this, "Fields Must be Filled", Toast.LENGTH_SHORT).show();
                }
            } else {
                binding.signupEmail.setError("You can't use this type of email");
            }

        } else if (selectedType.equals(getString(R.string.staff).toLowerCase()))
        {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(f_name) && !TextUtils.isEmpty(l_name) && !TextUtils.isEmpty(dep) && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(phone)) {
                signup(email, pass, f_name, l_name, dep, dob, phone, roll_no);
            } else {
                Toast.makeText(this, "Fields Must be Filled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signup(String email, String pass, String f_name, String l_name, String dep, String dob, String phone, @Nullable String roll_no) {
        FirebaseRef.getAuth()
                .createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseRef.getCurrentUser().sendEmailVerification();
                            if (roll_no.isEmpty())
                            {
                                dataSaveToFirebase(email, pass, f_name, l_name, dep, dob, phone, roll_no);
                            }
                            else
                                dataSaveToFirebase(email, pass, f_name, l_name, dep, dob, phone, roll_no);

                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void dataSaveToFirebase(String email, String pass, String f_name, String l_name, String dep, String dob, String phone, @Nullable String roll_no) {
        String userId = FirebaseRef.getCurrentUserId();

        Map<String, Object> map = new HashMap<>();

        map.put("u_id", userId);
        map.put("email", email);
        map.put("password", pass);
        map.put("profile_img", ApplicationUtils.DEFAULT_IMAGE);
        map.put("first_name", f_name);
        map.put("last_name", l_name);
        map.put("department", dep);
        map.put("type", selectedType);
        if (selectedType.equals(getString(R.string.user).toLowerCase().trim()))
            map.put("roll_no", roll_no);

        map.put("date_of_birth", dob);
        map.put("phone", phone);


        FirebaseRef.getUserRef()
                .child(userId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            Toast.makeText(SignUpActivity.this, "user created successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}