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

package com.vartmp7.stalker.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vartmp7.stalker.MainActivity;
import com.vartmp7.stalker.R;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private static final int RC_SIGN_IN = 1;

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private static final int REQ_COARSE_LOCATION=1;
    private static final int REQ_FINE_LOCATION=2;
    private static final int REQ_INTERNET=3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.form_login,container,false);
//        Log.d(TAG, "onCreateView: LoginFragment");
        Button signUpButton = v.findViewById(R.id.btn_signUp);
        signUpButton.setOnClickListener(this);

        Button signInButton1 = v.findViewById(R.id.btn_signIn);
        signInButton1.setOnClickListener(this);

// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = v.findViewById(R.id.login_button);
        loginButton.setPermissions("email");

        // If using in a fragment
        loginButton.setFragment(this);
        // Callback registration

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                AccessToken accessToken = AccessToken.getCurrentAccessToken();

//                Log.d(TAG, "onSuccess: " + accessToken.getUserId());
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

//                Successivamente, esegui l'accesso vero e proprio, come nell'OnClickListener di un pulsante personalizzato:
//                LoginManager.getInstance().logInWithReadPermissions(LoginActivitity.this, Collections.singletonList("public_profile"));
                handleFacebookAccessToken(accessToken);
            }

            @Override
            public void onCancel() {
                // App code
//                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
//                Log.d(TAG, "onError: " + error.toString());
            }

        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        SignInButton signInButton = v.findViewById(R.id.btn_googleSignIn);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        v.findViewById(R.id.btnProcediSenzaAuth).setOnClickListener(this);
        requestPermissions(Manifest.permission.INTERNET, REQ_INTERNET);
        requestPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, REQ_COARSE_LOCATION);
        requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQ_FINE_LOCATION);


        return v;
    }
    private void handleFacebookAccessToken(@NotNull AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        Log.d(TAG, "handleFacebookAccessToken: " + credential.getProvider());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        goToMainActivity();
//                            updateUI(user);
//                        if (user != null)
//                            Log.d(TAG, "onComplete: " + user.getDisplayName());
                    } else {
                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(requireContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                    }

                    // ...
                });
    }
    private void handleSignInResult(@NotNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            goToMainActivity();
//            Log.d(TAG, "handleSignInResult: " + account.getDisplayName());
            // Signed in successfully, show authenticated UI.

//            updateUI(account);
        } catch (ApiException e) {

            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(requireContext(),"Accesso fallito, riprovare più tardi!", Toast.LENGTH_SHORT).show();
//            updateUI(null);
        }
    }






    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void goToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().getApplication().setTheme(R.style.AppTheme);
        startActivity(intent);
    }
    private void changeMethod(Fragment fragment,String backstackString){
        FragmentManager manager =requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fcvLoginContainer,fragment);
        transaction.addToBackStack(backstackString);
        transaction.commit();
    }

    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.btn_googleSignIn:
                googleSignIn();
                break;
            case R.id.btnProcediSenzaAuth:
                SharedPreferences.Editor editor = requireContext().getSharedPreferences("stalker",MODE_PRIVATE).edit();
                editor.putBoolean("not_login",true).apply();
                goToMainActivity();
                break;
            case R.id.btn_signUp:
//                Log.e(TAG, "cliccato btn signUp");
                changeMethod(new LoginWithMail(),"login_with_mail");
//                showSignUpDialog();
                break;
            case R.id.btn_signIn:
                changeMethod(new SignUpWithMail(),"register_with_mail");
//                showSignInDialog();
                break;
        }
    }
    private void requestPermissions(String permission, int code){
        if ((requireContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)){
            requestPermissions(new String[]{permission},code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQ_COARSE_LOCATION | REQ_FINE_LOCATION | REQ_INTERNET:
                if (grantResults.length==1 && grantResults[0]!= PackageManager.PERMISSION_GRANTED)
                    requestPermissions(permissions[0],requestCode);
                break;
        }
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

}
