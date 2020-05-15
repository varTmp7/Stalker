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

package com.vartmp7.stalker.ui.tracking;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.vartmp7.stalker.R;
import com.vartmp7.stalker.Tools;
import com.vartmp7.stalker.component.StalkerLDAP;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.ui.DetailsDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class TrackingViewAdapter extends RecyclerView.Adapter<TrackingViewAdapter.ViewHolder> {
    private List<Organization> listOrganization;
    private Context context;
    private TrackingViewModel viewModel;


    public TrackingViewAdapter(Context context, TrackingViewModel viewModel) {
        listOrganization = new ArrayList<>();
        this.context = context;
        this.viewModel = viewModel;
    }

    public void setList(List<Organization> org) {
        this.listOrganization = org;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tracking, parent, false);
        TrackingViewAdapter.ViewHolder viewHolder = new TrackingViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Size
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Organization org = listOrganization.get(position);
        holder.ibtnPreferito.setImageResource(org.isFavorite() ? R.drawable.icon_fav_si : R.drawable.icon_fav_no);

        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.btnStartTracking:
                case R.id.ibtnTrackingOn:
                    viewModel.updateOrganization(org.setTrackingActive(!org.isTrackingActive()));
                    notifyItemChanged(position);
                    break;
                case R.id.llTitle:
                    if (holder.llInformationToHide.getVisibility() == View.GONE) {
                        holder.llInformationToHide.setVisibility(View.VISIBLE);
                    } else {
                        holder.llInformationToHide.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btnLoginLDAP:

                    if (!org.isLogged()) {
                        showLDAPLoginDialog((Button) v, holder.sAnonimo, org);
                    } else {
                        org.setLogged(false).setAnonymous(false);
                        if (org.getType().equalsIgnoreCase(Organization.PRIVATE))
                            org.setTrackingActive(false);
                        ((Button) v).setText(R.string.login_ldap);
                        viewModel.updateOrganization(org);
                        holder.sAnonimo.setEnabled(false);
                        holder.sAnonimo.setChecked(false);
                        notifyItemChanged(position);
                    }
                    break;
                case R.id.ibtnAddToPreferiti:

//                    org.setPreferito(!org.isPreferito());
                    if (Tools.isUserLogged(context)) {
                        holder.ibtnPreferito.setImageResource(!org.isFavorite() ? R.drawable.icon_fav_si : R.drawable.icon_fav_no);
                        RotateAnimation rotate1 = new RotateAnimation(0, 216, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotate1.setDuration(500);
                        rotate1.setInterpolator(new LinearInterpolator());
                        holder.ibtnPreferito.startAnimation(rotate1);

                        if (org.isFavorite())
                            viewModel.removeFavorite(org);
                        else viewModel.addFavorite(org);

                    } else {
                        Toast.makeText(context, R.string.not_logged_yet, Toast.LENGTH_SHORT).show();
                    }


                    break;
                case R.id.sAnonymousSwitch:
                    viewModel.updateOrganization(org.setAnonymous(holder.sAnonimo.isChecked()));
                    break;
                case R.id.ibtnInfo:
                    new DetailsDialog(context, org).showOrganizationDetails();
                    break;
            }
        };

        holder.sAnonimo.setEnabled(org.isLogged());
        holder.ibtnInfo.setOnClickListener(listener);
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions().error(R.drawable.logo_unipd))
                .load(org.getImageUrl())
                .fitCenter()
                .into(holder.civIconOrganizzazione);

        holder.btnTracciami.setOnClickListener(listener);

        if (org.getType().equalsIgnoreCase(Organization.PUBLIC)) {
            holder.sAnonimo.setVisibility(View.GONE);
            holder.btnLoginLDAP.setVisibility(View.GONE);
        } else {
            holder.sAnonimo.setVisibility(View.VISIBLE);
            holder.btnLoginLDAP.setVisibility(View.VISIBLE);
        }

        holder.ibtnTrackingStatus.setOnClickListener(listener);
        if (org.isTrackingActive()) {
            holder.ibtnTrackingStatus.setImageResource(R.drawable.ic_tracking_on);
            holder.btnTracciami.setText(R.string.stop);
        } else {
            holder.ibtnTrackingStatus.setImageResource(R.drawable.ic_tracking_off);
            holder.btnTracciami.setText(R.string.track_me);
        }
        if (org.getPlaces() != null) {
            StringBuilder builder = new StringBuilder();
            org.getPlaces().forEach(p -> builder.append(p.getName()));
        }

        holder.tvNomeOrganizzazione.setText(org.getName());
        holder.llTitle.setOnClickListener(listener);

