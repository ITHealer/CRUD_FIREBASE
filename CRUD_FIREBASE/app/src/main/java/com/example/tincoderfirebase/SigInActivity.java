package com.example.tincoderfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//đăng nhập
public class SigInActivity extends AppCompatActivity {

    private LinearLayout layoutSigUp;
    private EditText edtEmail, edtPassword;
    private Button btnSigIn;
    private ProgressDialog mProgressDialog;
    private LinearLayout layoutForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sig_in);

        initUI();
        initListener();
    }

    private void initListener() {
        layoutSigUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigInActivity.this, SigUpActivity.class);
                startActivity(intent);
            }
        });

        btnSigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn();
            }
        });

        layoutForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickForgotPassword();
            }
        });
    }

    private void onClickForgotPassword() {
        mProgressDialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //tạo một dialog để user xác nhận email
        String emailAddress = "ungminhhoai29@gmail.com";

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(SigInActivity.this, "Success.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onClickSignIn() {
        //thêm phần check validate vào
        String strEmail = edtEmail.getText().toString().trim();
        String strPass = edtPassword.getText().toString().trim();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mProgressDialog.show();
        mAuth.signInWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(SigInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
//                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SigInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });

    }

    private void initUI() {
        mProgressDialog = new ProgressDialog(this);
        layoutSigUp = (LinearLayout) findViewById(R.id.layout_sigup);
        layoutForgotPassword = (LinearLayout) findViewById(R.id.layout_forget_password);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnSigIn = (Button) findViewById(R.id.btn_sign_in);
    }
}