package com.shid.swissaid.UI.ui.draft;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shid.swissaid.Adapters.DraftListAdapter;
import com.shid.swissaid.Model.Draft;
import com.shid.swissaid.R;

import java.util.List;


public class DraftFragment extends Fragment {

    private DraftViewModel mViewModel;

    private DraftListAdapter adapter;




    public static DraftFragment newInstance() {
        return new DraftFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.draft_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DraftViewModel.class);
        // TODO: Use the ViewModel
    }

    private void observeSetup() {
        mViewModel.getAllDrafts().observe(this, new Observer<List<Draft>>() {
            @Override
            public void onChanged(@Nullable final List<Draft> drafts) {
                adapter.setDraftList(drafts);
            }
        });
    }

    private void recyclerSetup() {

        RecyclerView recyclerView;

        adapter = new DraftListAdapter(R.layout.draft_list_item);
        recyclerView = getView().findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

}
