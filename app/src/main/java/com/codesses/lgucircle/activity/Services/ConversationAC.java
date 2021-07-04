package com.codesses.lgucircle.activity.Services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.ConversationAdapter;
import com.codesses.lgucircle.Dialogs.UserSearchDialog;
import com.codesses.lgucircle.Interfaces.OnConversationClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ActivityConversationBinding;
import com.codesses.lgucircle.model.ChatList;
import com.codesses.lgucircle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConversationAC extends AppCompatActivity {

    AppCompatActivity mContext;
    ActivityConversationBinding binding;
    List<User> userList = new ArrayList<>();
    List<ChatList> chatLists = new ArrayList<>();
    ConversationAdapter adapter;
    public static ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_conversation);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationAC.this.onBackPressed();
            }
        });
        binding.newConversation.setOnClickListener(this::createConversation);
        imageView = findViewById(R.id.btn_delete);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                deleteConversation(position);
//            }
//        });
//        itemTouchHelper.attachToRecyclerView(binding.conversationRecycler);

        getConversationList();

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.delete))
                        .setMessage("Delete " + Constants.selectedUsers + " selected chats?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteConversation();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .create()
                        .show();
            }
        });
    }

    private void setAdapter() {
        adapter = new ConversationAdapter(userList, mContext, new OnConversationClick() {
            @Override
            public void onClick(String userId) {
                Intent intent = new Intent(mContext, ServicesChatAC.class);
                intent.putExtra(Constants.USER_ID, userId);
                startActivity(intent);
            }

            @Override
            public void longClick() {
                binding.btnDelete.setVisibility(View.VISIBLE);

            }
        });
        binding.conversationRecycler.setAdapter(adapter);
    }

    //    TODO: Conversation List
    private void getConversationList() {
        FirebaseRef.getConversationRef()
                .child(FirebaseRef.getCurrentUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        chatLists.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatList chatlist = dataSnapshot.getValue(ChatList.class);
                            chatLists.add(chatlist);
                        }
                        getUsersList();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    private void getUsersList() {
        FirebaseRef.getUserRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            for (ChatList chatList : chatLists) {
                                if (user.getU_id().equals(chatList.getId())) {
                                    userList.add(user);
                                }
                            }

                        }
                        setAdapter();

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void createConversation(View view) {
        UserSearchDialog userSearchDialog = new UserSearchDialog("ConversationAC");
        userSearchDialog.show(mContext.getSupportFragmentManager(), "user search");
    }

    private void deleteConversation() {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).isPicked()) {
                FirebaseRef.getConversationRef()
                        .child(FirebaseRef.getCurrentUserId())
                        .child(userList.get(i).getU_id())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Successfully deleted", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

                FirebaseRef.getMessageRef()
                        .child(FirebaseRef.getCurrentUserId())
                        .child(FirebaseRef.getCurrentUserId() + userList.get(i).getU_id())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(mContext, "Successfully deleted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        Constants.isChatPicked = false;
        Constants.selectedUsers = -1;
        binding.btnDelete.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.isChatPicked = false;
        Constants.selectedUsers = -1;
    }
}