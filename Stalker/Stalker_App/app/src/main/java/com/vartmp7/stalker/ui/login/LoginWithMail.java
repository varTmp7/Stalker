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
import android.app.Dialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.vartmp7.stalker.MainActivity;
import com.vartmp7.stalker.R;

import org.jetbrains.annotations.NotNull;

public class LoginWithMail extends Fragment implements View.OnClickListener {
    private EditText etEmail, etPassoword;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.form_login_with_mail, container, false);
        etEmail = root.findViewById(R.id.etEmail);
        etPassoword = root.findViewById(R.id.etPassword);

        mAuth = FirebaseAuth.getInstance();

        root.findViewById(R.id.btnPasswordForgot).setOnClickListener(this);
        root.findViewById(R.id.btnLogin).setOnClickListener(this);
        return root;
    }
    private void showConfirmDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.password_recover_form, null));
        builder.setTitle(R.string.recupero_password);
        builder.setMessage(R.string.ask_mail_for_recupero_password);
        builder.setPositiveButton(getString(R.string.conferma), (dialog, which) -> {
            Dialog d = (Dialog) dialog;
            EditText et =d.findViewById(R.id.etEmailToRecover);
            if (!et.getText().toString().trim().equals("")){
                sendPasswordRecoverMail(et.getText().toString());
            }else{
                Toast.makeText(requireContext(), R.string.inserire_indirizzo_mail, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.annulla, (dialog, which) -> dialog.dismiss());
        builder.create().show();

    }

    private void sendPasswordRecoverMail(String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            int id;
            if (task.isSuccessful()){
                id=R.string.mail_inviata_con_successo;
            }else {
                id= R.string.qualcosa_non_e_andato_bene;
            }
            Toast.makeText(requireContext(),getString(id), Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()){
            case R.id.btnPasswordForgot:
                showConfirmDialog();
                break;
            case R.id.btnLogin:
                String email = etEmail.getText().toString().trim();
                if (!email.equals("")){
                    String password = etPassoword.getText().toString().trim();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(requireContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    requireActivity().getApplication().setTheme(R.style.AppTheme);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(requireContext(), "Autenticazione fallita!",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                    );
                }else{
                    Toast.makeText(requireContext(),"La mail non è valida!", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

}
