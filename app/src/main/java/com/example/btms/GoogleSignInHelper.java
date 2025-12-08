package com.example.btms;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignInHelper {

    private static final int RC_SIGN_IN = 9001;
    private Activity activity;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    public GoogleSignInHelper(Activity activity, DatabaseHelper dbHelper, SharedPreferences sharedPreferences) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        this.sharedPreferences = sharedPreferences;
    }

    public void signIn() {
        try {
            // Configure Google Sign In
            // Note: In production, replace with your actual OAuth 2.0 Client ID from Google Cloud Console
            // For now, using default sign-in which may require setup
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, gso);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            // Fallback: Simulate Google sign in for demo purposes
            // In production, ensure Google Sign In is properly configured
            simulateGoogleSignIn();
        }
    }
    
    private void simulateGoogleSignIn() {
        // Demo fallback - creates a demo Google user
        String googleEmail = "demo.user@gmail.com";
        String googleName = "Demo Google User";

        long userId = dbHelper.registerOrLoginGoogle(googleName, googleEmail);
        
        if (userId > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_email", googleEmail);
            editor.putBoolean("is_logged_in", true);
            editor.putString("login_type", "google");
            editor.putString("user_name", googleName);
            editor.apply();
            
            Toast.makeText(activity, "Signed in with Google (Demo Mode)", Toast.LENGTH_SHORT).show();
            android.content.Intent intent = new android.content.Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public boolean handleSignInResult(int requestCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                
                if (account != null) {
                    String email = account.getEmail();
                    String name = account.getDisplayName();
                    
                    if (email != null && name != null) {
                        // Register or login user
                        long userId = dbHelper.registerOrLoginGoogle(name, email);
                        
                        if (userId > 0) {
                            // Save user info to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_email", email);
                            editor.putBoolean("is_logged_in", true);
                            editor.putString("login_type", "google");
                            editor.putString("user_name", name);
                            editor.apply();
                            
                            return true;
                        }
                    }
                }
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(activity, "Google Sign In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    public static int getSignInRequestCode() {
        return RC_SIGN_IN;
    }
}

