package com.aiplatform.aiplatform;

import static com.aiplatform.aiplatform.display.colorlist;
import static com.aiplatform.aiplatform.display.pathList;
import static com.aiplatform.aiplatform.display.current_brush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.List;

public class DrawingBoard extends AppCompatActivity {
    public static Path path=new Path();
    public static Paint paint_brush=new Paint();
    Button submit;
    LinearLayout screenshot;
    String intextra;
    MediaPlayer mpCorrect;
    int flag=0;
    MediaPlayer mpWrong;
    int random_int;
    int count=0;
    int scoreCount=0;

    DatabaseReference time;

    FirebaseAuth mAuth;
    public FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

    TextView questionTextview,score;
    private DigitClassifier digitClassifier = new DigitClassifier((Context)this);
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drawing_board);

        screenshot=findViewById(R.id.screenshot);
        questionTextview=findViewById(R.id.question);

        mAuth=FirebaseAuth.getInstance();

       mpCorrect=MediaPlayer.create(this,R.raw.correct);
       mpWrong=MediaPlayer.create(this,R.raw.wrong);

       score=findViewById(R.id.score);


        Intent intent=getIntent();
        intextra=intent.getStringExtra("levelname");
     submit=findViewById(R.id.submit);

        pathList.clear();
        colorlist.clear();
        path.reset();

        switch (intextra)
        {
            case "1":
                digitQuestions();
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  Toast.makeText(getApplicationContext(),"SEND REQUEST",Toast.LENGTH_SHORT).show();

                        if(count<5)
                        {
                            classifyDrawing();
                            count++;
                        }
                        else
                        {

                            if(scoreCount<3)
                            {
                                finish();
                                finish();
                                Toast.makeText(getApplicationContext(),"Revise the activity",Toast.LENGTH_LONG).show();
                                Intent iC=new Intent(DrawingBoard.this,CompletionActivity.class);
                                iC.putExtra("result",0);
                                startActivity(iC);

                            }
                            else
                            {
                                time=firebaseDatabase.getReference().child("Users").child(mAuth.getUid().toString()).child("level");
                                time.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String c=snapshot.getValue(String.class);
                                        int a= Integer.parseInt(c);
                                        if(Integer.parseInt(intextra)==a)
                                        {
                                            if (flag == 0) {

                                                a += 1;
                                                time.setValue(String.valueOf(a));
                                                flag = 1;
                                                Intent i=new Intent(DrawingBoard.this,Levels.class);
                                                startActivity(i);
                                                finish();
                                               // Toast.makeText(getApplicationContext(),"CONGRATULATIONS !! YOU HAVE COMPLETED LEVEL",Toast.LENGTH_LONG).show();
                                               /* Dialog dialog;
                                                View view=getLayoutInflater().inflate(R.layout.full_screen,null);
                                                dialog=new Dialog(getApplicationContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                                                dialog.setContentView(view);
                                                dialog.show();

                                                */
                                                Intent iC=new Intent(DrawingBoard.this,CompletionActivity.class);
                                                iC.putExtra("result",1);
                                                startActivity(iC);

                                            }

                                        }

                                        else
                                        {
                                          //  Toast.makeText(getApplicationContext(),"YOU SHOULD TRY SOLVING NEW PROBLEMS",Toast.LENGTH_LONG).show();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                //Toast.makeText(getApplicationContext(),"CONGRATULATIONS !! YOU HAVE COMPLETED LEVEL",Toast.LENGTH_LONG).show();
                               // Intent i=new Intent(DrawingBoard.this,Levels.class);
                              //  i.putExtra("levelname",a);
                             //   startActivity(i);

                            }
                        }
                    }
                });


                break;

            case "2":


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shape();
                    }
                });


                break;
        }


        this.digitClassifier.initialize().addOnFailureListener((OnFailureListener)null);


    }


    void digitQuestions()
    {
        int min = 0; // Minimum value of range
        int max = 8; // Maximum value of range
        random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);

        int arr[]=new int[6];
        if(arr.length==0)
        {
            questionTextview.setText(String.valueOf(random_int));
        }
        else
        {
            int flag=0;
            for(int i=0;i<arr.length;i++)
            {
                if(arr[i]==random_int)
                {
                    flag=1;
                }

            }

            if (flag==1)
            {
                digitQuestions();
            }
            else
            {
                questionTextview.setText(String.valueOf(random_int));
            }

        }


    }

    protected void onDestroy() {
        this.digitClassifier.close();
        super.onDestroy();
    }
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // Get the device's sensor orientation.
        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
        }
        return rotationCompensation;
    }

    public void shape()
    {
        screenshot.setDrawingCacheEnabled(true);
        screenshot.buildDrawingCache();
        Bitmap bitmap = screenshot.getDrawingCache();

        InputImage image = InputImage.fromBitmap(bitmap,0);


        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        // ...
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            int index = label.getIndex();

                            Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                });

    }
   public final void classifyDrawing() {
        //DrawView var10000 = this.drawView;
        screenshot.setDrawingCacheEnabled(true);


        screenshot.buildDrawingCache();

        Bitmap bitmap = screenshot.getDrawingCache();
        if (bitmap != null && this.digitClassifier.isInitialized()) {
            this.digitClassifier.classifyAsync(bitmap).addOnSuccessListener((OnSuccessListener)(new OnSuccessListener() {
                // $FF: synthetic method
                // $FF: bridge method
                public void onSuccess(Object var1) {
                    this.onSuccess((String)var1);
                   // Toast.makeText(getApplicationContext(),(String)var1,Toast.LENGTH_LONG).show();
                }

                public final void onSuccess(String resultText) {
          Toast.makeText(getApplicationContext(),String.valueOf(resultText.charAt(19)),Toast.LENGTH_LONG).show();
                    String check= questionTextview.getText().toString();
                    if(check.charAt(0)==resultText.charAt(19))
                    {
                        scoreCount++;
                        score.setText("YOUR SCORE : "+scoreCount);
                        mpCorrect.start();
                        pathList.clear();
                        colorlist.clear();
                        path.reset();
                        digitQuestions();
                    }
                    else
                    {
                        mpWrong.start();
                    }
                }
            })).addOnFailureListener((OnFailureListener)(new OnFailureListener() {
                public final void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                  //  TextView var10000 = MainActivity.this.predictedTextView;
                   /* if (var10000 != null) {
                        MainActivity var10001 = MainActivity.this;
                        Object[] var10003 = new Object[1];
                        Intrinsics.checkNotNullExpressionValue(e, "e");
                        var10003[0] = e.getLocalizedMessage();
                        var10000.setText((CharSequence)var10001.getString(1900024, var10003));
                    }

                    */

                    Log.e("MainActivity", "Error classifying drawing.", (Throwable)e);
                }
            }));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mpCorrect.release();
        mpWrong.release();
    }

    public void eraser(View view)
    {
        pathList.clear();
        colorlist.clear();
        path.reset();
    }

    public void pencil(View view)
    {
        paint_brush.setColor(Color.YELLOW);
        currentColor(paint_brush.getColor());
    }
    public void redColor(View view)
    {
        paint_brush.setColor(Color.YELLOW);
        currentColor(paint_brush.getColor());
    }

    public void yellowColor(View view)
    {
        paint_brush.setColor(Color.YELLOW);
        currentColor(paint_brush.getColor());
    }
    public void greenColor(View view)
    {
        paint_brush.setColor(Color.YELLOW);
        currentColor(paint_brush.getColor());
    }
    public void magentaColorColor(View view)
    {
        paint_brush.setColor(Color.MAGENTA);
        currentColor(paint_brush.getColor());
    }

    public void currentColor(int c)
    {

        current_brush=c;
        path=new Path();
    }
}