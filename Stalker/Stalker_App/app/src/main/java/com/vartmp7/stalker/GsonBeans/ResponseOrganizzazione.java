package com.vartmp7.stalker.GsonBeans;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ResponseOrganizzazione {

    private ArrayList<Organizzazione> organizations;

    public ArrayList<Organizzazione> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(ArrayList<Organizzazione> organizations) {
        this.organizations = organizations;
    }


    public String[] getDataForSpinner() {
        ArrayList<String> toRet = new ArrayList<>();
        toRet.add("Scegli un'organizzazione");

        for (Organizzazione org : organizations) {
            toRet.add(org.getName());
        }

        return toRet.toArray(new String[0]);
    }

    public int getOrganizzationsLength() {
        return organizations.size();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder= new StringBuilder();
        for (Organizzazione org: organizations)
            builder.append(org.getName());
        return builder.toString();
    }
}
