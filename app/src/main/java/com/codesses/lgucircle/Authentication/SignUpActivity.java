package com.codesses.lgucircle.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codesses.lgucircle.Dialogs.ProgressDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    AppCompatActivity mContext;
    ActivitySignUpBinding binding;

    String selectedType;

    String selectedDepartment = "",
            selectedYear = "",
            selectedProgram = "",
            selectedSession = "";

    boolean isFirstName = false,
            isLastName = false,
            isEmail = false,
            isPhoneNo = false,
            isPassword = false,
            isRollNo = false,
            isYear = false,
            isSession = false,
            isProgram = false,
            isDepartment = false,
            isConfirmPassword = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_sign_up);

        binding.signupPass.setOnFocusChangeListener(this);
        binding.alreadyUser.setOnClickListener(this::backPress);
        binding.signupBtn.setOnClickListener(this::getUserData);
        binding.fullRollNo.setVisibility(View.GONE);
        selectedType = binding.radioUser.getText().toString().toLowerCase().trim();

        binding.backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//    TODO: Set Spinners
        spinnerSessions();
        spinnerYears();
        spinnerPrograms();
        spinnerDepartment();
        setGroupListener();
//    TODO: Text Change Listeners
        setTextChangeListeners();

    }

    //    TODO: Spinners Methods
    private void spinnerSessions() {
        //       Set 1st item disable
        ArrayAdapter<String> session_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                getResources().getStringArray(R.array.sessions)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the 1st item from Spinner
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        session_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        binding.spSession.setAdapter(session_adapter);

        binding.spSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (selectedType.equals(getString(R.string.student).toLowerCase().trim())) {
                    selectedSession = adapterView.getItemAtPosition(position).toString();
                    isSession = true;
                    if (selectedSession.equals(getString(R.string.choose_session))) {
                        isSession = false;
                        selectedSession = "";
                    }
                }
                updateRollNo();
                updateButtonState();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void spinnerYears() {
//       Set 1st item disable
        ArrayAdapter<String> years_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                getResources().getStringArray(R.array.years)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the 1st item from Spinner
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        years_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        binding.spYear.setAdapter(years_adapter);

        binding.spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = parent.getItemAtPosition(position).toString();
                isYear = true;
                if (selectedYear.equals(getString(R.string.choose_year))) {
                    isYear = false;
                    selectedYear = "";
                }
                updateRollNo();
                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void spinnerPrograms() {
        //       Set 1st item disable
        ArrayAdapter<String> programs_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                getResources().getStringArray(R.array.programs)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the 1st item from Spinner
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        programs_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        binding.spProgram.setAdapter(programs_adapter);

        binding.spProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProgram = parent.getItemAtPosition(position).toString();
                isProgram = true;
                if (selectedProgram.equals(getString(R.string.choose_program))) {
                    isProgram = false;
                    selectedProgram = "";
                }
                updateRollNo();
                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void spinnerDepartment() {
        ArrayAdapter<String> department_adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                getResources().getStringArray(R.array.departments)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the 1st item from Spinner
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        department_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        binding.spDep.setAdapter(department_adapter);

        binding.spDep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = parent.getItemAtPosition(position).toString();
                isDepartment = true;
                binding.rollNo.setEnabled(true);
                if (selectedDepartment.equals(getString(R.string.choose_department))) {
                    isDepartment = false;
                    selectedDepartment = "";
                    binding.rollNo.setEnabled(false);
                }
                updateRollNo();
                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setGroupListener() {
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = group.findViewById(checkedId);

                selectedType = radioButton.getText().toString().toLowerCase().trim();
                if (selectedType.equals(getString(R.string.student).toLowerCase())) {
                    binding.spSession.setVisibility(View.VISIBLE);
                    binding.spYear.setVisibility(View.VISIBLE);
                    binding.spDep.setVisibility(View.VISIBLE);
                    binding.spProgram.setVisibility(View.VISIBLE);
                    binding.rollNo.setVisibility(View.VISIBLE);
                    binding.spSession.setSelection(0);
                    binding.spYear.setSelection(0);
                    binding.spProgram.setSelection(0);
                    binding.spDep.setSelection(0);

                } else if (selectedType.equals(getString(R.string.staff).toLowerCase())) {
                    binding.spDep.setVisibility(View.VISIBLE);
                    binding.spProgram.setVisibility(View.VISIBLE);
                    binding.rollNo.setVisibility(View.GONE);
                    binding.spSession.setVisibility(View.GONE);
                    binding.spYear.setVisibility(View.GONE);

                    if (isSession) {
                        isSession = false;
                        updateRollNo();
                    }
                }
            }
        });
    }

    private void setTextChangeListeners() {
//          TODO: First Name Change Listener

        binding.fName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                firstNameValid(s.toString());
            }
        });

