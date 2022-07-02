package com.example.tincoderfirebase.ui.profile;

import static com.example.tincoderfirebase.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.tincoderfirebase.MainActivity;
import com.example.tincoderfirebase.R;
import com.example.tincoderfirebase.databinding.FragmentMyProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MyProfileFragment extends Fragment {

    // từ fragment vào đây để ánh xạ view
    private ImageView imgAvatar;
    private EditText edtFullName, edtEmail;
    private Button btnUpdate, btnUpdateEmail;
    private Uri mUri;

    private ProgressDialog mProgressDialog;

    private MainActivity mMainActivity;
    private FragmentMyProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //ViewModelProvider là một tiện ích để lấy instance của ViewModel tức MyProfileViewModel
        MyProfileViewModel myProfileViewModel =
                new ViewModelProvider(this).get(MyProfileViewModel.class);

        binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        anhXa();
        mMainActivity = (MainActivity) getActivity();
        mProgressDialog = new ProgressDialog(getActivity());

        myProfileViewModel.getText().observe(getViewLifecycleOwner(), edtFullName::setText);
        myProfileViewModel.getText().observe(getViewLifecycleOwner(), edtEmail::setText);
        setUserInfor();

        eventListener();
        return root;
    }

    private void eventListener() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mở gallery để chọn ảnh
                onClickRequestPermission();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });

        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateEmail();
            }
        });
    }

    private void onClickUpdateEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }
        String strEmail = edtEmail.getText().toString().trim();
        mProgressDialog.show();
        user.updateEmail(strEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Cập nhật email thành công!", Toast.LENGTH_SHORT).show();
                            mMainActivity.showUserProfile();
                        }
                    }
                });
    }

    //tạo set uri pv update; dùng để gọi bên main để truyền uri đó qua

    public void setUri(Uri uri) {
        mUri = uri;
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        mProgressDialog.show();
        String fullName = edtFullName.getText().toString().trim();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            //sau khi update thì cập nhật lại hiển thị trên menu (navigation)
                            mMainActivity.showUserProfile();
                        }
                    }
                });
    }

    private void onClickRequestPermission() {
        //cách sử dụng phương thức từ một activity khác
        if (mMainActivity == null) {
            return;
        }
        //nếu android nhỏ hơn API 23
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mMainActivity.openGallary();
            return;
        }
        //nếu ng dùng cho phép sd rồi
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mMainActivity.openGallary();
        } else {
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
        }

    }

    public void setBitmapImageView(Bitmap bitmapImageView) {
        imgAvatar.setImageBitmap(bitmapImageView);
    }

    private void anhXa() {
        imgAvatar = binding.imgAvatar;
        edtFullName = binding.edtFullName;
        edtEmail = binding.edtEmail;
        btnUpdate = binding.btnUpdateProfile;
        btnUpdateEmail = binding.btnUpdateEmail;
    }

    private void setUserInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        edtFullName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar).into(imgAvatar);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}