package com.example.tincoderfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigUpActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnSigUp;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sig_up);
        
        initUI();
        initListener();
    }

    private void initListener() {
        btnSigUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSigUp();
            }
        });
    }

    private void onClickSigUp() {
        String strEmail = edtEmail.getText().toString().trim();
        String strPass = edtPassword.getText().toString().trim();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //bắt đầu call API thì show Dialog lên
        mProgressDialog.show();
        mAuth.createUserWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        //trả về một đối tượng task
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(SigUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            //đóng tất cả các activity trước tg main
                            finishAffinity();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SigUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void initUI() {
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnSigUp = (Button) findViewById(R.id.btn_sigup);
        mProgressDialog = new ProgressDialog(this);
    }
}