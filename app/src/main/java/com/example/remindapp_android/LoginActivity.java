package com.example.remindapp_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout et_Password, et_Email;
    Button btn_login;
    TextView tv_signUp,tv_forgotPassword;
    String password,email;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setBackgroundColor(Color.parseColor("#0000ff"));
        tv_signUp = findViewById(R.id.tV_SignUp);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        et_Email = (TextInputLayout) findViewById(R.id.et_Email);
        et_Password = (TextInputLayout) findViewById(R.id.et_Password);

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ValidateEmail()){
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
        email = et_Email.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            et_Email.setError("Email is Required");
            return false;
        }else if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            et_Email.setError("Enter Valid email");
            return false;
        }else{
            et_Email.setError(null);
            et_Email.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean ValidatePassword() {

        password = et_Password.getEditText().getText().toString();

        if (TextUtils.isEmpty(password)) {
            et_Password.setError("Password is Required");
            return false;
        }else if(password.length() < 6){
            et_Password.setError("Password should be more than six");
            return  false;
        }
        else{
            et_Password.setError(null);
            et_Password.setErrorEnabled(false);
            return true;
        }
    }

    private void LoginUser() {
        if(!ValidateEmail()  | !ValidatePassword()){
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "LogIn Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}