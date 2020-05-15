/*
 * MIT License
 *
 * Copyright (c) 2020 VarTmp7
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vartmp7.stalker.ui.organizations;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vartmp7.stalker.R;
import com.vartmp7.stalker.StalkerApplication;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.repository.OrganizationsRepository;

public class OrganizationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private OrganizationsViewModel organizzazioneViewModel;
    private RecyclerView recyclerView;
    private OrganizationViewAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String FIRST_LOG="first_log";



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_organizations, container, false);

        StalkerApplication application = (StalkerApplication) getActivity().getApplication();
        OrganizationsRepository organizationsRepository = application.getOrganizationsRepositoryComponent().organizationsRepository();

        organizzazioneViewModel = new ViewModelProvider(requireActivity(),
                new OrganizationsViewModel.OrganizationViewModelFactory(organizationsRepository)).get(OrganizationsViewModel.class);


        swipeRefreshLayout = root.findViewById(R.id.srfl);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK, Color.GREEN, Color.MAGENTA);
        recyclerView = root.findViewById(R.id.rvListaOrganizzazioni);

        setUpRecyclerView();
        organizzazioneViewModel.getOrganizationList().observe(getViewLifecycleOwner(), lista -> {
            mAdapter.setOrganizations(lista);
            swipeRefreshLayout.setRefreshing(false);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Organization o = mAdapter.getOrganizationAt(viewHolder.getAdapterPosition());
                organizzazioneViewModel.addOrganizationToTrack(o);
                mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(recyclerView);


        return root;
    }


    private void setUpRecyclerView() {
        mAdapter = new OrganizationViewAdapter(getContext(), organizzazioneViewModel,
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        organizzazioneViewModel.refresh();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        organizzazioneViewModel.refresh();
    }

}
