package com.example.tincoderfirebase.ui.changepassword;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tincoderfirebase.databinding.FragmentChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;

    private EditText edtNewPass, edtConfirmNewPass;
    private Button btnChangePass;
    private ProgressDialog mProgressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChangePasswordViewModel changePasswordViewModel =
                new ViewModelProvider(this).get(ChangePasswordViewModel.class);


        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        anhXa();
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePassword();
            }
        });

        changePasswordViewModel.getText().observe(getViewLifecycleOwner(), edtNewPass::setText);


        return root;
    }

    //xác thực lại người dùng
    private void reAuthenticate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234"); //cho một dialog để nhập email pass cũ

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            onClickChangePassword();
                        } else {
                            Toast.makeText(getActivity(), "Cập nhật Password k thành công!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onClickChangePassword() {
        String newPass = edtNewPass.getText().toString().trim();
        mProgressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Cập nhật Password thành công!", Toast.LENGTH_SHORT).show();

                        } else {
                            //Show dialog re-Authenticate

                        }
                    }
                });
    }

    private void anhXa() {
        mProgressDialog = new ProgressDialog(getActivity());
        edtNewPass = binding.edtNewPassword;
        btnChangePass = binding.btnChangePassword;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}