package com.example.wordquizgame;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
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
    private String[] answerStrings, chooseStrings;
    private String showTextString = "";

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
        answerStrings = myDataImage.answerStrings;
        chooseStrings = myDataImage.chooseStrings;

        //Show Question Image
        showQuestionImage(0);

        //Clear Answer
        clearAnswer();

        //Create Button
        createButton(2);

    }   // Main Method

    private void createButton(int intIndex) {

        final char[] answerChars = chooseStrings[intIndex].toCharArray();


        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < answerChars.length; i++) {

            Button button = new Button(this);
            button.setId(i);
            button.setText(String.valueOf(answerChars[i]));

            button.setLayoutParams(layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d("5April", "You Click Label = " + answerChars[view.getId()]);
                    addTextToTextView(String.valueOf(answerChars[view.getId()]));

                }   // onClick
            });





            linearLayout.addView(button);

        }   //for

    }   // createButton

    private void addTextToTextView(String strAdd) {

        showTextString = showTextString + strAdd;
        showTextString.trim();

        textView.setText(showTextString);

    }   // addText

    private void clearAnswer() {

        textView.setText("");

    }   // clearAnswer

    private void showQuestionImage(int intIndex) {

        imageView.setImageResource(questionImageInts[intIndex]);

    }   // showQuestion

}   // Main Class
