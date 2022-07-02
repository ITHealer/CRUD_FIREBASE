package com.example.tincoderfirebase;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tincoderfirebase.ui.profile.MyProfileFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tincoderfirebase.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public static final int MY_REQUEST_CODE = 1;
    //Truyền cái phức openGallary từ MyProfileFragment sang
    final private MyProfileFragment mMyProfileFragment = new MyProfileFragment();

    private NavigationView navigationView;
    private TextView tvName, tvEmail;
    private ImageView imgAvatar;
    private MenuItem mLogOut, mMyProfile, mChangePassword;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //nó trả về cho chúng ta sau khi ta chọn ảnh từ Gallary xử lý kq trả về
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent == null) {
                    return;
                }
                Uri uri = intent.getData();
                mMyProfileFragment.setUri(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mMyProfileFragment.setBitmapImageView(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUI();

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_my_profile, R.id.nav_change_password)
                .setOpenableLayout(drawer)
                .build();

        handlerMenuItem();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        showUserProfile();
    }

    private void handlerMenuItem() {
        //xử lý tab Đăng xuất
        mLogOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SigInActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void showUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl(); //url trả về cho ta một uri

            if (name == null) {
                //nếu trong csdl name = null thì ẩn cái name đi
                tvName.setVisibility(View.GONE);
            } else {
                tvName.setVisibility(View.VISIBLE);
                tvName.setText(name);
            }

            tvEmail.setText(email);
            //nếu load có lỗi gì nó sẽ set cái avatar mặc định bên dưới
            Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar).into(imgAvatar);
        }
    }

    //Hàm trả về action của ng dùng cho phép ta sd hay từ chối

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallary();
            } else {
                Toast.makeText(this, "Vui lòng cấp phép cho dịch vu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void initUI() {
        navigationView = binding.navView;
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        imgAvatar = navigationView.getHeaderView(0).findViewById(R.id.img_avatar);

        mLogOut = binding.navView.getMenu().findItem(R.id.nav_sign_out);
        mMyProfile = binding.navView.getMenu().findItem(R.id.nav_my_profile);
        mChangePassword = binding.navView.getMenu().findItem(R.id.nav_change_password);
    }
}