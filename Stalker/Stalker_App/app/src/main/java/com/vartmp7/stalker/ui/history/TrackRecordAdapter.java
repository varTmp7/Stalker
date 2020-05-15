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

package com.vartmp7.stalker.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vartmp7.stalker.R;
import com.vartmp7.stalker.datamodel.TrackRecord;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TrackRecordAdapter extends RecyclerView.Adapter<TrackRecordAdapter.ViewHolder> {

    private List<TrackRecord> records;

    TrackRecordAdapter(List<TrackRecord> records) {
        this.records = records;
    }

    void updateTracks(@NotNull List<TrackRecord> records){
        records.forEach(trackRecord -> trackRecord.setDateTime(trackRecord.getDateTime().replace("T"," ")));
        this.records=records.stream().sorted(
                (o1, o2) -> Comparator.comparing(TrackRecord::getDateTime).compare(o1,o2)*-1)
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_record, parent, false);
        return new TrackRecordAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrackRecord record = records.get(position);
        holder.tvData.setText(record.getDateTime());
        holder.tvNomeLuogo.setText(record.getPlaceName());

        if (record.isEntered()) {
            holder.ivIcon.setImageResource(R.drawable.ic_enter);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_exit);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class  ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNomeLuogo;
        private TextView tvData;
        private ImageView ivIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeLuogo = itemView.findViewById(R.id.tvOrganizationInfo);
            tvData = itemView.findViewById(R.id.tvTime);
            ivIcon = itemView.findViewById(R.id.ivInOut);
        }
    }
}
