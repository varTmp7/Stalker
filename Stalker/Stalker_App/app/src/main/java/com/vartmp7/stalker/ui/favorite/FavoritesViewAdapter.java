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

package com.vartmp7.stalker.ui.favorite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vartmp7.stalker.R;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.ui.DetailsDialog;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoritesViewAdapter extends RecyclerView.Adapter<FavoritesViewAdapter.ViewHolder> {
    private List<Organization> organizzazioni;
    private Context context;
    private FavoritesViewModel viewModel;
    private  NavController navController;

    public void setOrganizzazioni(List<Organization> organizzazioni) {
        this.organizzazioni = organizzazioni;
        notifyDataSetChanged();
    }

    FavoritesViewAdapter(Context context, FavoritesViewModel model, List<Organization> organizzazioni, NavController controller) {
        this.context = context;
        this.organizzazioni = organizzazioni;
        this.viewModel = model;
        this.navController = controller;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Organization org = organizzazioni.get(position);
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions().error(R.drawable.icon_stalker))
                .load(organizzazioni.get(position).getImageUrl())
                .into(holder.civIconaOrganizzazione);

        holder.tvNomeOrganizzazione.setText(org.getName());

        holder.btnAddToTracking.setOnClickListener(v -> {
            if (org.isTracking()) {
                Toast.makeText(context, "Organizzazione già presente nell'elenco dei tracking!", Toast.LENGTH_SHORT).show();
            } else {
                org.setTracking(true);
                viewModel.updateOrganizzazione(org);
                navController.navigate(R.id.action_navigation_preferiti_to_navigation_tracking);
            }

        });
        holder.ivDetails.setOnClickListener(v -> new DetailsDialog(context, org).showOrganizationDetails());

    }


    @Override
    public int getItemCount() {
        return organizzazioni.size();
    }

    Organization getOrganizationAt(int position) {
        return organizzazioni.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeOrganizzazione;
        CircleImageView civIconaOrganizzazione;
        Button btnAddToTracking;
        Button ivDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            civIconaOrganizzazione = itemView.findViewById(R.id.ivIconOrganizzazione);
            tvNomeOrganizzazione = itemView.findViewById(R.id.tvNomeOrganizzazione);
            btnAddToTracking = itemView.findViewById(R.id.btnTrackMe);
            ivDetails = itemView.findViewById(R.id.btnShowDetails);
        }
    }
}