//        TODO: Last Name Change Listener

        binding.lName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lastNameValid(s.toString());
            }
        });

        binding.signupEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailValid(s.toString());
            }
        });

        binding.rollNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validRollNo(s.toString());
            }
        });

        binding.phoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validPhoneNo(s.toString());
            }
        });

        binding.signupPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0)
                    binding.cnfrmPass.setEnabled(true);
                else
                    binding.cnfrmPass.setEnabled(false);
                hintChangeOnPassword(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validPassword(s.toString());
            }
        });

        binding.cnfrmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validConfirmPassword(s.toString());
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            slideDown(binding.clPasswordHint);
        } else {
            slideUp(binding.clPasswordHint);
        }

    }

    public void slideDown(View view) {
        view.setVisibility(View.VISIBLE);
//        TranslateAnimation animate = new TranslateAnimation(
//                0,
//                0,
//                view.getHeight(),
//                0);
//        animate.setDuration(500);
//        animate.setFillAfter(true);
//        view.startAnimation(animate);
    }

    public void slideUp(View view) {
        view.setVisibility(View.GONE);
//        TranslateAnimation animate = new TranslateAnimation(
//                0,
//                0,
//                0,
//                view.getHeight());
//        animate.setDuration(500);
//        animate.setFillAfter(true);
//        view.startAnimation(animate);
    }


    /*******************************************************************************************************************************************************
     *                                                                                                                                                     *
     *                                                                          Check Methods                                                              *
     *                                                                                                                                                     *
     *******************************************************************************************************************************************************/

    private void firstNameValid(String fName) {
        if (!TextUtils.isEmpty(fName)) {
            if (fName.length() >= 3) {
                isFirstName = true;
                binding.fName.setError(null);
            } else {
                isFirstName = false;
                binding.fName.setError(getString(R.string.invalid_first_name));
            }
        } else {
            isFirstName = false;
            binding.fName.setError(null);
        }

        updateButtonState();
    }

    private void lastNameValid(String lName) {
        if (!TextUtils.isEmpty(lName)) {
            if (lName.length() >= 3) {
                isLastName = true;
                binding.fName.setError(null);
            } else {
                isLastName = false;
                binding.lName.setError(getString(R.string.invalid_last_name));
            }
        } else {
            isLastName = false;
            binding.lName.setError(null);
        }

        updateButtonState();
    }

    private void emailValid(String email) {
        if (!TextUtils.isEmpty(email)) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                isEmail = true;
                binding.signupEmail.setError(null);
            } else {
                isEmail = false;
                binding.signupEmail.setError(getString(R.string.invalid_email_format));
            }

        } else {
            isEmail = false;
            binding.signupEmail.setError(null);
        }
    }

    private void validRollNo(String rollNo) {
        if (!TextUtils.isEmpty(rollNo)) {
            if (rollNo.length() == 3) {

                binding.rollNo.setError(null);
            } else {
                binding.rollNo.setError(getString(R.string.invalid_last_name));
            }
        } else {
            binding.rollNo.setError(null);
        }

        updateRollNo();
        updateButtonState();
    }

    private void validPhoneNo(String phoneNo) {
        if (!TextUtils.isEmpty(phoneNo)) {
            if (phoneNo.length() >= 10) {
                isPhoneNo = true;
                binding.phoneNo.setError(null);
            } else {
                isPhoneNo = false;
                binding.phoneNo.setError(getString(R.string.invalid_phone_no_format));
            }

        } else {
            isPhoneNo = false;
            binding.phoneNo.setError(null);
        }

        updateButtonState();
    }

    private void hintChangeOnPassword(CharSequence s) {
        Pattern digitPattern = Pattern.compile("(.)*(\\d)(.)*");
        Pattern lettersPattern = Pattern.compile("(.)*[a-zA-Z](.)*");
        Pattern specialPattern = Pattern.compile("(?=.*[@#$%^&+=])");
        boolean digits = digitPattern.matcher(s).find();
        boolean letters = lettersPattern.matcher(s).find();
        boolean specialChar = specialPattern.matcher(s).find();

//                        Check length of the password
        if (s.length() >= 6) {
            binding.tvPasswordHint2.setTextColor(ContextCompat.getColor(mContext, R.color.dim_green));
        } else {
            binding.tvPasswordHint2.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

        if (digits) {
            binding.tvPasswordHint4.setTextColor(ContextCompat.getColor(mContext, R.color.dim_green));
        } else {
            binding.tvPasswordHint4.setTextColor(
                    ContextCompat.getColor(mContext, R.color.red));
        }

        if (letters) {
            binding.tvPasswordHint6.setTextColor(ContextCompat.getColor(mContext, R.color.dim_green));
        } else {
            binding.tvPasswordHint6.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

        if (specialChar) {
            binding.tvPasswordHint8.setTextColor(ContextCompat.getColor(mContext, R.color.dim_green));
        } else {
            binding.tvPasswordHint8.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

    }

    private void validPassword(String password) {
        if (!TextUtils.isEmpty(password)) {
            if (ApplicationUtils.isValidPassword(password)) {
                isPassword = true;
                binding.signupPass.setError(null);
            } else {
                isPassword = false;
                binding.signupPass.setError(mContext.getString(R.string.invalid_password_format));
            }

        } else {
            isPassword = false;
            binding.signupPass.setError(null);
        }

        updateButtonState();
    }

    private void validConfirmPassword(String confirmPassword) {
        String pass = binding.signupPass.getText().toString();
        if (!TextUtils.isEmpty(confirmPassword)) {
            if (pass.equals(confirmPassword)) {
                isConfirmPassword = true;
                binding.cnfrmPass.setError(null);
            } else {
                isConfirmPassword = false;
                binding.cnfrmPass.setError(mContext.getString(R.string.invalid_confirm_password_not_matched));
            }

        } else {
            isConfirmPassword = false;
            binding.cnfrmPass.setError(null);
        }


        updateButtonState();
    }


    private void backPress(View view) {
        onBackPressed();
    }

    private void getUserData(View view) {
        String email = binding.signupEmail.getText().toString().trim();
        String pass = binding.signupPass.getText().toString().trim();
        String f_name = binding.fName.getText().toString().trim();
        String l_name = binding.lName.getText().toString().trim();
        String phone = binding.phoneNo.getText().toString().trim();
        String dep = "";
        if (isProgram && isDepartment)
            dep = selectedProgram + selectedDepartment;

        String roll_no = "";
        if (selectedType.equals(getString(R.string.student).toLowerCase()))
            roll_no = binding.fullRollNo.getText().toString().trim();

        if (selectedType.equals(getString(R.string.student).toLowerCase())) {
            if (!email.endsWith("@lgu.edu.pk")) {
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(f_name) && !TextUtils.isEmpty(l_name) && !TextUtils.isEmpty(dep) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(roll_no)) {
                    ProgressDialog.ShowProgressDialog(mContext, R.string.sign_up, R.string.please_wait);
                    signup(email, pass, f_name, l_name, dep, phone, roll_no);
                } else {
                    Toast.makeText(this, "Fields Must be Filled", Toast.LENGTH_SHORT).show();
                }
            } else {
                binding.signupEmail.setError("You can't use this type of email");
            }

        } else if (selectedType.equals(getString(R.string.staff).toLowerCase())) {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(f_name) && !TextUtils.isEmpty(l_name) && !TextUtils.isEmpty(dep) && !TextUtils.isEmpty(phone)) {
                ProgressDialog.ShowProgressDialog(mContext, R.string.signing_up, R.string.please_wait);
                if(!email.endsWith("@lgu.edu.pk"))
                {
                    Toast.makeText(mContext, "you have to use an email that ends with @lgu.edu.pk", Toast.LENGTH_SHORT).show();
                    binding.signupEmail.setError("must ends with @lgu.edu.pk");
                    ProgressDialog.DismissProgressDialog();
                    return;
                }
                else if (email.endsWith("@lgu.edu.pk"))
                {
                    if (ApplicationUtils.isStaffEmailValid(email))
                    {
                        Toast toast = Toast.makeText(mContext, "You can't use this type of email", Toast.LENGTH_SHORT);
                        toast.show();
                        binding.signupEmail.setError("not allowed");
                        ProgressDialog.DismissProgressDialog();
                        return;
                    }
                    else
                    {
                        signup(email, pass, f_name, l_name, dep, phone, roll_no);
                    }
                }
            } else {
                Toast.makeText(this, "Fields Must be Filled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signup(String email, String pass, String f_name, String l_name, String dep, String phone, @Nullable String roll_no) {
        FirebaseRef.getAuth()
                .createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseRef.getCurrentUser().sendEmailVerification();
                            if (roll_no.isEmpty())
                                dataSaveToFirebase(email, pass, f_name, l_name, dep, phone, null);
                            else
                                dataSaveToFirebase(email, pass, f_name, l_name, dep, phone, roll_no);

                        } else {
                            ProgressDialog.DismissProgressDialog();
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void dataSaveToFirebase(String email, String pass, String f_name, String l_name, String dep, String phone, @Nullable String roll_no) {
        String userId = FirebaseRef.getUserId();

        Map<String, Object> map = new HashMap<>();

        map.put("u_id", userId);
        map.put("email", email);
        map.put("password", pass);
        map.put("profile_img", ApplicationUtils.DEFAULT_IMAGE);
        map.put("first_name", f_name);
        map.put("last_name", l_name);
        map.put("department", dep);
        map.put("type", selectedType);
        if (selectedType.equals(getString(R.string.student).toLowerCase().trim()))
            map.put("roll_no", roll_no);

        map.put("phone", binding.spCountryCode.getSelectedCountryCodeWithPlus() + phone);


        FirebaseRef.getUserRef()
                .child(userId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ProgressDialog.DismissProgressDialog();
                            ApplicationUtils.hideKeyboard(mContext);
                            finish();
                            Toast.makeText(mContext, "user created successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            ProgressDialog.DismissProgressDialog();
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void updateRollNo() {

        if (isSession) {
            binding.fullRollNo.setVisibility(View.VISIBLE);
            binding.fullRollNo.setText(selectedSession);
            if (isYear) {
                binding.fullRollNo.setText(selectedSession + "-" + selectedYear);
                if (isProgram) {
                    binding.fullRollNo.setText(selectedSession + "-" + selectedYear + "/" + selectedProgram);
                    if (isDepartment) {
                        binding.fullRollNo.setText(selectedSession + "-" + selectedYear + "/" + selectedProgram + selectedDepartment + "/" + binding.rollNo.getText().toString().trim());
                        isRollNo = true;
                    }
                }
            }
        } else {
            isRollNo = false;
            binding.fullRollNo.setVisibility(View.GONE);
        }
    }

    private void updateButtonState() {
        if (selectedType.equals(getString(R.string.student).toLowerCase().trim()))
            binding.signupBtn.setEnabled(isFirstName && isLastName && isEmail && isPhoneNo && isRollNo && isPassword && isConfirmPassword);
        else if (selectedType.equals(getString(R.string.staff).toLowerCase().trim()))
            binding.signupBtn.setEnabled(isFirstName && isLastName && isEmail && isPhoneNo && isPassword && isProgram && isDepartment && isConfirmPassword);

        if (binding.signupBtn.isEnabled()) {
            binding.signupBtn.setBackground(getResources().getDrawable(R.drawable.active_background));
        } else {
            binding.signupBtn.setBackground(getResources().getDrawable(R.drawable.disable_background));
        }
    }

}