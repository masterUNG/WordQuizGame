package com.example.wordquizgame;

/**
 * Created by masterUNG on 12/29/15 AD.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class DrawView extends View {

    //กำรหนดรูปที่ต้องการจะ ย้าย
    public int[] object = new int[3];

    //กำหนดรูปที่ต้องการจะไป ทาบ
    public int[] target = new int[3];

    //กำหนดขนาดของ Object
    public int object_size;
    public int[][] object_position;
    public int[][] object_default_position;

    //จองหน่วยความจำของรูป
    public Bitmap[] object_bitmap = new Bitmap[object.length];

    public int[][] target_position;
    public Bitmap[] target_bitmap = new Bitmap[target.length];
    public boolean[] onTarget = new boolean[target.length];

    public int screen_width, screen_height;

    int object_id = -1;
    Context mContext;

    private int[] objectSourceInts, targetSourceInts;
    private int timesAnInt = 1;


    public DrawView(Context context, Display display) {
        super(context);
        mContext = context;

        //Assign Source of Image
        objectSourceInts = new int[12];
        objectSourceInts[0] = R.drawable.animals_cat;
        objectSourceInts[1] = R.drawable.animals_dog;
        objectSourceInts[2] = R.drawable.animals_dolphin;
        objectSourceInts[3] = R.drawable.body_arm;
        objectSourceInts[4] = R.drawable.body_ear;
        objectSourceInts[5] = R.drawable.body_eye;
        objectSourceInts[6] = R.drawable.body_foot;
        objectSourceInts[7] = R.drawable.body_hair;
        objectSourceInts[8] = R.drawable.body_hand;
        objectSourceInts[9] = R.drawable.body_mouth;
        objectSourceInts[10] = R.drawable.body_nose;
        objectSourceInts[11] = R.drawable.body_thumb;

        targetSourceInts = new int[12];
        targetSourceInts[0] = R.drawable.cat;
        targetSourceInts[1] = R.drawable.dog;
        targetSourceInts[2] = R.drawable.dolphin;
        targetSourceInts[3] = R.drawable.arm;
        targetSourceInts[4] = R.drawable.ear;
        targetSourceInts[5] = R.drawable.eye;
        targetSourceInts[6] = R.drawable.foot;
        targetSourceInts[7] = R.drawable.hair;
        targetSourceInts[8] = R.drawable.hand;
        targetSourceInts[9] = R.drawable.mouth;
        targetSourceInts[10] = R.drawable.nose;
        targetSourceInts[11] = R.drawable.thumb;

        //Random for Image
        randomForImage();

        screen_width = display.getWidth();
        screen_height = display.getHeight();
        object_size = screen_width / 5;
        for (int i = 0; i < object.length; i++) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), target[i]);
            target_bitmap[i] = Bitmap.createScaledBitmap(bm, object_size, object_size, false);
            bm = BitmapFactory.decodeResource(getResources(), object[i]);
            object_bitmap[i] = Bitmap.createScaledBitmap(bm, object_size, object_size, false);
            onTarget[i] = false;
        }

        target_position = new int[][]{{screen_width * 6 / 8, screen_height * 6 / 8}
                , {screen_width * 6 / 8, screen_height * 4 / 8}
                , {screen_width * 6 / 8, screen_height * 2 / 8}};

        object_default_position = new int[][]{{screen_width * 2 / 8, screen_height * 2 / 8}
                , {screen_width * 2 / 8, screen_height * 4 / 8}
                , {screen_width * 2 / 8, screen_height * 6 / 8}};

        object_position = new int[][]{{object_default_position[0][0], object_default_position[0][1]}
                , {object_default_position[1][0], object_default_position[1][1]}
                , {object_default_position[2][0], object_default_position[2][1]}};

    }   // DrawView



    private void randomForImage() {

        int[] intNumber = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        int[] intMyArrayRandom = new int[3];

        Random objRandom = new Random();

        for (int i = 0; i < 3; i++) {

            int intIndex = objRandom.nextInt(intNumber.length); // สุ่ม 1-12

            while (intNumber[intIndex] == 0) {

                intIndex = objRandom.nextInt(intNumber.length); // สุ่ม 1-12

            }   // while

            intMyArrayRandom[i] = intNumber[intIndex];
            intNumber[intIndex] = 0;

            object[i] = chooseImage(true, intMyArrayRandom[i]);
            target[i] = chooseImage(false, intMyArrayRandom[i]);

        }   // for

    }   // randomForImage


    private int chooseImage(boolean status, int intMyRandom) {

        int intImage = R.drawable.animals_dog;
        int intTatget;

        switch (intMyRandom) {

            case 1:
                intImage = R.drawable.animals_cat;
                intTatget = R.drawable.cat;
                break;
            case 2:
                intImage = R.drawable.animals_dog;
                intTatget = R.drawable.dog;
                break;
            case 3:
                intImage = R.drawable.animals_dolphin;
                intTatget = R.drawable.dolphin;
                break;
            case 4:
                intImage = R.drawable.body_arm;
                intTatget = R.drawable.arm;
                break;
            case 5:
                intImage = R.drawable.body_ear;
                intTatget = R.drawable.ear;
                break;
            case 6:
                intImage = R.drawable.body_eye;
                intTatget = R.drawable.eye;
                break;
            case 7:
                intImage = R.drawable.body_foot;
                intTatget = R.drawable.foot;
                break;
            case 8:
                intImage = R.drawable.body_hair;
                intTatget = R.drawable.hair;
                break;
            case 9:
                intImage = R.drawable.body_hand;
                intTatget = R.drawable.hand;
                break;
            case 10:
                intImage = R.drawable.body_mouth;
                intTatget = R.drawable.mouth;
                break;
            case 11:
                intImage = R.drawable.body_nose;
                intTatget = R.drawable.nose;
                break;
            case 12:
                intImage = R.drawable.body_thumb;
                intTatget = R.drawable.thumb;
                break;

            default:
                intImage = R.drawable.animals_cat;
                intTatget = R.drawable.cat;
                break;
        }   // switch

        if (status) {
            return intImage;
        } else {
            return intTatget;
        }


    }

    protected void onDraw(Canvas canvas) {

        Paint p = new Paint();
        p.setColor(Color.rgb(0x3e, 0x9c, 0xbc));
        p.setAntiAlias(true);
        for (int i = 0; i < target_position.length; i++) {
            canvas.drawBitmap(target_bitmap[i]
                    , target_position[i][0] - (object_size / 2)
                    , target_position[i][1] - (object_size / 2), p);
        }

        for (int i = 0; i < target_position.length; i++) {
            canvas.drawBitmap(object_bitmap[i]
                    , object_position[i][0] - (object_size / 2)
                    , object_position[i][1] - (object_size / 2), p);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventaction) {

            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on a object
                object_id = -1;
                for (int i = 0; i < object.length; i++) {
                    double radCircle = Math.sqrt(Math.pow(Math.abs(X - object_position[i][0]), 2)
                            + Math.pow(Math.abs(Y - object_position[i][1]), 2));
                    if (radCircle < object_bitmap[i].getWidth() / 2 && !onTarget[i]) {
                        object_id = i;
                    }
                }

            case MotionEvent.ACTION_MOVE:
                if (object_id != -1 && !onTarget[object_id]) {
                    object_position[object_id][0] = X;
                    object_position[object_id][1] = Y;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (object_id != -1 && !onTarget[object_id]) {

                    if (Math.abs(object_position[object_id][0] - target_position[object_id][0]) < object_size / 2
                            && Math.abs(object_position[object_id][1] - target_position[object_id][1]) < object_size / 2) {
                        object_position[object_id][0] = target_position[object_id][0];
                        object_position[object_id][1] = target_position[object_id][1];
                        onTarget[object_id] = true;

                        Log.d("game", "You Win");

                    }   // if 2

                    int count = 0;
                    for (int i = 0; i < object.length; i++) {
                        if (onTarget[i])
                            count++;
                    }   // for

                    if (count == object.length) {

                        timesAnInt += 1;

                        Toast.makeText(mContext, "ครั้งที่ " + Integer.toString(timesAnInt), Toast.LENGTH_SHORT).show();

                        reRunGame();

                        sentTimes(timesAnInt);

                    }   // if


                    Log.d("game", "count ==> " + count);
                    Log.d("game", "object.length ==> " + object.length);

                }   // if 1


                break;
        }

        invalidate();

        return true;
    }

    private void sentTimes(int timesAnInt) {



    }   // sentTimes

    private void reRunGame() {

        Handler objHandler = new Handler();
        objHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();

            }
        }, 3000);


    }   // reRunGame




    public void reset() {
        for (int i = 0; i < object.length; i++)
            onTarget[i] = false;

        object_position = new int[][]{{object_default_position[0][0], object_default_position[0][1]}
                , {object_default_position[1][0], object_default_position[1][1]}
                , {object_default_position[2][0], object_default_position[2][1]}};



        invalidate();
    }
}