package com.codesses.lgucircle.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.codesses.lgucircle.Interfaces.OnForgotPass;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.CheckEmptyFields;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPassDialog extends AppCompatDialogFragment {

    @BindView(R.id.forgot_email)
    EditText Forgot_Pass;
    @BindView(R.id.reset_btn)
    Button Reset_Btn;

    //    TODO: Interface
    OnForgotPass onForgotPass;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.forgot_pass_layout, null);
        ButterKnife.bind(this, view);


        builder.setView(view)
                .setTitle(getString(R.string.reset_pass));
        Reset_Btn.setOnClickListener(this::sendEmail);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onForgotPass = (OnForgotPass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    private void sendEmail(View view) {
        String email = Forgot_Pass.getText().toString().trim();

        if (CheckEmptyFields.isEditText(getActivity(), email, Forgot_Pass)) {

            onForgotPass.onApply(email);
            dismiss();
        }

    }
}
