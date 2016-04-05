package com.example.wordquizgame;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;
import android.widget.Button;
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

        //Create Button
        createButton(4);

    }   // Main Method

    private void createButton(int intButton) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < intButton; i++) {

            Button button = new Button(this);
            button.setId(i + 1);
            button.setText(Integer.toString(i+1));

            linearLayout.addView(button);

        }   //for

    }   // createButton

    private void clearAnswer() {

        textView.setText("");

    }   // clearAnswer

    private void showQuestionImage(int intIndex) {

        imageView.setImageResource(questionImageInts[intIndex]);

    }   // showQuestion

}   // Main Class
