package com.example.wordquizgame;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Game3Activity extends ActionBarActivity {

    //Explicit
    private ImageView imageView;
    private TextView textView;
    private LinearLayout linearLayout;
    private int[] questionImageInts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3);

        //Bind Widget
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView3);
        linearLayout = (LinearLayout) findViewById(R.id.linButton);

        //Get Data
        MyDataImage myDataImage = new MyDataImage();
        questionImageInts = myDataImage.questionImageInts;

        //Show Question Image
        showQuestionImage(0);

        //Clear Answer
        clearAnswer();


    }   // Main Method

    private void clearAnswer() {

        textView.setText("");

    }   // clearAnswer

    private void showQuestionImage(int intIndex) {

        imageView.setImageResource(questionImageInts[intIndex]);

    }   // showQuestion

}   // Main Class
