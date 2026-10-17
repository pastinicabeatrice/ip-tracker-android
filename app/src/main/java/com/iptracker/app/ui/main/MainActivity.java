package com.iptracker.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.iptracker.app.R;
import com.iptracker.app.firebase.FirebaseAuthManager;
import com.iptracker.app.ui.auth.GoogleLoginActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuthManager authManager;
    private TextView userEmailTextView;
    private Button enterIpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupFirebase();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
        updateUserInterface();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        enterIpButton = findViewById(R.id.buttonEnterIp);

        if (navigationView.getHeaderCount() > 0) {
            View headerView = navigationView.getHeaderView(0);
            userEmailTextView = headerView.findViewById(R.id.userEmailText);
        }
    }

    private void setupFirebase() {
        authManager = new FirebaseAuthManager();
        if (!authManager.isUserSignedIn()) {
            startLoginActivity();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            handleNavigationItemSelected(id);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void handleNavigationItemSelected(int id) {
        if (id == R.id.nav_enter_ip) {
            safeNavigate(IPInputActivity.class);
        } else if (id == R.id.nav_settings) {
            safeNavigate(PreferencesActivity.class);
        } else if (id == R.id.nav_json_processing) {
            safeNavigate(JsonProcessingActivity.class);
        } else if (id == R.id.nav_async_operations) {
            safeNavigate(AsyncOperationsActivity.class);
        } else if (id == R.id.nav_logout) {
            performLogout();
        } else if (id == R.id.nav_close) {
            finish();
        }
    }

    private void setupClickListeners() {
        enterIpButton.setOnClickListener(v -> safeNavigate(IPInputActivity.class));
    }

    private void updateUserInterface() {
        FirebaseUser currentUser = authManager.getCurrentUser();
        if (currentUser != null && userEmailTextView != null) {
            userEmailTextView.setText(currentUser.getEmail());
        }
    }

    private void safeNavigate(Class<?> destinationClass) {
        try {
            startActivity(new Intent(this, destinationClass));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_navigation, Toast.LENGTH_SHORT).show();
        }
    }

    private void performLogout() {
        try {
            authManager.signOut();
            Intent intent = new Intent(this, GoogleLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_logout, Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, GoogleLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!authManager.isUserSignedIn()) {
            startLoginActivity();
        }
    }
}