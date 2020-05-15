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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vartmp7.stalker.R;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.ui.DetailsDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class OrganizationViewAdapter extends RecyclerView.Adapter<OrganizationViewAdapter.ViewHolder> {
    private List<Organization> listaOrganization;
    private Context context;
    private NavController navController;
    private OrganizationsViewModel viewModel;

    public OrganizationViewAdapter(Context context, OrganizationsViewModel organizationsViewModel, NavController controller) {
        this.context = context;
        this.viewModel = organizationsViewModel;
        this.navController = controller;
        listaOrganization = new ArrayList<>();
    }

    public void setOrganizations(List<Organization> newData) {
        this.listaOrganization = newData;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organization_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Organization org = listaOrganization.get(position);

        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions().error(R.drawable.logo_unipd))
                .load(listaOrganization.get(position).getImageUrl())
                .into(holder.ivIconOrganizzazione);

        holder.nomeOrganizzazione.setText(org.getName());
        holder.btnTrackMe.setOnClickListener(v -> {
            viewModel.addOrganizationToTrack(org);
            navController.navigate(R.id.action_navigation_organizations_to_navigation_tracking);
        });

        holder.btnShowDetails.setOnClickListener(v -> {
            new DetailsDialog(context,org).showOrganizationDetails();
        });

    }



    @Override
    public int getItemCount() {
        return listaOrganization.size();
    }

    Organization getOrganizationAt(int adapterPosition) {
        return listaOrganization.get(adapterPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeOrganizzazione;

        Button btnTrackMe, btnShowDetails;

        ImageView ivIconOrganizzazione;

        ViewHolder(@NonNull View itemView) {
            // dati dell'organizzazione
            super(itemView);
            ivIconOrganizzazione = itemView.findViewById(R.id.ivIconOrganizzazione);
            nomeOrganizzazione = itemView.findViewById(R.id.tvNomeOrganizzazione);
            btnTrackMe = itemView.findViewById(R.id.btnTrackMe);
            btnShowDetails = itemView.findViewById(R.id.btnShowDetails);

        }
    }


}
