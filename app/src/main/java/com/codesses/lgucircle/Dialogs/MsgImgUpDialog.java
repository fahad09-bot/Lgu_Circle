package com.codesses.lgucircle.Dialogs;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.codesses.lgucircle.Interfaces.SendData;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.FragmentMsgImgUpDialogBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;

import butterknife.ButterKnife;


public class MsgImgUpDialog extends DialogFragment {

    private SendData sendData;
    FragmentMsgImgUpDialogBinding binding;
    FragmentActivity fragmentActivity;
    String userImage;
    Uri imageUrl;
    String imageMessage;
    Dialog dialog;

    public MsgImgUpDialog(FragmentActivity fragmentActivity, String userImage, Uri imageUri, SendData sendData) {
        // Required empty public constructor
        this.fragmentActivity = fragmentActivity;
        this.userImage = userImage;
        this.imageUrl = imageUri;
        this.sendData = sendData;
    }


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);

    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        dialog = super.onCreateDialog(savedInstanceState);
//
//        return dialog;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMsgImgUpDialogBinding.bind(fragmentActivity.getLayoutInflater().inflate(R.layout.fragment_msg_img_up_dialog, container, false));

        binding.sendMessage.setOnClickListener(this::sendMessage);
        Picasso.get().load(imageUrl).into(binding.image);
        Picasso.get().load(userImage).into(binding.userImage);

        return binding.getRoot();
    }

    private void sendMessage(View view) {
        sendData.onSendData(binding.message.getText().toString(), imageUrl);
        dismiss();
    }

//    public void show(FragmentTransaction ft, String s) {
//        show(ft, s);
//    }
}