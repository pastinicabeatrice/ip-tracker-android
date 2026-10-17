package com.iptracker.app.firebase;

import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import android.content.Context;
import android.util.Log;
import java.util.concurrent.TimeUnit;

public class FirebaseAuthManager {
    private static final String TAG = "FirebaseAuthManager";
    private final FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth.AuthStateListener authStateListener;

    public FirebaseAuthManager() {
        auth = FirebaseAuth.getInstance();
        setupAuthStateListener();
    }

    private void setupAuthStateListener() {
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                user.getIdToken(true)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Token refreshed successfully");
                            }
                        });
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
        auth.addAuthStateListener(authStateListener);
    }

    public void initializeGoogleSignIn(Context context, String webClientId) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public Task<AuthResult> signInWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> Log.d(TAG, "signInWithGoogle:success"))
                .addOnFailureListener(e -> Log.w(TAG, "signInWithGoogle:failure", e));
    }

    public Task<AuthResult> signInWithEmail(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> Log.d(TAG, "signInWithEmail:success"))
                .addOnFailureListener(e -> Log.w(TAG, "signInWithEmail:failure", e));
    }

    public Task<AuthResult> createAccount(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> Log.d(TAG, "createAccount:success"))
                .addOnFailureListener(e -> Log.w(TAG, "createAccount:failure", e));
    }

    public void signOut() {
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserSignedIn() {
        return getCurrentUser() != null;
    }

    public void removeAuthStateListener() {
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }
}