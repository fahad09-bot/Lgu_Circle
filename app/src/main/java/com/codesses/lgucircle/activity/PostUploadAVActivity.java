package com.codesses.lgucircle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codesses.lgucircle.Dialogs.ProgressDialog;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class PostUploadAVActivity extends AppCompatActivity {
    //    TODO: Widgets
    @BindView(R.id.back_press)
    ImageView Back_Press;
    @BindView(R.id.profile_img)
    ImageView Profile_Img;
    @BindView(R.id.post_upload)
    Button Post_Upload;
    @BindView(R.id.post_text)
    EditText Post_Text;
    @BindView(R.id.add_image)
    Button Add_image;
    @BindView(R.id.post_image)
    ImageView Post_Image;

    //    TODO: Variables
    private String status = "",
            imageUri = "",
            stringImageUri = "",
            video = "";
    private Uri selectedImg;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload_a_v);
        ButterKnife.bind(this);

        //   TODO: Get Current User
        getCurrentUserData();



        //        TODO: Click Listeners
        Back_Press.setOnClickListener(this::backPress);
        Post_Upload.setOnClickListener(this::getPostData);
        Add_image.setOnClickListener(this::galleryPermission);
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ApplicationUtils.IMAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryImagePick();
                } else {
                    Toast.makeText(PostUploadAVActivity.this, getString(R.string.per_denied), Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ApplicationUtils.PICK_IMAGE_CODE && data != null) {

            Bundle extras = data.getExtras();

            selectedImg = data.getData();
            stringImageUri = selectedImg.toString();
            Post_Image.setImageURI(selectedImg);

//            TODO: Compress Profile Image
            if (extras != null) {

//                LOGO UPLOAD TO STORAGE
//                ImageUploadToStorage(CompressImage.getCompressImg(extras, "data"));
            }

        }
    }


    /***********************************
     * Methods Call In Current Activity
     */

    private void backPress(View view) {
        onBackPressed();
    }

    private void getCurrentUserData() {
        FirebaseRef.getUserRef()
                .child(FirebaseRef.getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User model = dataSnapshot.getValue(User.class);


//                        Picasso.get().load(model.getProfile_img()).into(Profile_Img);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(PostUploadAVActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void galleryPermission(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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

        startActivityForResult(intent, ApplicationUtils.PICK_IMAGE_CODE);

    }

    private void getPostData(View view) {
        status = Post_Text.getText().toString().trim();
        if (TextUtils.isEmpty(status) && TextUtils.isEmpty(stringImageUri)) {
            Toast.makeText(this, "Empty Post Not Allowed", Toast.LENGTH_SHORT).show();
            return;
        } else if (!TextUtils.isEmpty(status) && TextUtils.isEmpty(stringImageUri)) {
            postUpload();
            type = 0;
        } else if (TextUtils.isEmpty(status) && !TextUtils.isEmpty(stringImageUri)) {
            imageUploadToStorage();
            type = 1;
        } else if (!TextUtils.isEmpty(status) && !TextUtils.isEmpty(stringImageUri)) {
            imageUploadToStorage();
            type = 2;
        }

        //            TODO: Show Progress Dialog
        ProgressDialog.ShowProgressDialog(PostUploadAVActivity.this,
                R.string.uploading,
                R.string.please_wait);


    }

    private void imageUploadToStorage() {
        StorageReference reference = FirebaseRef.getPostStorage();
        UploadTask uploadTask = reference.putFile(selectedImg);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

//                Toast.makeText(PostUploadAV.this, "", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            imageUri = task.getResult().toString();
                            postUpload();

                        } else {
                            Toast.makeText(PostUploadAVActivity.this, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ProgressDialog.DismissProgressDialog();
                Toast.makeText(PostUploadAVActivity.this, "Warning: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postUpload() {

        String postId = FirebaseRef.getPostsRef().push().getKey();

        Map<String, Object> map = new HashMap<>();

        map.put("p_id", postId);
        map.put("posted_by", FirebaseRef.getUserId());
        map.put("posted_by_role", getString(R.string.user_role));
        map.put("status", status);
        map.put("image", imageUri);
        map.put("video", video);
        map.put("type", type);
        map.put("timestamp", System.currentTimeMillis());

        FirebaseRef.getPostsRef()
                .child(postId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PostUploadAVActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                            ProgressDialog.DismissProgressDialog();
//                            onBackPressed();
                        } else {
                            ProgressDialog.DismissProgressDialog();

                            String error = task.getException().getMessage();
                            Toast.makeText(PostUploadAVActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}