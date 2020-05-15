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

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.vartmp7.stalker.R;

import org.jetbrains.annotations.NotNull;

public class SignUpWithMail extends Fragment implements View.OnClickListener {
    private EditText etEmail, etPassword, etRPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.form_sign_up, container, false);

        etEmail = root.findViewById(R.id.etEmail);
        etPassword = root.findViewById(R.id.etPassword);
        etRPassword = root.findViewById(R.id.etRipetiPassword);
        root.findViewById(R.id.btnReset).setOnClickListener(this);
        root.findViewById(R.id.btnSignUp).setOnClickListener(this);
        return root;
    }


    private void showSignUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.sign_up);
        builder.setMessage(R.string.confermare_registrazione);
        builder.setPositiveButton(getString(R.string.conferma), (dialog, which) -> {
            if (etPassword.getText().toString().trim().length() < 8) {
                Toast.makeText(requireContext(), R.string.password_troppo_corta, Toast.LENGTH_SHORT).show();
            } else if (etPassword.getText().toString().equals(etRPassword.getText().toString())) {
                signUp(etEmail.getText().toString(), etPassword.getText().toString());
            } else {
                Toast.makeText(requireContext(), R.string.password_non_coincidono, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.annulla, (dialog, which) -> dialog.dismiss());
        builder.create().show();

    }

    private void signUp(String email, String password) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "createUserWithEmail:success");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful())
                                    Toast.makeText(requireContext(), R.string.mail_di_verifica_inviata, Toast.LENGTH_SHORT).show();
                            });
                            if (user.isEmailVerified()) {
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fcvLoginContainer, new LoginWithMail())
                                        .commit();
                            } else {
                                Toast.makeText(requireContext(), R.string.verifica_mail,Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        int message =R.string.unknown_error;
                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            message= R.string.account_esistente_con_questa_mail;
                        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show();

                    }
                }
        );
    }

    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.btnReset:
                etEmail.setText("");
                etPassword.setText("");
                etRPassword.setText("");
                break;
            case R.id.btnSignUp:
                showSignUpDialog();
                break;
        }
    }
}
