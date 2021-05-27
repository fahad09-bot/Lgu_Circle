package com.codesses.lgucircle.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.signup_email)
    EditText Signup_email;
    @BindView(R.id.signup_pass)
    EditText Signup_pass;
    @BindView(R.id.signup_btn)
    Button Signup_btn;
    @BindView(R.id.already_user)
    Button Already_user;
    @BindView(R.id.f_name)
    EditText First_name;
    @BindView(R.id.l_name)
    EditText Last_name;
    @BindView(R.id.dep)
    EditText Department;
    @BindView(R.id.dob)
    EditText Date_of_birth;
    @BindView(R.id.phone_no)
    EditText Phone_number;
    @BindView(R.id.roll_no)
    EditText Roll_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        Already_user.setOnClickListener(this::backPress);
        Signup_btn.setOnClickListener(this::getUserData);

    }

    private void backPress(View view) {
        onBackPressed();
    }

    private void getUserData(View view) {
        String email = Signup_email.getText().toString().trim();
        String pass = Signup_pass.getText().toString().trim();
        String f_name = First_name.getText().toString().trim();
        String l_name = Last_name.getText().toString().trim();
        String dep = Department.getText().toString().trim();
        String dob = Date_of_birth.getText().toString().trim();
        String phone = Phone_number.getText().toString().trim();
        String roll_no = Roll_no.getText().toString().trim().toLowerCase();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(f_name) && !TextUtils.isEmpty(l_name) && !TextUtils.isEmpty(dep) && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(phone) &&!TextUtils.isEmpty(roll_no)) {
            signup(email, pass, f_name, l_name, dep, dob, phone, roll_no);
        } else {
            Toast.makeText(this, "Fields Must be Filled", Toast.LENGTH_SHORT).show();
        }
    }

    private void signup(String email, String pass, String f_name, String l_name, String dep, String dob, String phone, String roll_no) {
        FirebaseRef.getAuth()
                .createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseRef.getCurrentUser().sendEmailVerification();
                            dataSaveToFirebase(email, pass, f_name, l_name, dep, dob, phone, roll_no);
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void dataSaveToFirebase(String email, String pass, String f_name, String l_name, String dep, String dob, String phone, String roll_no) {
        String userId = FirebaseRef.getCurrentUserId();

        Map<String, Object> map = new HashMap<>();

        map.put("u_id", userId);
        map.put("email", email);
        map.put("password", pass);
        map.put("profile_img", ApplicationUtils.DEFAULT_IMAGE);
        map.put("first_name", f_name);
        map.put("last_name", l_name);
        map.put("department", dep);
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