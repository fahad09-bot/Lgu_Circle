package com.codesses.lgucircle.Interfaces;

import android.net.Uri;

public interface SendData {
    void onSendData(Uri selectedImage);
    void onSendData(String message, Uri image);
}
