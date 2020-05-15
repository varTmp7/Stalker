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

//     * Stalker
//     * P=3.1415926
//     *
//     * project google sign in client id
//     * 543529788745-mc7pagmter3bvr7i3e1vim0tjq0fhov6.apps.googleusercontent.com
//     *
//     *client secret:
//     * _g0uY5uQ87yD469NmjFJ4kS7
package com.vartmp7.stalker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vartmp7.stalker.datamodel.Organization;
import com.vartmp7.stalker.repository.FirebaseFavoritesSource;
import com.vartmp7.stalker.repository.OrganizationsRepository;
import com.vartmp7.stalker.ui.IntroActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiaowei Wen, Lorenzo Taschin
 */
public class MainActivity extends AppCompatActivity {
    public static final String PREFERENCE_FILE = "stalker";
    public static final String PREFERENCE_NOT_LOGIN = "not_login";
    //    public static final String URL_SERVER="https://stalker-be.ddns.net/";
    public static final String URL_SERVER = "https://10.0.2.2/";
    public static final String FIRST_LOG="first_log";
    private GoogleSignInClient mGoogleSignInClient;

    private NavController navController;
    public static OrganizationsRepository repository;

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme them = super.getTheme();
        them.applyStyle(R.style.AppTheme, true);
        return them;
    }
    private void showIntroduction(){
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (defaultSharedPreferences.getBoolean(FIRST_LOG,true)){
            startActivity(new Intent(this, IntroActivity.class));
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!Tools.isUserLogged(this) && !getSharedPreferences(PREFERENCE_FILE, MODE_PRIVATE).getBoolean(PREFERENCE_NOT_LOGIN, false)){
            goToLoginActivity(false);
        }else{
            showIntroduction();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_organizations, R.id.navigation_tracking, R.id.navigation_preferiti, R.id.navigation_cronologia)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);


        if (FirebaseAuth.getInstance().getCurrentUser() != null || GoogleSignIn.getLastSignedInAccount(this) != null) {
            OrganizationsRepository orgRepo = ((StalkerApplication) getApplication()).getOrganizationsRepositoryComponent().organizationsRepository();
            orgRepo.setOrganizationFavoritesSource(new FirebaseFavoritesSource(getUserId(),FirebaseFirestore.getInstance()));
        }

        NavigationUI.setupWithNavController(navView, navController);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gson.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Tools.isNetworkAvailable(this))
            gotoSetting(R.string.devi_attivare_connessione, Settings.ACTION_WIFI_SETTINGS);
        if (!Tools.isGPSEnable(this)) {
            gotoSetting(R.string.devi_attivare_gps, Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        }
    }

    private void gotoSetting(int msg, String action) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    startActivity(new Intent(action));
                    dialog.dismiss();
                })
                .setTitle(R.string.attenzione)
                .setNegativeButton(R.string.esci, (dialog, which) -> {
                    dialog.cancel();
                    finish();
                });
        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (Tools.isUserLogged(MainActivity.this)) {
            inflater.inflate(R.menu.setting_menu, menu);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                if (!(FirebaseAuth.getInstance().getCurrentUser().getProviderData().size() >= 2 &&
                        FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getProviderId()
                                .equals(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD))) {
                    menu.removeItem(R.id.menuModificaDati);
                }
            } else {
                menu.removeItem(R.id.menuModificaDati);
            }
        } else {
            inflater.inflate(R.menu.setting_menu_no_log, menu);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    logout();
                } else {
                    googleSignOut();
                }
                goToLoginActivity(false);
                return false;
            case R.id.menuLogin:
                goToLoginActivity(true);
                break;
            case R.id.menuModificaDati:
                showEditInfoDialog();
                break;
            case R.id.menuReportBug:
                segnalaBug();
                break;
            case R.id.menuManuale:
                String url = "https://github.com/varTmp7/Stalker/tree/master/RA/DocumentiEsterni";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private void segnalaBug() {
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.mail_gruppo)});
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.segnala_bug));
        mailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.descrizione_bug));
        mailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(mailIntent,getString(R.string.choose_mail_client)));
    }


    private void showEditInfoDialog() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.StalkerDialogTheme);
        builder.setTitle(R.string.modifica_dati);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_dati_form, null);
        EditText etNewMail = view.findViewById(R.id.etEmail);

        etNewMail.setText(currentUser.getEmail());
        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etRPassword = view.findViewById(R.id.etRipetiPassword);
        builder.setIcon(R.drawable.ic_edit_black_24dp)
                .setView(view)
                .setPositiveButton(R.string.conferma, (dialog, which) -> {
                    if (!etNewMail.getText().toString().trim().equalsIgnoreCase(currentUser.getEmail())) {
                        currentUser.updateEmail(etNewMail.getText().toString()).addOnCompleteListener(task -> {
                            int stringId = task.isSuccessful() ? R.string.modificato_con_successo : R.string.modifica_di_fallito;
                            showToast(getString(stringId, getString(R.string.email)));
                        });
                    }
                    if (etPassword.getText().toString().trim().equalsIgnoreCase(""))
                        return;
                    updatePassword(etPassword.getText().toString(), etRPassword.getText().toString());

                }).setNegativeButton(R.string.annulla, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void updatePassword(@NotNull String password, @NotNull String repeatPassword) {
        if (password.trim().equals(repeatPassword.trim())) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser()
                        .updatePassword(password)
                        .addOnCompleteListener(task -> {
                            int stringId = task.isSuccessful() ? R.string.modificato_con_successo : R.string.modifica_di_fallito;
                            showToast(getString(stringId, getString(R.string.password)));
                        });
            }
        } else {
            Toast.makeText(this, R.string.password_non_coincidono, Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (GoogleSignIn.getLastSignedInAccount(MainActivity.this) != null)
            return GoogleSignIn.getLastSignedInAccount(MainActivity.this).getId();
        return "";
    }


    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        cleanUserData();
    }

    private void cleanUserData() {
        OrganizationsRepository orgRepo = ((StalkerApplication)getApplication()).getOrganizationsRepositoryComponent().organizationsRepository();
        List<Organization> organizations = orgRepo.getOrganizations().getValue();
        if (organizations != null) {
            organizations = new ArrayList<>(organizations);
            organizations.stream().sequential().forEach(o ->
                    o.setLogged(false)
                            .setTracking(false)
                            .setTrackingActive(false)
                            .setAnonymous(false)
                            .setFavorite(false)
                            .setPersonalCn("")
                            .setLdapPassword("")
            );
            orgRepo.updateOrganizations(organizations);
        }

    }

    private void googleSignOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task ->
                        Toast.makeText(MainActivity.this, R.string.successfully_logout, Toast.LENGTH_SHORT).show());
    }

    public void goToLoginActivity(boolean backButton) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        if (!backButton) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        getApplication().setTheme(R.style.AppThemeNoActionBar);

        startActivity(intent);
    }
}
