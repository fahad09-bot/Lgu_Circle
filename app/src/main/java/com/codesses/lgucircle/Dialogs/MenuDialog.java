package com.codesses.lgucircle.Dialogs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codesses.lgucircle.Interfaces.SendData;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.databinding.FragmentMenuDialogBinding;

import static android.app.Activity.RESULT_OK;


public class MenuDialog extends DialogFragment {

    FragmentActivity fragmentActivity;
    FragmentMenuDialogBinding binding;

    private Uri selectedImg;
    private int type;
    private String stringImageUri;
    SendData sendData;


    public MenuDialog(FragmentActivity fragmentActivity, SendData sendData) {
        // Required empty public constructor
        this.fragmentActivity = fragmentActivity;
        this.sendData = sendData;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMenuDialogBinding.bind(inflater.inflate(R.layout.fragment_menu_dialog, container,
                false));

        // Inflate the layout for this fragment

        binding.menuAttachmentGallery.setOnClickListener(this::galleryPermission);
        return binding.getRoot();
    }

    private void galleryPermission(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fragmentActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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

    private void galleryImagePick() {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_PICK);

        intent.setType("image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 100);
//        intent.putExtra("outputY", 100);
//        intent.putExtra("noFaceDetection", true);
//        intent.putExtra("return-data", true);
        startActivityForResult(intent, ApplicationUtils.PICK_IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ApplicationUtils.PICK_IMAGE_CODE && data != null) {

            selectedImg = data.getData();
            stringImageUri = selectedImg.toString();
            dismiss();
            sendData.onSendData(selectedImg);

        }
    }

}