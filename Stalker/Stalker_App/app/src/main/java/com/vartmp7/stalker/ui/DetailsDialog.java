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

package com.vartmp7.stalker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.vartmp7.stalker.R;
import com.vartmp7.stalker.datamodel.Organization;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsDialog {
    private Context context;
    private Organization org;

    public DetailsDialog(Context context, Organization org) {
        this.context = context;
        this.org = org;
    }

    public void showOrganizationDetails() {
        AlertDialog.Builder detailDialog = new AlertDialog.Builder(context, R.style.StalkerDetaislDialog);
        detailDialog.setIcon(R.drawable.ic_info_black_24dp);
        detailDialog.setTitle(org.getName());

        View v = LayoutInflater.from(context).inflate(R.layout.detail_view, null);
        CircleImageView iv = v.findViewById(R.id.ivIconOrganizzazione);
        Glide.with(context).load(org.getImageUrl()).into(iv);

        if (org.getType().equals("public"))
            v.findViewById(R.id.llLDAP).setVisibility(View.GONE);

        TextView tvName = v.findViewById(R.id.tvName);
        tvName.setText(org.getName());
        TextView tvIndirizzo = v.findViewById(R.id.tvIndirizzo);
        tvIndirizzo.setText(context.getString(
                R.string.template_indirizzo,
                org.getAddress(),
                org.getCity(),
                org.getPostalCode(),
                org.getRegion(),
                org.getNation()));
        TextView tvNum = v.findViewById(R.id.tvNumeroTelefonico);
        tvNum.setText(org.getPhoneNumber());
        TextView tvTipo = v.findViewById(R.id.tvTipoOrganizzazione);
        tvTipo.setText(org.getType());
        TextView tvEmail = v.findViewById(R.id.tvEmail);
        tvEmail.setText(org.getEmail());
        TextView tvInfo = v.findViewById(R.id.tvInfoLDAP);
        tvInfo.setText(context.getString(R.string.template_ldap,
                org.getLdapUrl(),
                org.getLdapPort(),
                org.getLdapDomainComponent(),
                org.getLdapCommonName()));
        TextView tvLuoghi = v.findViewById(R.id.tvElencoLuoghi);
        tvLuoghi.setText(org.getPlacesInfo());
        detailDialog.setNegativeButton(context.getString(R.string.chiudi), (dialog, which) -> dialog.dismiss());

        detailDialog.setView(v);
        detailDialog.create().show();

    }

}