//        holder.tvElencoLuoghi =

        holder.ibtnPreferito.setOnClickListener(listener);

        holder.btnLoginLDAP.setOnClickListener(listener);
        holder.btnLoginLDAP.setActivated(org.isLogged());
        holder.btnLoginLDAP.setText(org.isLogged() ? R.string.logout : R.string.login_ldap);

        holder.sAnonimo.setOnClickListener(listener);
        holder.sAnonimo.setChecked(org.isAnonymous());

        holder.btnTracciami.setEnabled(!org.getType().equalsIgnoreCase(Organization.PRIVATE) || org.isLogged());


    }


    private void showLDAPLoginDialog(Button v, Switch anonimo, Organization organization) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.StalkerDialogTheme);
        builder.setTitle(R.string.login);
        builder.setMessage(R.string.inserisci_le_credenziali_ldap);
        builder.setView(LayoutInflater.from(context).inflate(R.layout.form_login_with_ldap, null));
        builder.setPositiveButton(context.getString(R.string.conferma), (dialog, which) -> {
                    Dialog d = (Dialog) dialog;
                    EditText etUsername = d.findViewById(R.id.etCN);
                    EditText etPassword = d.findViewById(R.id.etPassword);
                    String url = "cn=" + etUsername.getText().toString();
                    if (organization.getLdapCommonName() != null && !organization.getLdapCommonName().equals("")) {
                        url += "," + organization.getLdapCommonName();
                    }
                    if (organization.getLdapDomainComponent() != null && !organization.getLdapDomainComponent().equals(""))
                        url += "," + organization.getLdapDomainComponent();


//                    // todo togliere i commenti
//                    Log.d(TAG, "showLDAPLoginDialog: " + organization.getLdapUrl() + organization.getLdapPort() + organization.getLdapCommonName());
                    //StalkerLDAP ldap = new StalkerLDAP(organization.getLdapUrl(), organization.getLdapPort(),
                    //        url, etPassword.getText().toString());

                    StalkerLDAP ldap = new StalkerLDAP("10.0.2.2", organization.getLdapPort(),
                            url, etPassword.getText().toString());
                    try {
                        ldap.bind();
                        ldap.search();
                        v.setText(R.string.logout);
                        organization.setLogged(true);
                        organization.setPersonalCn(etUsername.getText().toString());
                        organization.setLdapPassword(etPassword.getText().toString());
                        viewModel.updateOrganization(organization);
                        anonimo.setEnabled(true);
                        anonimo.setChecked(false);

                        Toast.makeText(context, R.string.logged, Toast.LENGTH_SHORT).show();
                    } catch (LDAPException e) {
                        Toast.makeText(context, R.string.connection_to_ldap_failed, Toast.LENGTH_SHORT).show();
                    } catch (ExecutionException e) {
                        Toast.makeText(context, R.string.ldap_login_failed_check_credentials, Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                }
        );

        builder.setNegativeButton(R.string.annulla, (dialog, which) -> dialog.dismiss());
        builder.create().show();

    }

    public Organization getOrganizationAt(int pos) {
        return listOrganization.get(pos);
    }

    @Override
    public int getItemCount() {
        return listOrganization.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNomeOrganizzazione;
        Button btnTracciami, btnLoginLDAP;
        ImageButton ibtnPreferito, ibtnTrackingStatus;
        Switch sAnonimo;
        CardView cvTrackingitem;
        LinearLayout llInformationToHide, llTitle;
        CircleImageView civIconOrganizzazione;
        ImageButton ibtnInfo;

        public ViewHolder(@NonNull View v) {
            super(v);

            tvNomeOrganizzazione = v.findViewById(R.id.tvNomeOrganizzazione);


            btnTracciami = v.findViewById(R.id.btnStartTracking);
            btnLoginLDAP = v.findViewById(R.id.btnLoginLDAP);
            ibtnPreferito = v.findViewById(R.id.ibtnAddToPreferiti);
            sAnonimo = v.findViewById(R.id.sAnonymousSwitch);
            cvTrackingitem = v.findViewById(R.id.llTrackingItem);
            llInformationToHide = v.findViewById(R.id.llHidingInformation);
            llTitle = v.findViewById(R.id.llTitle);
            ibtnTrackingStatus = v.findViewById(R.id.ibtnTrackingOn);
            civIconOrganizzazione = v.findViewById(R.id.civIconOrganizzazione);
            ibtnInfo = v.findViewById(R.id.ibtnInfo);
        }
    }

}
