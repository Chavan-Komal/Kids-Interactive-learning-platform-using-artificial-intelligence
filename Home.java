package com.aiplatform.aiplatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView logout,nameUser,level,firstname;
    Button start;
     MediaPlayer homeMusic;
public FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
Button puzzle,magicup,memorygame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        level=findViewById(R.id.level);
        nameUser=findViewById(R.id.nameofuser);
        firstname=findViewById(R.id.firstname);
        mAuth=FirebaseAuth.getInstance();
        final MediaPlayer mpClick= MediaPlayer.create(this,R.raw.click);
        homeMusic=MediaPlayer.create(this,R.raw.homemusic);
        homeMusic.start();
        DatabaseReference nameofuser=firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("name");

        nameofuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String msg=dataSnapshot.getValue(String.class);
                nameUser.setText(msg.toUpperCase());
                firstname.setText(msg.substring(0,msg.indexOf(" ")));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference levelofuser=firebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("level");

        levelofuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String msg=dataSnapshot.getValue(String.class);
                level.setText("LEVEL "+msg.toUpperCase());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpClick.start();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Home.this, Login.class)); //Go back to home page
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                homeMusic.pause();
            }
        });


        start=findViewById(R.id.start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(Home.this,Levels.class);
             //   Intent i=new Intent(Home.this,Levels.class);
                startActivity(i);
            //   overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                mpClick.start();
                homeMusic.pause();


            }
        });

        puzzle=findViewById(R.id.puzzle);
        puzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Home.this,GameView.class);
                i.putExtra("weburl","1");
                mpClick.start();
                startActivity(i);
                homeMusic.pause();
            }
        });

        magicup=findViewById(R.id.magiccup);
        magicup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Home.this,GameView.class);
                i.putExtra("weburl","2");
                mpClick.start();
                startActivity(i);
                homeMusic.pause();
            }
        });

        memorygame=findViewById(R.id.memorygame);
        memorygame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Home.this,GameView.class);
                i.putExtra("weburl","3");
                mpClick.start();
                startActivity(i);
                homeMusic.pause();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        homeMusic.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        homeMusic.start();
    }
}