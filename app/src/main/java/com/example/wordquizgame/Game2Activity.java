package com.example.wordquizgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Game2Activity extends Activity {
    DrawView dv;
    LinearLayout scene;

    private TextView timesTextView;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);



        scene = (LinearLayout)findViewById(R.id.scene);
        dv = new DrawView(Game2Activity.this, getWindowManager().getDefaultDisplay());
        draw();

        Button buttonReset = (Button)findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              //  dv.reset();

                Intent objIntent = new Intent(Game2Activity.this, Game2Activity.class);
                objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(objIntent);


            }   // event
        });




    }   // Main Method





    public void draw() {
        try {
            scene.removeView(dv);
        } catch (Exception e) { }
        scene.addView(dv);
    }

    public void removeView() {
        try {
            scene.removeView(dv);
        } catch (Exception e) { }
    }



}
