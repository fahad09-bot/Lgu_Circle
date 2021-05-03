package com.codesses.lgucircle.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class FirebaseRef {

    public static FirebaseAuth getAuth() { return FirebaseAuth.getInstance(); }

    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public static String getCurrentUserId() {
        return getAuth().getCurrentUser().getUid();
    }

    public static String getUserEmail() {
        return getAuth().getCurrentUser().getEmail();
    }
    public static String getUserId() {
        return getAuth().getCurrentUser().getUid();
    }
    //    TODO: Firebase Database
    public static DatabaseReference getDatabaseInstance() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getUserRef() {
        return getDatabaseInstance().child("Users");
    }

    //    TODO: Get Posts Reference
    public static DatabaseReference getPostsRef() {
        return getDatabaseInstance().child("posts");
    }

    //    TODO: Get Opinions Reference
    public static DatabaseReference getCommentRef() {
        return getDatabaseInstance().child("comments");
    }
//    public static DatabaseReference getDemoRef() {
//        return getDatabaseInstance().child("Demo");
//    }

    /****************************
     *   Firebase Storage
     * @return
     */

    //   TODO: Get Storage Instance
    public static StorageReference getStorageInstance() {
        return FirebaseStorage.getInstance().getReference();
    }


    //    TODO: Get Post Storage Reference
    public static StorageReference getPostStorage() {
        return getStorageInstance().child("Posts/").child("Images/" + UUID.randomUUID().toString());
    }

}


