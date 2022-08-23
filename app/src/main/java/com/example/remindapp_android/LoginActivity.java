package com.example.remindapp_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindapp_android.Admin.AdminHome;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout et_Password, et_Email;
    Button btn_login;
    ImageView btn_googleLogin;
    TextView tv_signUp, tv_forgotPassword;
    String password, email;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore myDatabase;
    GoogleSignInClient gsc;
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    SignInCredential credential;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createRequest();

        btn_login = findViewById(R.id.btn_login);
        btn_googleLogin = findViewById(R.id.btn_googleLogin);
        tv_signUp = findViewById(R.id.tV_SignUp);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        mAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        et_Email = (TextInputLayout) findViewById(R.id.et_Email);
        et_Password = (TextInputLayout) findViewById(R.id.et_Password);
        // email = et_Email.getEditText().getText().toString();

//        gsc = GoogleSignIn.getClient(this, gso);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.mapkey))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btn_googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleLogin();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_Email.getEditText().getText().toString();
                password = et_Password.getEditText().getText().toString();
//                CheckUser();
                LoginUser();
            }
        });

        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidateEmail()) {
                    return;
                }
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LoginActivity.this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);
    }

    private void GoogleLogin() {
//        String idToken = credential.getGoogleIdToken();
//        if (idToken !=  null) {
//            // Got an ID token from Google. Use it to authenticate
//            // with Firebase.
//            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
//            mAuth.signInWithCredential(firebaseCredential)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                // Sign in success, update UI with the signed-in user's information
//                                Toast.makeText(LoginActivity.this, "Google SignIn success", Toast.LENGTH_SHORT).show();
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                startActivity(intent);
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Toast.makeText(LoginActivity.this, "Google SignIn failure" + task.getException(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }

        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            if (user.getEmail().equals("admin@remind.com")) {
                Intent intent = new Intent(LoginActivity.this, AdminHome.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Google SignIn failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //    @Override
//    protected void onStart() {
//        super.onStart();
//        if (user != null) {
//            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(intent);
//        }
//    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (user != null) {
//            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(intent);
//        }
//    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(intent);
//        }
//    }

    private Boolean ValidateEmail() {
//        email = et_Email.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            et_Email.setError("Email is Required");
            return false;
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            et_Email.setError("Enter Valid email");
            return false;
        } else {
            et_Email.setError(null);
            et_Email.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean ValidatePassword() {

//        password = et_Password.getEditText().getText().toString();

        if (TextUtils.isEmpty(password)) {
            et_Password.setError("Password is Required");
            return false;
        } else if (password.length() < 6) {
            et_Password.setError("Password should be more than six");
            return false;
        } else {
            et_Password.setError(null);
            et_Password.setErrorEnabled(false);
            return true;
        }
    }

    private void LoginUser() {
        if (!ValidateEmail() | !ValidatePassword()) {
            return;
        }
//        if (email.equals("admin@remind.com") ){
//            Toast.makeText(LoginActivity.this, "Admin Logged In Successfully", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(LoginActivity.this, AdminHome.class);
//            startActivity(intent);
//        } else {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    myDatabase.collection("users").document(task.getResult().getUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value.get("email").equals("admin@remind.com")){
                                Toast.makeText(LoginActivity.this, "Admin Logged In Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, AdminHome.class);
                                startActivity(intent);
                            }else{
//                                    Toast.makeText(LoginActivity.this, value.get("name") + " Logged In Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

//                    if (task.getResult().getUser().getEmail().toString() == "admin@remind.com"){
//                            Toast.makeText(LoginActivity.this, "Admin Logged In Successfully", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LoginActivity.this, AdminHome.class);
//                            startActivity(intent);
//                    }else{
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                        startActivity(intent);
//                    }
//                    CheckUser();
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "LogIn Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//            private void CheckUser () {
//                if (email == "admin@remind.com" && password == "Admin@123"){
//                    Toast.makeText(LoginActivity.this, "Admin Logged In Successfully", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                } else {
////                    LoginUser();
////                    FirebaseUser user = mAuth.getCurrentUser();
////                    Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
////                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
////                    startActivity(intent);
//                }
//            }
}