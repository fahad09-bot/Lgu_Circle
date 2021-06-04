package com.codesses.lgucircle.Fragments.Authority;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.IdeaAdapter;
import com.codesses.lgucircle.Interfaces.IdeaClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.FragmentIdeaStatusBinding;
import com.codesses.lgucircle.databinding.FragmentIdeasBinding;
import com.codesses.lgucircle.model.Idea;
import com.codesses.lgucircle.model.User;
import com.codesses.lgucircle.smtp.Smtp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class IdeaStatusFragment extends Fragment {

    FragmentIdeaStatusBinding binding;
    FragmentActivity fragmentActivity;

    List<Idea> ideaList = new ArrayList<>();

    IdeaAdapter ideaAdapter;

    int status;

    public IdeaStatusFragment(int position) {
        status = position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIdeaStatusBinding.bind(inflater.inflate(R.layout.fragment_idea_status, container, false));
        initAdapter();
        getIdeas();
        return binding.getRoot();
    }

    private void initAdapter() {
        ideaAdapter = new IdeaAdapter(fragmentActivity, ideaList, new IdeaClick() {
            @Override
            public void onAccept(String id, int position) {
                showDialog(id, position);
            }

            @Override
            public void onReject(String id, int position) {
                updateStatus(id, position, 2, 0);
            }
        });
        binding.ideaRecycler.setAdapter(ideaAdapter);
    }

    private void updateStatus(String id, int position, int status, int type) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        String i_id = ideaList.get(position).getI_id();
        FirebaseRef.getIdeaRef()
                .child(i_id)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (status == 3)
                            if (type == 2)
                                sendAcceptEmail(id);
                            else if (type == 1)
                                sendManualMail(id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(fragmentActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showDialog(String id, int position) {
        String message = "";
        message = "Are you sure you want to accept this idea?";

        new AlertDialog.Builder(fragmentActivity)
                .setTitle("Confirmation")
                .setCancelable(false)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        showEmailDialog(id, position);
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();

    }

    private void showEmailDialog(String id, int position) {
        new AlertDialog.Builder(fragmentActivity)
                .setTitle("Confirmation")
                .setCancelable(false)
                .setMessage("Please select type of email you want to send.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.manual, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        updateStatus(id, position, 3, 1);
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string._default, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateStatus(id, position, 3, 2);
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void sendManualMail(String id) {
        FirebaseRef.getUserRef()
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("IntentReset")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
                        email.putExtra(Intent.EXTRA_SUBJECT, "Idea Accepted");
                        email.setType("message/rfc822");
                        startActivity(email);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    private void sendAcceptEmail(String id) {
        FirebaseRef.getUserRef()
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Smtp smtp = new Smtp(fragmentActivity, user.getEmail(), "Idea Accepted", "Hi " + user.getFirst_name() + "\n" + fragmentActivity.getString(R.string.accept_message));
                        String message = "";
                        try {
                            message = smtp.execute().get();
                            Toast.makeText(fragmentActivity, message, Toast.LENGTH_SHORT).show();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void getIdeas() {
        FirebaseRef.getIdeaRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ideaList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Idea idea = dataSnapshot.getValue(Idea.class);
                    if (idea.getStatus() == status)
                        ideaList.add(idea);
                }
                initAdapter();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}