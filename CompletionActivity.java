package com.aiplatform.aiplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class CompletionActivity extends AppCompatActivity {
Button finishbutton,reviselevel;
int intextra;
LinearLayout happyLayout,fail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_completion);


       // mpHappy.release();
        Intent intent=getIntent();
        intextra=intent.getIntExtra("result",0);
        finishbutton=findViewById(R.id.finishhappy);
        happyLayout=findViewById(R.id.happylayout);
        fail=findViewById(R.id.fail);
        reviselevel=findViewById(R.id.reviselevel);

        switch (intextra)
        {
            case 0:
                happyLayout.setVisibility(View.GONE);
                fail.setVisibility(View.VISIBLE);
                final MediaPlayer mpFail= MediaPlayer.create(this,R.raw.fail);
                mpFail.start();
                mpFail.setLooping(false);
                reviselevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mpFail.release();
                        finish();
                    }
                });


                break;
            case 1:
                happyLayout.setVisibility(View.VISIBLE);
                fail.setVisibility(View.GONE);
                final MediaPlayer mpHappy= MediaPlayer.create(this,R.raw.happy);
                mpHappy.start();
                mpHappy.setLooping(true);
                finishbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mpHappy.release();
                        finish();
                    }
                });
                break;
        }

    }
}