package com.intkhabahmed.solarcalculator.fragment;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.intkhabahmed.solarcalculator.R;
import com.intkhabahmed.solarcalculator.adapter.PinAdapter;
import com.intkhabahmed.solarcalculator.database.PinDatabase;
import com.intkhabahmed.solarcalculator.databinding.PinsFragmentBinding;
import com.intkhabahmed.solarcalculator.model.PinInfo;
import com.intkhabahmed.solarcalculator.ui.MapsActivity;
import com.intkhabahmed.solarcalculator.util.AppExecutors;

import java.util.List;

public class PinDialogFragment extends DialogFragment implements PinAdapter.OnItemClickListener {

    private PinsFragmentBinding mPinBinding;

    public PinDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPinBinding = DataBindingUtil.inflate(inflater, R.layout.pins_fragment, container, false);
        return mPinBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.pin_rv);
        final PinAdapter adapter = new PinAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getParentActivity(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        PinDatabase.getInstance(getContext()).pinDao().getAllPins().observe(this, new Observer<List<PinInfo>>() {
            @Override
            public void onChanged(@Nullable List<PinInfo> pinInfos) {
                if (pinInfos != null && pinInfos.size() > 0) {
                    adapter.setPins(pinInfos);
                    mPinBinding.emptyView.setVisibility(View.INVISIBLE);
                } else {
                    adapter.setPins(null);
                    mPinBinding.emptyView.setVisibility(View.VISIBLE);
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int id = (int) viewHolder.itemView.getTag();
                PinDatabase.getInstance(getParentActivity()).pinDao().getPinById(id).observe(PinDialogFragment.this, new Observer<PinInfo>() {
                    @Override
                    public void onChanged(@Nullable final PinInfo pinInfo) {
                        if (pinInfo != null) {
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    PinDatabase.getInstance(getParentActivity()).pinDao().deletePin(pinInfo);
                                    getParentActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getParentActivity(), getString(R.string.pin_deleted), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onItemClick(PinInfo pinInfo) {
        this.dismiss();
        ((MapsActivity) getParentActivity()).setLatLongAndUpdate(new LatLng(pinInfo.getLatitude(), pinInfo.getLongitude()));
    }

    private FragmentActivity getParentActivity() {
        return getActivity();
    }
}
