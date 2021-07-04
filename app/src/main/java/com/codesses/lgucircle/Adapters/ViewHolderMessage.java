package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnImageClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.activity.Services.ConversationAC;
import com.codesses.lgucircle.activity.Services.ServicesChatAC;
import com.codesses.lgucircle.model.Chat;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Locale;

public class ViewHolderMessage extends RecyclerView.ViewHolder {

    TextView showMessage;
    ImageView seenImage;
    TextView timeStamp;
    ImageView messageImage;
    CardView image_con;
    int viewType;
    ProgressBar progressBar;
    ConstraintLayout constraintLayout;


    public ViewHolderMessage(@NonNull @NotNull View itemView, int viewType) {
        super(itemView);

        this.viewType = viewType;
        showMessage = itemView.findViewById(R.id.show_message);
        seenImage = itemView.findViewById(R.id.message_status);
        timeStamp = itemView.findViewById(R.id.timestamp);
        messageImage = itemView.findViewById(R.id.message_image);
        image_con = itemView.findViewById(R.id.image_con);
        progressBar = itemView.findViewById(R.id.progressBar);
        constraintLayout = itemView.findViewById(R.id.parent);


    }

    public void onBind(Chat chat, OnImageClick onImageClick, Context mContext) {
        if (viewType == 0)
            seenImage.setVisibility(View.GONE);
        else if (viewType == 1) {
            seenImage.setVisibility(View.GONE);
//            if (chat.isSeen().equals("delivered"))
//                seenImage.setImageResource(R.drawable.ic_done);
//            else if (chat.isSeen().equals("received"))
//                seenImage.setImageResource(R.drawable.ic_rceived);
//            else if (chat.isSeen().equals("read"))
//                seenImage.setImageResource(R.drawable.ic_done_all);
        }

        if (chat.getType().equals(0)) {
            showMessage.setVisibility(View.VISIBLE);
            showMessage.setText(chat.getMessage());
            image_con.setVisibility(View.GONE);
        } else if (chat.getType().equals(1)) {
            showMessage.setVisibility(View.GONE);
            image_con.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            Picasso
                    .get()
                    .load(chat.getMessageImage())
                    .centerCrop()
                    .resize(500, 200)
                    .into(messageImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

        } else if (chat.getType().equals(2)) {
            image_con.setVisibility(View.VISIBLE);
            showMessage.setVisibility(View.VISIBLE);
            showMessage.setText(chat.getMessage());
            progressBar.setVisibility(View.VISIBLE);
            Picasso
                    .get()
                    .load(chat.getMessageImage())
                    .centerCrop()
                    .resize(500, 200)
                    .into(messageImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        try {
            cal.setTimeInMillis(Long.parseLong(chat.getTimestamp()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//
        if (!chat.isPicked())
            constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        else
            constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        String dateTime = DateFormat.format("hh:mm ", cal).toString();
        timeStamp.setText(dateTime);

        messageImage
                .setOnClickListener(v -> onImageClick
                        .onImageClick(chat.getMessageImage()));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.isMessagePicked) {
                    if (chat.isPicked()) {
                        constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        chat.setPicked(false);
                        Constants.selectedMessages -= 1;
                        if (Constants.selectedMessages.equals(0)) {
                            ServicesChatAC.binding.btnDelete.setVisibility(View.GONE);
                            ServicesChatAC.binding.send.setEnabled(true);
                            ServicesChatAC.binding.attach.setEnabled(true);
                            ServicesChatAC.binding.message.setEnabled(true);
                            Constants.isMessagePicked = false;
                        }
                    } else {
                        chat.setPicked(true);
                        constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                        Constants.selectedMessages += 1;
                    }

                }
            }
        });
    }
}
