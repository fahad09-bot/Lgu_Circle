package com.codesses.lgucircle.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.CheckEmptyFields;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import java.util.HashMap;
import java.util.Map;

public class ChangePassDialog extends DialogFragment {

    ChangePasswordBinding binding;

    FragmentActivity fragmentActivity;


    //    TODO: Variables
    private boolean isOldPassVisible = false,
            isNewPassVisible = false;


    //    TODO: Interface
//    OnChangePassClick onChangePassClick;


    //    TODO: Constructor
//    public ChangePassDialog(OnChangePassClick onChangePassClick) {
//        this.onChangePassClick = onChangePassClick;
//    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragmentActivity);
        binding = ChangePasswordBinding.bind(LayoutInflater.from(fragmentActivity).inflate(R.layout.change_password, null));

        alertDialog.setView(binding.getRoot())
                .setTitle(getString(R.string.change_pass));


//        TODO: Click Listeners
        binding.oldPassHide.setOnClickListener(this::oldPassVisibility);
        binding.newPass.setOnClickListener(this::newPassVisibility);
        binding.updatePass.setOnClickListener(this::updatePass);


        return alertDialog.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    //    @Override
//    public void onDetach() {
//        onChangePassClick = null;
//        super.onDetach();
//    }


    /*****************************************
     * Methods Call In Current Fragment Dialog
     */

    private void updatePass(View view) {
        String oldPass = binding.oldPass.getText().toString().trim();
        String newPass = binding.newPass.getText().toString().trim();

        if (CheckEmptyFields.isEditText(fragmentActivity, oldPass, binding.oldPass) &&
                CheckEmptyFields.isEditText(fragmentActivity, newPass, binding.newPass) &&
                CheckEmptyFields.isPassNotMatch(fragmentActivity, oldPass, newPass, binding.newPass)) {

//            onChangePassClick.onClick(oldPass, newPass);
            reAuthenticatePassword(oldPass, newPass);
            dismiss();
        }

    }

    private void reAuthenticatePassword(String oldPass, String newPass) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(FirebaseRef.getUserEmail(), oldPass);

        if (authCredential != null) {
            FirebaseRef.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        FirebaseRef.getCurrentUser().updatePassword(newPass).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                ChangePassDialog.this.passSaveInDatabase(newPass);
                            } else {
                                Toast.makeText(fragmentActivity, "Alert! " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(fragmentActivity, "Alert! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(fragmentActivity, "Alert! " + "Please enter correct old password", Toast.LENGTH_SHORT).show();
        }


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


    private void oldPassVisibility(View view) {
        if (isOldPassVisible) {
            isOldPassVisible = false;
            binding.oldPassHide.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.oldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            isOldPassVisible = true;
            binding.oldPassHide.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.oldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    private void newPassVisibility(View view) {
        if (isNewPassVisible) {
            isNewPassVisible = false;
            binding.newPassHide.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {

            isNewPassVisible = true;
            binding.newPassHide.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }


}


