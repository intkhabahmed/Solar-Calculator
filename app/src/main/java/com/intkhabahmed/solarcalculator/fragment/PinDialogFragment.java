package com.intkhabahmed.solarcalculator.fragment;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.intkhabahmed.solarcalculator.R;
import com.intkhabahmed.solarcalculator.adapter.PinAdapter;
import com.intkhabahmed.solarcalculator.database.PinDatabase;
import com.intkhabahmed.solarcalculator.model.PinInfo;
import com.intkhabahmed.solarcalculator.ui.MapsActivity;

import java.util.List;

public class PinDialogFragment extends DialogFragment implements PinAdapter.OnItemClickListener{

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
        return inflater.inflate(R.layout.pins_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView emptyView = view.findViewById(R.id.empty_view);
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
                if (pinInfos != null) {
                    adapter.setPins(pinInfos);
                    emptyView.setVisibility(View.INVISIBLE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onItemClick(PinInfo pinInfo) {
        this.dismiss();
        ((MapsActivity)getParentActivity()).setLatLongAndUpdate(new LatLng(pinInfo.getLatitude(), pinInfo.getLongitude()));
    }

    private FragmentActivity getParentActivity() {
        return getActivity();
    }
}
