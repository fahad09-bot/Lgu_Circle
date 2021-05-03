package com.codesses.lgucircle.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.CheckEmptyFields;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePassDialog extends DialogFragment{

    @BindView(R.id.old_pass)
    EditText Old_Pass;
    @BindView(R.id.old_pass_hide)
    ImageView Old_Pass_Hide;
    @BindView(R.id.new_pass)
    EditText New_Pass;
    @BindView(R.id.new_pass_hide)
    ImageView New_Pass_Hide;
    @BindView(R.id.update_pass)
    Button Update_Pass;


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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.change_password, null);
        ButterKnife.bind(this, view);

        alertDialog.setView(view)
                .setTitle(getString(R.string.change_pass));


//        TODO: Click Listeners
        Old_Pass_Hide.setOnClickListener(this::oldPassVisibility);
        New_Pass_Hide.setOnClickListener(this::newPassVisibility);
        Update_Pass.setOnClickListener(this::updatePass);


        return alertDialog.show();
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
        String oldPass = Old_Pass.getText().toString().trim();
        String newPass = New_Pass.getText().toString().trim();

        if (CheckEmptyFields.isEditText(getActivity(), oldPass, Old_Pass) &&
                CheckEmptyFields.isEditText(getActivity(), newPass, New_Pass) &&
                CheckEmptyFields.isPassNotMatch(getActivity(), oldPass, newPass, New_Pass)) {

//            onChangePassClick.onClick(oldPass, newPass);
            dismiss();
        }

    }


    private void oldPassVisibility(View view) {
        if (isOldPassVisible) {
            isOldPassVisible = false;
            Old_Pass_Hide.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            Old_Pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            isOldPassVisible = true;
            Old_Pass_Hide.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            Old_Pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    private void newPassVisibility(View view) {
        if (isNewPassVisible) {
            isNewPassVisible = false;
            New_Pass_Hide.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            New_Pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {

            isNewPassVisible = true;
            New_Pass_Hide.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            New_Pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }


}


