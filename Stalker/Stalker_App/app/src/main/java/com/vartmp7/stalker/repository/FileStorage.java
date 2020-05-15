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

package com.vartmp7.stalker.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.datamodel.OrganizationResponse;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.AccessLevel;
import lombok.Getter;

public class FileStorage implements Storage {
    private String fileName;
    private Context context;
    private Gson gson;
    @Getter(AccessLevel.PUBLIC)
    private MutableLiveData<List<Organization>> mLiveOrgs;

    public FileStorage(String fileName, Context context, MutableLiveData<List<Organization>> org) {
        this.fileName = fileName;
        this.context = context;
        this.gson = new Gson();
        this.mLiveOrgs = new MutableLiveData<>(new ArrayList<>());
    }

    @Override
    public void updateOrganizations(@NotNull List<Organization> org) {
        List<Organization> orgList = mLiveOrgs.getValue()!=null ? new ArrayList<>(mLiveOrgs.getValue()) : new ArrayList<>();
        for (Organization organization : org) {
            int i = orgList.indexOf(organization);
            if(i!=-1){
                orgList.set(i,organization);
            }
        }
        saveOrganizations(orgList);
    }

    @Override
    public void updateOrganization(Organization o) {
        List<Organization> l = mLiveOrgs.getValue()!=null ? new ArrayList<>(mLiveOrgs.getValue()) : new ArrayList<>();
        int pos = -1;
        for (int i = 0; i < l.size() && pos == -1; i++)
            if (o.getId() == l.get(i).getId())
                pos = i;
        if (pos != -1) {
            l.remove(pos);
            l.add(pos, o);
            saveOrganizations(l);
        }
    }

    @Override
    public void updateOrganizationInfo(@NotNull List<Organization> orgsToUpdate) {
        List<Organization> currentOrgs = mLiveOrgs.getValue()!=null? new ArrayList<>(mLiveOrgs.getValue()) : new ArrayList<>();
        List<Organization> toSave = new ArrayList<>();
        for (int j = 0; j < orgsToUpdate.size(); j++) {
            boolean contained = false;
            Organization orgToUpdate = orgsToUpdate.get(j);
            for (int i = 0; i < currentOrgs.size() && !contained; i++) {
                Organization currentOrg = currentOrgs.get(i);
                if (currentOrg.getId() == orgToUpdate.getId()) {
                    contained = true;
                    orgToUpdate.setTrackingActive(currentOrg.isTrackingActive());
                    orgToUpdate.setTracking(currentOrg.isTracking());
                    orgToUpdate.setFavorite(currentOrg.isFavorite());
                    orgToUpdate.setLogged(currentOrg.isLogged());
                    orgToUpdate.setAnonymous(currentOrg.isAnonymous());
                    orgToUpdate.setPersonalCn(currentOrg.getPersonalCn());
                    orgToUpdate.setLdapPassword(currentOrg.getLdapPassword());
                    toSave.add(orgToUpdate);
                }
            }
            if (!contained) {
                toSave.add(orgToUpdate);
            }
        }
        saveOrganizations(toSave);
    }


    @Override
    public LiveData<List<Organization>> getLocalOrganizations() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try (FileInputStream fis = context.openFileInput(fileName)){

                    StringBuilder stringBuilder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
                        String line = reader.readLine();
                        while (line != null) {
                            stringBuilder.append(line).append('\n');
                            line = reader.readLine();
                        }
                    } catch (IOException e) {
                    } finally {
                        String contents = stringBuilder.toString();
                        OrganizationResponse responseOrganizzazioni = gson.fromJson(contents, OrganizationResponse.class);
                        if (responseOrganizzazioni != null) {

                            List<Organization> organizzazioni = responseOrganizzazioni.getOrganizations();//mLiveOrgs.getValue();
                            mLiveOrgs.postValue(organizzazioni);
                        }  //                            Log.e(TAG, "run: ResponseOrganizzazioni null");


                    }


                } catch (IOException ignored) {

                }
            }
        }.start();
        return mLiveOrgs;
    }


    @Override
    public synchronized void saveOrganizations(List<Organization> orgs) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public synchronized void run() {

                File orgJson = new File(context.getFilesDir(), fileName);
                if (!orgJson.exists()) {
                    try {
                        boolean b =orgJson.createNewFile();
                        if (!b)
                            return;
                    } catch (IOException e) {
                    }
                }

                try (FileWriter writer = new FileWriter(orgJson)){
                    String l = new Gson().toJson(new OrganizationResponse().setOrganizations(orgs));
                    writer.write(l);
                    writer.flush();
                    writer.close();
                    mLiveOrgs.postValue(orgs);
                } catch (IOException e) {
                }
            }
        });
    }



}
