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
    private int timesAnInt = 0, countAnInt = 0;
    private StringBuilder stringBuilder;

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
        stringBuilder = new StringBuilder();

        //Clear Answer
        clearAnswer();

        //Show Question Image
        showQuestionImage(0);


        //Create Button
        createButton(0);

    }   // Main Method

    private void createButton(int intIndex) {

        final char[] answerChars = chooseStrings[intIndex].toCharArray();

        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        for (int i = 0; i < answerChars.length; i++) {

            final Button button = new Button(this);
            button.setId(i);
            button.setText(String.valueOf(answerChars[i]));

            button.setLayoutParams(layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d("5April", "You Click Label = " + answerChars[view.getId()]);
                    addTextToTextView(String.valueOf(answerChars[view.getId()]));

                    button.setVisibility(View.INVISIBLE);


                    timesAnInt += 1;
                    if (timesAnInt >= answerChars.length) {
                        checkAnswer();
                    }


                }   // onClick
            });


            linearLayout.addView(button);

        }   //for

    }   // createButton

    private void checkAnswer() {

        if (answerStrings[countAnInt].equals(textView.getText().toString())) {
            countAnInt += 1;
        }

        clearAnswer();
        showQuestionImage(countAnInt);
        createButton(countAnInt);

    }   // checkAnswer

    private void addTextToTextView(String strAdd) {

        Log.d("5April", "ค่่าที่ได้ strAdd ==> " + strAdd);

        stringBuilder.append(strAdd);

        Log.d("5April", "stringBuild ==> ที่สระสมได้ " + stringBuilder.toString());

        showTextString = stringBuilder.toString();

        Log.d("5April",
                "่ค่าของ showTextString ที่รับได้ ==> " + showTextString);

        forSetText(showTextString);


    }   // addText

    private void forSetText(String showTextString) {
        textView.setText(showTextString);
    }

    private void clearAnswer() {

        timesAnInt = 0;
        textView.setText("");

        stringBuilder.setLength(0);
        Log.d("5April", "stringBuild ตอน Clear ==> " + stringBuilder.toString());
        linearLayout.removeAllViews();

    }   // clearAnswer

    private void showQuestionImage(int intIndex) {

        imageView.setImageResource(questionImageInts[intIndex]);

    }   // showQuestion

}   // Main Class
