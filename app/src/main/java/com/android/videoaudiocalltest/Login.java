package com.android.videoaudiocalltest;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.videoaudiocalltest.Models.User;
import com.android.videoaudiocalltest.R;
import com.android.videoaudiocalltest.Util.CheckInternet;
import com.android.videoaudiocalltest.Util.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    //widgets
    private EditText loginEmail, loginPassword;
    private TextView resetPassword;
    private RelativeLayout loginButton;
    private TextView loginText;
    private ProgressBar loginProgress;
    private ImageView loginPasswordVisibility;

    //firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;

    //control values
    private boolean isPasswordVisible = false;

    //local
    private String localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //local user
        localUser = Paper.book().read(Common.USER_ID);


        //widgets
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        resetPassword = findViewById(R.id.resetPassword);
        loginButton = findViewById(R.id.loginButton);
        loginText = findViewById(R.id.loginText);
        loginProgress = findViewById(R.id.loginProgress);
        loginPasswordVisibility = findViewById(R.id.loginPasswordVisibility);


        //check local user
        if (localUser != null && !TextUtils.isEmpty(localUser)){

            Intent homeIntent = new Intent(Login.this, Home.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();

        }


        //password visibility
        loginPasswordVisibility.setOnClickListener(v -> {

            if (!isPasswordVisible){

                isPasswordVisible = true;
                loginPassword.setTransformationMethod(null);
                loginPasswordVisibility.setImageResource(R.drawable.ic_hide_password);

            } else {

                isPasswordVisible = false;
                loginPasswordVisibility.setImageResource(R.drawable.ic_show_password);
                loginPassword.setTransformationMethod(new PasswordTransformationMethod());

            }

        });


        //login
        loginButton.setOnClickListener(view -> validateParams());

        //reset password
        resetPassword.setOnClickListener(view -> resetThePassword());

    }

    private void resetThePassword() {

        //extract string
        String theEmail = loginEmail.getText().toString().trim();

        if (TextUtils.isEmpty(theEmail) || !isValidEmail(theEmail)){

            loginEmail.requestFocus();
            loginEmail.setError("Invalid");

        } else {

            //run network check
            new CheckInternet(Login.this, output -> {

                if (output == 1){

                    mAuth.sendPasswordResetEmail(theEmail)
                            .addOnCompleteListener(task -> {

                               if (task.isSuccessful()){

                                   Toast.makeText(this, "Reset link sent to your mail", Toast.LENGTH_LONG).show();

                               } else {

                                   Toast.makeText(this, "Error occurred", Toast.LENGTH_LONG).show();

                               }

                            });

                }

                if (output == 1){

                    Toast.makeText(this, "No internet access", Toast.LENGTH_SHORT).show();

                }

                if (output == 1){

                    Toast.makeText(this, "No network access", Toast.LENGTH_LONG).show();

                }

            }).execute();

        }

    }

    private void validateParams() {

        //lock widget
        loginButton.setEnabled(false);
        resetPassword.setEnabled(false);

        //extract strings
        String theEmail = loginEmail.getText().toString().trim();
        String thePass = loginPassword.getText().toString().trim();

        //check
        if (TextUtils.isEmpty(theEmail) || !isValidEmail(theEmail)){

            loginEmail.requestFocus();
            loginEmail.setError("Invalid");

        } else

        if (TextUtils.isEmpty(thePass)){

            loginPassword.requestFocus();
            loginPassword.setError("Required");

        } else {

            //run network check
            new CheckInternet(Login.this, output -> {

                if (output == 1){

                    //loading
                    loginText.setVisibility(View.GONE);
                    loginProgress.setVisibility(View.VISIBLE);

                    //login
                    mAuth.signInWithEmailAndPassword(theEmail, thePass)
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful()){

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    handleSignIn(user);

                                } else {

                                    //stop loading
                                    loginProgress.setVisibility(View.GONE);
                                    loginText.setVisibility(View.VISIBLE);

                                    //unlock widget
                                    loginButton.setEnabled(true);
                                    resetPassword.setEnabled(true);

                                    //error
                                    Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();

                                }

                            });

                } else

                if (output == 0){

                    //unlock widget
                    loginButton.setEnabled(true);
                    resetPassword.setEnabled(true);

                    //error
                    Toast.makeText(this, "No internet access", Toast.LENGTH_SHORT).show();

                } else

                if (output == 2){

                    //unlock widget
                    loginButton.setEnabled(true);
                    resetPassword.setEnabled(true);

                    //error
                    Toast.makeText(this, "No network access", Toast.LENGTH_LONG).show();

                }

            }).execute();

        }

    }

    private void handleSignIn(FirebaseUser user) {

        //get string
        String currentUid = user.getUid();
        String currentEmail = user.getEmail();

        //check db for data existence
        userRef = db.collection(Common.USER_NODE)
                .document(currentUid);
        userRef.get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        if (task.getResult().exists()){

                            //get user data
                            User theUser = task.getResult().toObject(User.class);
                            updateUI(theUser);

                        } else {

                            showProfileDialog(currentUid, currentEmail);

                        }

                    }

                });

    }

    //validate email
    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void showProfileDialog(final String currentUid, final String currentEmail) {

        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.new_user_layout,null);

        final EditText userFirstName = viewOptions.findViewById(R.id.userFirstName);
        final EditText userLastName = viewOptions.findViewById(R.id.userLastName);
        final RelativeLayout cancelButton = viewOptions.findViewById(R.id.cancelButton);
        final RelativeLayout setButton = viewOptions.findViewById(R.id.setButton);
        final TextView setText = viewOptions.findViewById(R.id.setText);
        final ProgressBar setProgress = viewOptions.findViewById(R.id.setProgress);

        //set dialog properties
        alertDialog.setView(viewOptions);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
        layoutParams.y = 150; // bottom margin
        alertDialog.getWindow().setAttributes(layoutParams);


        //cancel
        cancelButton.setOnClickListener(view -> {

            //sign out
            mAuth.signOut();

            //dismiss dialog
            alertDialog.dismiss();

        });

        alertDialog.setOnCancelListener(dialogInterface -> {

            //sign out
            mAuth.signOut();

            //dismiss dialog
            alertDialog.dismiss();

        });

        //set user
        setButton.setOnClickListener(view -> {

            //lock dialog
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);

            //extract strings
            String firstName = userFirstName.getText().toString().trim();
            String lastName = userLastName.getText().toString().trim();

            //validate
            if (TextUtils.isEmpty(firstName)){

                userFirstName.requestFocus();
                userFirstName.setError("Required");

                //unlock dialog
                alertDialog.setCancelable(true);
                alertDialog.setCanceledOnTouchOutside(true);

            } else

            if (TextUtils.isEmpty(lastName)){

                userLastName.requestFocus();
                userLastName.setError("Required");

                //unlock dialog
                alertDialog.setCancelable(true);
                alertDialog.setCanceledOnTouchOutside(true);

            } else {

                //run network check
                new CheckInternet(Login.this, output -> {

                    if (output == 1){

                        //loading
                        setText.setVisibility(View.GONE);
                        setProgress.setVisibility(View.VISIBLE);

                        //create user model
                        User newUser = new User(currentUid, currentEmail, firstName, lastName, "");
                        userRef = db.collection(Common.USER_NODE).document(currentUid);
                        userRef.set(newUser)
                                .addOnCompleteListener(task -> {

                                    if (task.isSuccessful()){

                                        //dismiss dialog
                                        alertDialog.dismiss();

                                        //update UI
                                        updateUI(newUser);

                                    } else {

                                        //unlock dialog
                                        alertDialog.setCancelable(true);
                                        alertDialog.setCanceledOnTouchOutside(true);

                                        //stop loading
                                        setProgress.setVisibility(View.GONE);
                                        setText.setVisibility(View.VISIBLE);

                                        //error
                                        Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show();

                                    }

                                });

                    } else

                    if (output == 0){

                        //unlock dialog
                        alertDialog.setCancelable(true);
                        alertDialog.setCanceledOnTouchOutside(true);

                        //error
                        Toast.makeText(this, "No internet access", Toast.LENGTH_SHORT).show();

                    } else

                    if (output == 2){

                        //unlock dialog
                        alertDialog.setCancelable(true);
                        alertDialog.setCanceledOnTouchOutside(true);

                        //error
                        Toast.makeText(this, "No network access", Toast.LENGTH_LONG).show();

                    }

                }).execute();

            }

        });


        //show dialog
        alertDialog.show();
    }

    private void updateUI(User theUser) {

        //check
        if (theUser != null){

            //write to local db
            Paper.book().write(Common.USER_ID, theUser.getUser_id());
            Paper.book().write(Common.PAPER_USER, theUser);

            Intent homeIntent = new Intent(Login.this, Home.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            overridePendingTransition(R.anim.slide_left, R.anim.fade_out);
            finish();

        } else {

            //sign out
            mAuth.signOut();

        }

    }
}
