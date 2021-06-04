package com.codesses.lgucircle.Fragments.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codesses.lgucircle.Adapters.ServiceAdapter;
import com.codesses.lgucircle.Interfaces.OnItemClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.FragmentUserServiceBinding;
import com.codesses.lgucircle.model.Service;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserServiceFragment} factory method to
 * create an instance of this fragment.
 */
public class UserServiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentActivity fragmentActivity;
    LinkedList<Service> servicesList = new LinkedList<>();

    ServiceAdapter serviceAdapter;
    FragmentUserServiceBinding binding;

    String userId;

    public UserServiceFragment(String userId) {
        // Required empty public constructor
        this.userId = userId;
    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment UserServiceFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static UserServiceFragment newInstance(String param1, String param2) {
//        UserServiceFragment fragment = new UserServiceFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentActivity = getActivity();
        binding = FragmentUserServiceBinding.bind(inflater.inflate(R.layout.fragment_user_service, container, false));
        getServicesData();

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


        return binding.getRoot();
    }

    private void deleteService(int position) {
        FirebaseRef
                .getServiceRef()
                .child(servicesList.get(position).getS_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Service service = dataSnapshot.getValue(Service.class);
                            assert service != null;
                            if (service.getS_id().equals(servicesList.get(position).getS_id()) && service.getPosted_by().equals(FirebaseRef.getUserId())) {
                                dataSnapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.e("OnCancelled", error.getMessage());
                    }
                });
    }


    private void getServicesData() {
        FirebaseRef.getServiceRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            servicesList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Service model = snapshot.getValue(Service.class);
                                if (model.getPosted_by().equals(FirebaseRef.getUserId())) {
                                    servicesList.addFirst(model);
                                }
                            }
                            Log.e("POSTSUSER", String.valueOf(servicesList.size()));

                            serviceAdapter = new ServiceAdapter(fragmentActivity, servicesList, new OnItemClick() {
                                @Override
                                public void onClick(String id) {

                                }
                            });
                            binding.servicesRecycler.setAdapter(serviceAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
