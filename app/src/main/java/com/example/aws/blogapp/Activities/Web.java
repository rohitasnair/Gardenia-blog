package com.example.aws.blogapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aws.blogapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Web extends AppCompatActivity {
    private boolean isChecked = false;
    Button poke;
    ImageView instagram;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darktheam);
        }else
            setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        ImageView navUserPhot = findViewById(R.id.imageView2);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);
        TextView navUsername = findViewById(R.id.textView2);
        TextView navUserMail = findViewById(R.id.textView5);
        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());







    }

}

