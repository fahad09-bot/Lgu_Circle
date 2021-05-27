package com.codesses.lgucircle.activity.Services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.ServicesAdapter;
import com.codesses.lgucircle.Adapters.UserPostAdapter;
import com.codesses.lgucircle.Dialogs.ProgressDialog;
import com.codesses.lgucircle.Interfaces.OnItemClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ActivityServicesBinding;
import com.codesses.lgucircle.databinding.AddServiceBinding;
import com.codesses.lgucircle.model.Post;
import com.codesses.lgucircle.model.Service;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServicesActivity extends AppCompatActivity {

    AppCompatActivity mContext;
    ActivityServicesBinding binding;
    LinkedList<Service> servicesList = new LinkedList<>();
    ServicesAdapter servicesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_services);
        binding.addService.setOnClickListener(this::addService);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        binding.servicesRecycler.setLayoutManager(linearLayoutManager);

        setSupportActionBar(binding.toolbar);
        setTitle(R.string.services);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        servicesAdapter = new ServicesAdapter(mContext, servicesList, id -> {
            Intent intent = new Intent(mContext, ServicesChatAC.class);
            intent.putExtra(Constants.USER_ID, id);
            startActivity(intent);
        });
        binding.servicesRecycler.setAdapter(servicesAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteService(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.servicesRecycler);

        getServices();
    }

    private void deleteService(int position) {
        FirebaseRef
                .getServiceRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Service service = dataSnapshot.getValue(Service.class);
                            assert service != null;
                            if (service.getS_id().equals(servicesList.get(position).getS_id())) {
                                if (service.getPosted_by().equals(FirebaseRef.getCurrentUserId()))
                                    dataSnapshot.getRef().removeValue();
                                else
                                    Toast.makeText(mContext, "You cannot delete this post", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.e("OnCancelled", error.getMessage());
                    }
                });
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
                        servicesAdapter.notifyDataSetChanged();


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
                mapService.put("description", description);
                String link = dialogBinding.serviceLinks.getText().toString();
                if (TextUtils.isEmpty(link))
                    mapService.put("Url", "");
                else if (!Patterns.WEB_URL.matcher(link).matches()) {
                    dialogBinding.serviceLinks.setError("Please Enter Valid Email");
                    return;
                } else
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
}