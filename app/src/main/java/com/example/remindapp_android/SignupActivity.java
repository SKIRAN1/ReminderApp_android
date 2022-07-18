package com.example.remindapp_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    Button btn_signUp;
    TextView tv_signIn;
    TextInputLayout et_userPassword, et_userEmail,et_userName,et_userPhone,et_userConfirmPassword;
    String name,email,phone,password,confirmPassword;
    FirebaseFirestore myDatabase;

    FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_signUp = findViewById(R.id.btn_signUp);
        tv_signIn = findViewById(R.id.tV_SignIn);
        mAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();
        loadingBar = new ProgressDialog(this);
        et_userEmail = (TextInputLayout)findViewById(R.id.et_userEmail);
        et_userName = (TextInputLayout) findViewById(R.id.et_userName);
        et_userPassword = (TextInputLayout) findViewById(R.id.et_userPassword);
        et_userPhone = (TextInputLayout) findViewById(R.id.et_userPhone);
        et_userConfirmPassword = (TextInputLayout) findViewById(R.id.et_userConfirmPassword);

        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
    }
    private Boolean ValidateName() {

        name = et_userName.getEditText().getText().toString();

        if (TextUtils.isEmpty(name)) {
            et_userName.setError("Name is Required");
            return false;
        }else{
            et_userName.setError(null);
            et_userName.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean ValidateEmail() {
        email = et_userEmail.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            et_userEmail.setError("Email is Required");
            return false;
        }else if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            et_userEmail.setError("Enter Valid email");
            return false;
        }else{
            et_userEmail.setError(null);
            et_userEmail.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean ValidatePhone() {

        phone = et_userPhone.getEditText().getText().toString();

        if (TextUtils.isEmpty(phone)) {
            et_userPhone.setError("Phone Number is Required");
            return false;
        }else if(phone.length() < 10){
            et_userPhone.setError("PhoneNumber is not valid");
            return  false;
        }
        else{
            et_userPhone.setError(null);
            et_userPhone.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean ValidatePassword() {
        password = et_userPassword.getEditText().getText().toString();
        if (TextUtils.isEmpty(password)) {
            et_userPassword.setError("Password is Required");
            return false;
        }else if(password.length() < 8){
            et_userPassword.setError("Password min 8 characters!");
            return false;
            // for Uppercase
        }else if( !password.matches("(.*[A-Z].*)")){
            et_userPassword.setError("Password at least one uppercase ");
            return false;
            //for number
        }else if( !password.matches("(.*[0-9].*)")) {
            et_userPassword.setError("Password at least one number ");
            return false;
        }
        //  for special Character
        else if(!password.matches("(.*[!@#$&*].*)")){
            et_userPassword.setError("Password at least one special character");
            return false;
        }
        else{
            et_userPassword.setError(null);
            et_userPassword.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean confirmPassword() {
        confirmPassword = et_userConfirmPassword.getEditText().getText().toString();
        if (password.equals(confirmPassword)) {
            et_userPassword.setError(null);
            et_userPassword.setErrorEnabled(false);
            return true;
        }
        else{
            et_userConfirmPassword.setError("Password doesn't match");
            return false;
        }
    }

    private void validation() {
        if(!ValidateName() | !ValidateEmail()  | !ValidatePassword() | !ValidatePhone() | !confirmPassword()){
            return;
        }
        loadingBar.setTitle("Creating Account");
        loadingBar.setMessage("Please wait until we are checking credentials");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        createAccount(name,phone,email,password);
    }

    private void createAccount(String name, String phone, String email, String password) {

        HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("email", email);
        userdataMap.put("phone", phone);
        userdataMap.put("name", name);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                myDatabase.collection("users").document(mAuth.getCurrentUser().getUid()).set(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Congratulations your account is created", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else {
                            loadingBar.dismiss();
                            Toast.makeText(SignupActivity.this, "Error, Please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

//        myDatabase.collection("users").add(userdataMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//                            Toast.makeText(SignupActivity.this, "Congratulations your account is created", Toast.LENGTH_SHORT).show();
//                            loadingBar.dismiss();
//                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                        }else {
//                            loadingBar.dismiss();
//                            Toast.makeText(SignupActivity.this, "Error, Please try again!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
    }
}