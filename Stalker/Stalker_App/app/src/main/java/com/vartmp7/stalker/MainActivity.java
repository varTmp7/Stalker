package com.vartmp7.stalker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unboundid.ldap.sdk.LDAPException;
import com.vartmp7.stalker.GsonBeans.Coordinata;
import com.vartmp7.stalker.GsonBeans.Luogo;
import com.vartmp7.stalker.GsonBeans.Organizzazione;
import com.vartmp7.stalker.GsonBeans.ResponseLuogo;
import com.vartmp7.stalker.GsonBeans.ResponseOrganizzazione;
import com.vartmp7.stalker.GsonBeans.TrackSignal;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.vartmp7.stalker.Tools.getUnsafeOkHttpClient;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final String TAG = "com.vartmp7.stalker.MainActivity";
    private Spinner sScegliOrganizzazione;
    private StalkerLDAP ldap;
    private ArrayList<Organizzazione> organizzazioni;
    private ArrayList<Luogo> luoghi;
    private TextView tvCurrentStatus;
    private int[] viewToshowOnChoice = {R.id.tvTestoElenco, R.id.tvElencoLuoghi,
            R.id.btnStartTracking, R.id.btnShowLoginDialog};
    private int[] viewToShowOnTracking = {R.id.tvStatus, R.id.tvCurrentStatus};
    private OkHttpClient client;
    private int FAIL_RESPONSE_CODE = 0;
    public final static String SERVER = "https://151.95.124.245/";
    //    public final static String SERVER = "https://192.168.31.76:5000/";
    private LocationManager locationManager;
    private Gson gson;
    private TextView tvLuoghi;
    private Luogo prevLuogo;
    private Tracker tracker = new Tracker(MainActivity.this, new StalkerTrackingCallBack() {
        @Override
        public void onLocationsChanged(Location l) {
            Coordinata c = new Coordinata(l.getLatitude(), l.getLongitude());
            Organizzazione org =
                    organizzazioni.get(sScegliOrganizzazione.getSelectedItemPosition() - 1);
            trackSignal = new TrackSignal()
                    .setIdOrganization(Long.parseLong(org.getId()))
                    .setAuthenticated(false);

            SimpleDateFormat format = new SimpleDateFormat("Y-M-d hh:mm:ss");
            Date date = Calendar.getInstance(Locale.getDefault()).getTime();
            String formattedDate = format.format(date);
            trackSignal.setDate_time(formattedDate.replace(" ", "T"));

            if (ldap != null && !org.getType().equalsIgnoreCase("public"))
                trackSignal.setAuthenticated(true)
                        .setSurname(ldap.getSurname())
                        .setUsername(ldap.getUid())
                        .setUid_number(Long.parseLong(ldap.getUidNumber()));

            for (int i = 0; i < luoghi.size() && !trackSignal.isEntered(); i++) {
                Luogo luogo = luoghi.get(i);
                if (luogo.isInPlace(c)) {
                    tvCurrentStatus.setText(String.format(getString(R.string.you_are_in), luogo.getName()));
                    if (prevLuogo == null) {
                        prevLuogo = luogo;
                        trackSignal.setIdPlace(prevLuogo.getId()).setEntered(true);
                        post(trackSignal.getUrlToPost(), gson.toJson(trackSignal));
                    } else if (prevLuogo != luogo) {
                        trackSignal.setEntered(false).setIdPlace(prevLuogo.getId());
                        post(trackSignal.getUrlToPost(), gson.toJson(trackSignal));
                        trackSignal.setEntered(true).setIdPlace(luogo.getId());
                        post(trackSignal.getUrlToPost(), gson.toJson(trackSignal));
                        prevLuogo = luogo;
                    }
                    return;
                }
            }
            if (prevLuogo != null) {
                trackSignal.setEntered(false).setIdPlace(prevLuogo.getId());
                post(trackSignal.getUrlToPost(), gson.toJson(trackSignal));
                tvCurrentStatus.setText(String.format(getString(R.string.you_left), prevLuogo.getName()));
                prevLuogo = null;
            } else
                tvCurrentStatus.setText(R.string.not_in_any_place);

        }
    });


    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
            get(SERVER + "organizations");
        else
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        client = getUnsafeOkHttpClient();// Client usato a scopo di test
        sScegliOrganizzazione = findViewById(R.id.s_scegliOrganizzazione);
        sScegliOrganizzazione.setSelected(false);
        findViewById(R.id.btnShowLoginDialog).setOnClickListener(this);
        findViewById(R.id.btnStartTracking).setOnClickListener(this);
        findViewById(R.id.btnRefresh).setOnClickListener(this);

        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        tvLuoghi = findViewById(R.id.tvElencoLuoghi);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Seleziona un'organizzazione..."});

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sScegliOrganizzazione.setAdapter(adapter);
        sScegliOrganizzazione.setOnItemSelectedListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    get(SERVER + "organizations");
                } else {
                    Toast.makeText(this, "I need permissions ", Toast.LENGTH_SHORT).show();
                }
            case 0:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startTracking();
                } else {
                    Toast.makeText(this, "I need permissions ", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }
                break;
        }
    }

    void post(String url, String json) {
        RequestBody requestBody = RequestBody.create(json, JSON);
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
            }
        });

    }

    void get(String url) {

        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            Message msg = new Message();
            Bundle b = new Bundle();

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                b.putInt("CODE", 0);
                b.putString("ErrorMsg", e.getMessage());
                msg.setData(b);
                error_handler.sendMessage(msg);
//                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    int req_code = Integer.parseInt(Objects.requireNonNull(response.header("req_code")));
                    b.putInt("REQ_CODE", req_code);
                    String str = response.body().string();
                    b.putString("MSG", str);
                } catch (NullPointerException e) {
                    b.putInt("CODE", FAIL_RESPONSE_CODE);
                } finally {

                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            }
        });

    }


    @SuppressLint("HandlerLeak")
    private Handler error_handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            ResponseOrganizzazione r = new ResponseOrganizzazione();
            r.setOrganizations(new ArrayList<Organizzazione>());
            loadOrganizazzione(r);
            Toast.makeText(MainActivity.this, msg.getData().getString("ErrorMsg"), Toast.LENGTH_SHORT).show();

        }
    };
    private final int REQ_ORG = 0;
    private final int REQ_LUOGHI = 5;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        private Gson gson = new Gson();

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            int code = msg.getData().getInt("REQ_CODE");
            String s = msg.getData().getString("MSG");
            switch (code) {
                case REQ_ORG:
//                  Log.d(TAG, "handleMessage: "+s);
                    loadOrganizazzione(gson.fromJson(s, ResponseOrganizzazione.class));
                    break;
                case REQ_LUOGHI:
                    updateLuoghi(gson.fromJson(s, ResponseLuogo.class));
                    // aggiornare i luoghi
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Something failed!", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void updateLuoghi(ResponseLuogo l) {
        luoghi = l.getPlaces();
        tvLuoghi.setText(l.getDataForSpinner());
    }

    private void loadOrganizazzione(ResponseOrganizzazione orgs) {

        if (orgs != null) {
            String[] mList = orgs.getDataForSpinner();
            organizzazioni = orgs.getOrganizations();
            if (organizzazioni.size() == 0) {
                mList = new String[]{"Non ci sono organizzazioni!"};
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mList);

            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            sScegliOrganizzazione.setAdapter(adapter);
        } else
            Toast.makeText(MainActivity.this, "Qualcosa è andato storto!\nRiprova più tardi",
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            hideView(viewToshowOnChoice);
            hideView(viewToShowOnTracking);
            return;
        }

        String req = String.format("organizations/%s/places", organizzazioni.get(position - 1).getId());
//        Log.d(TAG, "onItemSelected: REQ" + SERVER + req);
        get(SERVER + req);
        trackingSwitch();
        showView(viewToshowOnChoice);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void showView(int[] views) {
        for (int i : views) {
            findViewById(i).setVisibility(View.VISIBLE);
        }
        if (!organizzazioni.get(sScegliOrganizzazione.getSelectedItemPosition() - 1).getType().equalsIgnoreCase("private")) {
            findViewById(R.id.btnShowLoginDialog).setVisibility(View.VISIBLE);
        }
    }

    private void hideView(int[] views) {
        for (int i : views) {
            findViewById(i).setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.btnShowLoginDialog).setVisibility(View.INVISIBLE);
    }

    private TrackSignal trackSignal = null;


    private void startTracking() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, -1, 5, tracker);
        } else
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    public void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(R.string.login);
        builder.setMessage("Fai accesso all'organizzazione che hai scelto");
        builder.setView(inflater.inflate(R.layout.dialog_login, null));
        builder.setPositiveButton(getString(R.string.conferma), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Dialog d = (Dialog) dialog;
                        EditText etUsername = d.findViewById(R.id.etUsername);
                        EditText etPassword = d.findViewById(R.id.etPassword);

                        ldap = new StalkerLDAP("10.0.2.2", 389,
                                etUsername.getText().toString(), etPassword.getText().toString());
                        try {
                            ldap.bind();
                            ldap.search();
                            Toast.makeText(MainActivity.this, "Logged", Toast.LENGTH_SHORT).show();
//                            ((Button)findViewById(R.id.btnShowLoginDialog)).setText("logged");
                        } catch (LDAPException | ExecutionException | InterruptedException e) {
                            Toast.makeText(MainActivity.this, "Qualcosa è andato storto, ri " +
                                    "provare più tardi", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }

        );

        builder.setNegativeButton(R.string.annulla, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void trackingSwitch() {
        locationManager.removeUpdates(tracker);
        ((Button) findViewById(R.id.btnStartTracking)).setText(getString(R.string.start_track));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartTracking:
                Button btn = (Button) v;
                if (btn.getText().toString().equalsIgnoreCase(getResources().getString(R.string.start_track))) {
                    Toast.makeText(MainActivity.this, sScegliOrganizzazione.getSelectedItem() + " ti sta tracciando!", Toast.LENGTH_SHORT).show();
                    showView(viewToShowOnTracking);
                    tvCurrentStatus.setText(String.format("%s ti sta tracciando!", sScegliOrganizzazione.getSelectedItem()));
                    startTracking();
                    btn.setText(getString(R.string.stop));
                } else {
                    trackingSwitch();
                }
                break;
            case R.id.btnRefresh:
                get(SERVER + "organizations");
                break;
            case R.id.btnShowLoginDialog:
                showLoginDialog();
                break;
        }
    }
}
