package com.codesses.lgucircle.activity.Services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.ServiceAdapter;
import com.codesses.lgucircle.Dialogs.ProgressDialog;
import com.codesses.lgucircle.Interfaces.OnItemClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.CheckEmptyFields;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ActivityServicesBinding;
import com.codesses.lgucircle.databinding.AddServiceBinding;
import com.codesses.lgucircle.model.Service;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ServicesActivity extends AppCompatActivity {

    AppCompatActivity mContext;
    ActivityServicesBinding binding;
    LinkedList<Service> servicesList = new LinkedList<>();
    ServiceAdapter serviceAdapter;
    boolean isUrl = false;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_services);
        binding.addService.setOnClickListener(this::addService);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        binding.servicesRecycler.setLayoutManager(linearLayoutManager);

        serviceAdapter = new ServiceAdapter(mContext, servicesList, new OnItemClick() {
            @Override
            public void onClick(String id) {
                Intent intent = new Intent(mContext, ServicesChatAC.class);
                intent.putExtra(Constants.USER_ID, id);
                startActivity(intent);
            }

            @Override
            public void onMenuClick(View view, String id) {
                showMenu(view, id);
            }
        });

        binding.servicesRecycler.setAdapter(serviceAdapter);


        getServices();
    }


    private void getServices() {
        FirebaseRef.getServiceRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        servicesList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Service model = dataSnapshot.getValue(Service.class);
                            servicesList.addFirst(model);
                        }
                        serviceAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    private void addService(View view) {
        AddServiceBinding dialogBinding = DataBindingUtil.bind(LayoutInflater.from(mContext).inflate(R.layout.add_service, null, false));
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(dialogBinding.getRoot());
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.setCancelable(false);
        setTextChangeListener(dialogBinding);

        dialogBinding.serviceUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //            TODO: Show Progress Dialog
                ProgressDialog.ShowProgressDialog(mContext,
                        R.string.uploading,
                        R.string.please_wait);
                Map<String, Object> mapService = new HashMap<>();
                String serviceId = FirebaseRef.getPostsRef().push().getKey();
                mapService.put("s_id", serviceId);
                String userId = FirebaseRef.getCurrentUserId();
                mapService.put("posted_by", userId);
                String description = dialogBinding.serviceDescription.getText().toString();
                if (CheckEmptyFields.isEditText(mContext, description, dialogBinding.serviceDescription))
                    mapService.put("description", description);
                else {
                    ProgressDialog.DismissProgressDialog();
                    return;
                }

                String link = dialogBinding.serviceLinks.getText().toString();
                if (!isUrl)
                    mapService.put("Url", "");
                else if (isUrl)
                    mapService.put("Url", link);


                mapService.put("time_stamp", System.currentTimeMillis());

                FirebaseRef.getServiceRef()
                        .child(serviceId)
                        .updateChildren(mapService)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Service Uploaded", Toast.LENGTH_SHORT).show();
                                ProgressDialog.DismissProgressDialog();
                                dialog.dismiss();
//                            onBackPressed();
                            } else {
                                ProgressDialog.DismissProgressDialog();
                                String error = task.getException().getMessage();
                                Toast.makeText(mContext, "Error: " + error, Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        dialog.show();
    }

    private void setTextChangeListener(AddServiceBinding dialogBinding) {
        dialogBinding.serviceLinks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validUrl(s.toString(), dialogBinding);
            }
        });
    }

    private void validUrl(String url, AddServiceBinding dialogBinding) {
        if (!TextUtils.isEmpty(url)) {
            if (ApplicationUtils.isUrlValid(url)) {
                isUrl = true;
                dialogBinding.serviceLinks.setError(null);
            } else {
                isUrl = false;
                dialogBinding.serviceLinks.setError(mContext.getString(R.string.invalid_url_format));
            }

        } else {
            isUrl = false;
            dialogBinding.serviceLinks.setError(null);
        }

    }

    private void showMenu(View v, String id) {
        PopupMenu popup = new PopupMenu(mContext, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        FirebaseRef
                                .getServiceRef()
                                .child(id)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                }
                return false;
            }
        });
        popup.inflate(R.menu.post_menu);
        popup.show();
    }

}