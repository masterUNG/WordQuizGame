package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wordquizgame.db.DatabaseHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Set;


public class GameActivity extends ActionBarActivity {

    protected static final String DIFFICULTY = "difficulty";
    private static final String TAG = "GameActivity";

    private static final int TOTAL_QUESTIONS = 3;   // จำนวนคำถามใน 1 เกม
    private static final int COLUMNS_PER_ROW = 2;   // จำนวนปุ่มคำศัพท์ใน 1 แถว

    private ArrayList<String> mFileNameList;        // ชื่อไฟล์ภาพทั้งหมดในหมวดคำศัพท์ที่ถูกเลือก
    private ArrayList<String> mQuizWordsList;       // คำศัพท์ทั้ง 10 คำที่เป็นคำถาม
    private ArrayList<String> mChoiceWords;         // เก็บคำศัพท์ที่เป็นตัวเลือก ซึ่งหนึ่งในนั้นคือคำตอบ

    private String mAnswerFileName;                 // ชื่อไฟล์ภาพที่เป็นคำตอบของคำถามข้อปัจจุบัน
    private int mTotalGuesses;                      // จำนวนครั้งที่ทายคำตอบ (จำนวนครั้งที่คลิกปุ่มคำศัพท์)
    private int mScore;                             // คะแนน (จำนวนครั้งที่ตอบถูก)

    private int mNumChoices;                        // จำนวนตัวเลือก
    //private String mSelectedCase;                   // รูปแบบพยัญชนะที่ผู้ใช้เลือก (เล็ก, ใหญ่, สุ่ม)
    //private Set<String> mSelectedCategories;        // หมวดคำศัพท์ที่ผู้ใช้เลือก

    private Random mRandom;                         // ออบเจ็คสำหรับสร้างเลขสุ่ม
    private Handler mHandler;                       // ออบเจ็คสำหรับถ่วงเวลาก่อนแสดงคำถามข้อถัดไป
    private Animation mShakeAnimation;              // แอนิเมชั่นการสั่น

    private TextView mQuestionNumberTextView;       // แสดงหมายเลขข้อของคำถาม
    private ImageView mQuestionImageView;           // รูปภาพคำถาม
    private TextView mAnswerTextView;               // แสดงข้อความที่บอกว่าตอบถูกหรือผิด
    private TableLayout mButtonTableLayout;         // ตารางบรรจุปุ่มคำศัพท์ที่เป็นตัวเลือก

    private DatabaseHelper mDbHelper;               // database helper
    private SQLiteDatabase mDatabase;               // ตัวฐานข้อมูลที่เราจะอ่าน/เขียนข้อมูล
    private int mDifficulty;                        // ค่าระดับความยากที่จะเก็บลงฐานข้อมูล


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // อ่านค่าระดับความยากที่ส่งมาจาก MainActivity
        Intent intent = getIntent();
        int diffIndex = intent.getIntExtra(DIFFICULTY, 0);
        String str = (getResources().getStringArray(R.array.difficulty))[diffIndex];
        if (str.equals(getString(R.string.easy_label))) {
            mNumChoices = 2;
            mDifficulty = DatabaseHelper.DIFFICULTY_EASY;
        } else if (str.equals(getString(R.string.medium_label))) {
            mNumChoices = 4;
            mDifficulty = DatabaseHelper.DIFFICULTY_MEDIUM;
        } else if (str.equals(getString(R.string.hard_label))) {
            mNumChoices = 6;
            mDifficulty = DatabaseHelper.DIFFICULTY_HARD;
        }

        // สร้างออบเจ็คต่างๆที่ต้องใช้งาน
        mFileNameList = new ArrayList<String>();
        mQuizWordsList = new ArrayList<String>();
        mChoiceWords = new ArrayList<String>();
        mRandom = new Random();
        mHandler = new Handler();

        // โหลดแอนิเมชั่นที่กำหนดไว้ในไฟล์ shake.xml
        mShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        mShakeAnimation.setRepeatCount(3);  // ให้แสดงแอนิเมชั่นซ้ำ 3 รอบ

        // อ้างอิงไปยังวิวต่างๆใน layout
        mQuestionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        mQuestionImageView = (ImageView) findViewById(R.id.questionImageView);
        mAnswerTextView = (TextView) findViewById(R.id.answerTextView);
        mButtonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);

        mDbHelper = new DatabaseHelper(this);
        mDatabase = mDbHelper.getWritableDatabase();

        //readSettings();                     // อ่านค่าตัวเลือกจาก shared preference
        //getImageFileName();                 // โหลดรายชื่อไฟล์มาเก็บใน ArrayList
        //startQuiz();                        // เริ่มเกมใหม่
        GetImageFileNameTask task = new GetImageFileNameTask(savedInstanceState);
        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.game);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mDbHelper.close();
    }

    private void readSettings() {
        // เข้าถึง shared preference
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        // อ่านค่าตัวเลือก "รูปแบบพยัญชนะ"
        String charCaseKey = getString(R.string.char_case_key);
        //mSelectedCase = pref.getString(charCaseKey, null);

        // อ่านค่าตัวเลือก "หมวดคำศัพท์"
        String categoriesKey = getString(R.string.categories_key);
        //mSelectedCategories = pref.getStringSet(categoriesKey, null);
    }

    private class GetImageFileNameTask extends AsyncTask<Void, Void, Void> {

        Bundle savedInstanceState;

        private GetImageFileNameTask(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        protected Void doInBackground(Void... params) {
            getImageFileName();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (savedInstanceState == null) {
                startQuiz();
            } else {
                resumeQuiz(savedInstanceState);
            }
        }
    }

    private void getImageFileName() {
        // อ่านค่าตัวเลือกหมวดคำศัพท์ที่ผู้ใช้กำหนด
        Set<String> selectedCategories = SettingsActivity.getOptionCategories(this);

        AssetManager assets = getAssets();  // เข้าถึง AssetManager
        try {
            // สำหรับหมวดคำศัพท์แต่ละหมวดที่ถูกเลือก
            for (String category : selectedCategories) {
                // หาชื่อไฟล์ภาพทั้งหมดจากหมวดนั้น
                String[] paths = assets.list(category);

                // ลบนามสกุล .png ของไฟล์ แล้วเก็บชื่อไฟล์ลงใน fileNameList
                for (String path : paths) {
                    mFileNameList.add(path.replace(".png", ""));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading image file names", e);
        }
    }

    private void startQuiz() {
        mScore = 0;                         // รีเซ็ตจำนวนครั้งที่ตอบถูก
        mTotalGuesses = 0;                  // รีเซ็ตจำนวนครั้งที่ทายคำตอบ
        mQuizWordsList.clear();             // ลบรายการคำศัพท์ที่เป็นคำถาม

        while (mQuizWordsList.size() <= TOTAL_QUESTIONS) {
            // สุ่มเลข เพื่อใช้เป็น index ในการเลือกไฟล์ภาพจาก fileNameList
            int randomIndex = mRandom.nextInt(mFileNameList.size());

            // หาชื่อไฟล์ภาพที่สุ่มเลือกได้
            String fileName = mFileNameList.get(randomIndex);

            // ถ้า quizWordsList ยังไม่มีชื่อไฟล์ภาพนั้น ให้เพิ่มชื่อไฟล์นั้นเข้าไปใน quizWordsList
            // (แต่ถ้ามีชื่อไฟล์นั้นแล้วก็จะไปสุ่มเลือกมาใหม่)
            if (!mQuizWordsList.contains(fileName)) {
                mQuizWordsList.add(fileName);
            }
        }

        loadNextQuestion();                 // โหลดคำถามข้อแรก
    }

    private void resumeQuiz(Bundle savedInstanceState) {
        mScore = savedInstanceState.getInt(SCORE);
        mTotalGuesses = savedInstanceState.getInt(TOTAL_GUESSES);
        mQuizWordsList = savedInstanceState.getStringArrayList(QUIZ_WORDS_LIST);

        mQuestionNumberTextView.setText(savedInstanceState.getString(QUESTION_NUMBER_TEXT_VIEW));
        mAnswerTextView.setText(savedInstanceState.getString(ANSWER_TEXT_VIEW));
        mAnswerTextView.setTextColor(savedInstanceState.getInt(ANSWER_TEXT_VIEW_COLOR));
        mAnswerFileName = savedInstanceState.getString(ANSWER_FILE_NAME);

        mChoiceWords = savedInstanceState.getStringArrayList(CHOICE_WORDS);

        loadQuestionImage();
        createChoiceButtons();

        boolean[] buttonStatesArray = savedInstanceState.getBooleanArray(BUTTON_STATES_LIST);

        for (int row = 0; row < mNumChoices / COLUMNS_PER_ROW; row++) {
            TableRow currentTableRow = (TableRow) mButtonTableLayout.getChildAt(row);

            for (int column = 0; column < COLUMNS_PER_ROW; column++) {
                Button choiceButton = (Button) currentTableRow.getChildAt(column);
                choiceButton.setEnabled(buttonStatesArray[row * COLUMNS_PER_ROW + column]);
            }
        }
    }

    private void loadNextQuestion() {
        // อ่านชื่อไฟล์ภาพชื่อแรกใน quizWordsList มาเป็นคำตอบ
        // (จริงๆเป็นชื่อไฟล์ แต่ชื่อไฟล์มีคำศัพท์รวมอยู่ด้วย) และลบชื่อนั้นออกจากลิสต์ไปเลย
        mAnswerFileName = mQuizWordsList.remove(0);

        mAnswerTextView.setText("");        // ลบข้อความใน answerTextView

        // แสดงหมายเลขข้อของคำถามปัจจุบันและจำนวนคำถามทั้งหมดใน 1 เกม
        String questionHeadLabel = getString(
                R.string.question_head_label, mScore + 1, TOTAL_QUESTIONS);
        mQuestionNumberTextView.setText(questionHeadLabel);

        loadQuestionImage();                // โหลดไฟล์รูปภาพคำถามจาก assets มาแสดงใน ImageView
        prepareChoiceWords();               // เตรียมคำศัพท์ที่เป็นตัวเลือก ซึ่งหนึ่งในนั้นคือคำตอบ
    }

    private void loadQuestionImage() {
        // หาชื่อหมวดหมู่ของคำศัพท์ที่รวมอยู่ในชื่อไฟล์ภาพ
        String category = mAnswerFileName.substring(0, mAnswerFileName.indexOf('-'));

        // เข้าถึง AssetManager เพื่อโหลดไฟล์ภาพจากโฟลเดอร์ assets
        AssetManager assets = getAssets();
        InputStream stream;                 // InputStream ที่ใช้อ่านข้อมูลจากไฟล์ภาพ

        try {
            // เปิดไฟล์ภาพ
            stream = assets.open(category + "/" + mAnswerFileName + ".png");

            // อ่านข้อมูลจากไฟล์ภาพมาเก็บเป็นออบเจ็ค Drawable แล้วนำไปกำหนดให้กับ ImageView
            // เพื่อแสดงรูปภาพนั้นออกมา
            Drawable questionImage = Drawable.createFromStream(stream, mAnswerFileName);
            mQuestionImageView.setImageDrawable(questionImage);
        } catch (IOException e) {
            Log.e(TAG, "Error loading " + mAnswerFileName, e);
        }
    }

    private void prepareChoiceWords() {
        mChoiceWords.clear();               // ลบตัวเลือกของโจทย์ข้อที่แล้ว

        // สุ่ม choice คำศัพท์
        while (mChoiceWords.size() < mNumChoices) {
            // สุ่มเลข เพื่อใช้เป็น index ในการเลือกคำศัพท์ที่เป็น choice
            int randomIndex = mRandom.nextInt(mFileNameList.size());

            // หาชื่อไฟล์ภาพที่สุ่มเลือกได้
            String fileName = mFileNameList.get(randomIndex);

            // ถ้า mChoiceWords ยังไม่มีชื่อไฟล์ภาพนั้นและไม่ซ้ำกับคำตอบ ให้เพิ่มชื่อไฟล์นั้นเข้าไปใน mChoiceWords
            // (แต่ถ้ามีชื่อไฟล์นั้นแล้วหรือซ้ำกับคำตอบ ก็จะไปสุ่มเลือกมาใหม่)
            if (!mChoiceWords.contains(getWord(fileName)) && !fileName.equals(mAnswerFileName)) {
                mChoiceWords.add(getWord(fileName));
            }
        }

        // สุ่มเลือกตำแหน่งใน choiceWords มา 1 ตำแหน่ง แล้วนำคำตอบแทนลงไป
        int randomIndex = mRandom.nextInt(mChoiceWords.size());
        mChoiceWords.set(randomIndex, getWord(mAnswerFileName));

        changeWordCase();                   // เปลี่ยนเป็นตัวพิมพ์เล็กหรือใหญ่ตามที่ผู้ใช้กำหนดในหน้า settings
        createChoiceButtons();              // สร้างปุ่มตัวเลือก
    }

    private void changeWordCase() {
        // อ่านค่าตัวเลือกรูปแบบพยัญชนะที่ผู้ใช้กำหนด
        String selectedCharCase = SettingsActivity.getOptionCharCase(this);

        String charCase;                    // รูปแบบพยัญชนะที่จะกำหนดให้คำศัพท์

        // ตรวจสอบว่าผู้ใช้ต้องการให้สุ่มรูปแบบพยัญชนะของคำศัพท์หรือไม่ ถ้าใช่ก็จะทำการสุ่ม
        // แต่ถ้าไม่ใช่ ก็จะใช้รูปแบบพยัญชนะตามที่ผู้ใช้กำหนด
        if (selectedCharCase.equals(getResources().getString(
                R.string.random_case_value))) {
            if (mRandom.nextInt(2) == 0)    // สุ่มเลขจำนวนเต็ม 0 หรือ 1
                charCase = getResources().getString(R.string.upper_case_value);
            else
                charCase = getResources().getString(R.string.lower_case_value);
        } else {
            charCase = selectedCharCase;       // ใช้รูปแบบพยัญชนะตามที่ผู้ใช้กำหนด
        }

        // กำหนด case ให้กับคำศัพท์ทั้งหมดใน mChoiceWords
        for (int i = 0; i < mChoiceWords.size(); i++) {
            if (charCase.equals(getResources().getString(R.string.upper_case_value))) {
                mChoiceWords.set(i, mChoiceWords.get(i).toUpperCase(Locale.ENGLISH));
            } else if (charCase.equals(getResources().getString(R.string.lower_case_value))) {
                mChoiceWords.set(i, mChoiceWords.get(i).toLowerCase(Locale.ENGLISH));
            }
        }
    }

    private String getWord(String name) {
        return name.substring(name.indexOf('-') + 1);
    }

    private void createChoiceButtons() {
        // ลบปุ่มใน TableLayout (ปุ่มของคำถามข้อที่แล้ว)
        for (int row = 0; row < mButtonTableLayout.getChildCount(); row++) {
            ((TableRow) mButtonTableLayout.getChildAt(row)).removeAllViews();
        }

        // เข้าถึง LayoutInflater
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // สร้างปุ่มใน TableLayout จำนวน 1, 2 หรือ 3 แถว ขึ้นอยู่กับค่าของ numChoices
        for (int row = 0; row < mNumChoices / COLUMNS_PER_ROW; row++) {
            TableRow currentTableRow = (TableRow) mButtonTableLayout.getChildAt(row);

            // เพิ่มปุ่มในแต่ละแถวเป็นจำนวน COLUMNS_PER_ROW
            for (int column = 0; column < COLUMNS_PER_ROW; column++) {
                // สร้างออบเจ็ค Button โดย inflate จาก XML ในไฟล์ guess_button.xml
                Button newGuessButton = (Button) inflater.inflate(
                        R.layout.guess_button, currentTableRow, false);

                newGuessButton.setText(mChoiceWords.get(row * COLUMNS_PER_ROW + column));

                // ระบุการทำงานเมื่อปุ่มถูกคลิก
                newGuessButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitGuess((Button) v);
                    }
                });

                currentTableRow.addView(newGuessButton);    // เพิ่มปุ่มใน TableRow
            }
        }
    }

    private void submitGuess(Button guessButton) {
        String guess = guessButton.getText().toString();    // คำศัพท์บนปุ่ม
        String answer = getWord(mAnswerFileName);           // คำศัพท์ที่เป็นคำตอบ
        mTotalGuesses++;                                    // เพิ่มจำนวนครั้งที่ผู้ใช้ทายคำศัพท์

        // ถ้าตอบถูก
        if (guess.equalsIgnoreCase(answer)) {
            mScore++;                       // เพิ่มจำนวนครั้งที่ตอบถูก
            playSound(R.raw.applause);      // เล่นเสียง (ปรบมือ)

            // แสดงคำศัพท์นั้นและข้อความที่บอกว่าตอบถูก สีเขียว
            String msg = guess + " " + getResources().getString(R.string.correct_answer);
            mAnswerTextView.setText(msg);
            mAnswerTextView.setTextColor(getResources().getColor(R.color.correct_answer));

            disableAllButtons();            // ทำให้ปุ่มทั้งหมดใช้งานไม่ได้ (ป้องกันผู้ใช้คลิก)

            // ถ้าครบ 10 ข้อแล้ว
            if (mScore == TOTAL_QUESTIONS) {
                // สร้าง AlertDialog.Builder (ออบเจ็คที่เป็นตัวสร้าง AlertDialog)
                AlertDialog.Builder resetDialog = new AlertDialog.Builder(this);

                // กำหนดข้อความบน title bar ของไดอะล็อก
                resetDialog.setTitle(R.string.result_title);

                // กำหนดข้อความในไดอะล็อก สรุปผลการเล่นเกม
                resetDialog.setMessage(String.format("%s %d\n%s %.1f%%",
                        getResources().getString(R.string.num_guesses),
                        mTotalGuesses,
                        getResources().getString(R.string.success_percentage),
                        ((100 * TOTAL_QUESTIONS) / (double) mTotalGuesses)));

                // ไม่ให้ยกเลิกไดอะล็อกได้ (เช่นการใช้ปุ่ม Back)
                resetDialog.setCancelable(false);

                // กำหนดปุ่ม “เริ่มเกมใหม่” ในไดอะล็อก
                resetDialog.setPositiveButton(R.string.restart_quiz,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startQuiz();    // เริ่มเกมใหม่
                            }
                        }
                );

                // กำหนดปุ่ม “กลับเมนูหลัก” ในไดอะล็อก
                resetDialog.setNegativeButton(R.string.return_main,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();       // ปิดหน้าเกม
                            }
                        }
                );

                resetDialog.show();         // แสดงไดอะล็อกออกมา
                saveScore();                // บันทึกคะแนนลงฐานข้อมูล

            } else {  // ตอบถูก แต่เกมยังไม่จบ (ยังไม่ครบ 10 ข้อ)
                // ถ่วงเวลา 2 วินาที แล้วแสดงคำถามข้อถัดไป
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextQuestion();
                    }
                }, 2000);                   // 2000 มิลลิวินาที ก็คือ 2 วินาที
            }
        } else {  // ถ้าตอบผิด
            playSound(R.raw.fail3);         // เล่นเสียง (fail)

            // แสดงแอนิเมชั่นการสั่น
            mQuestionImageView.startAnimation(mShakeAnimation);

            // แสดงข้อความที่บอกว่าตอบผิด สีแดง
            mAnswerTextView.setText(R.string.incorrect_answer);
            mAnswerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));

            guessButton.setEnabled(false);  // กำหนดให้ปุ่มนั้นใช้งานไม่ได้
        }
    }

    private void disableAllButtons() {
        for (int row = 0; row < mButtonTableLayout.getChildCount(); ++row) {
            TableRow tableRow = (TableRow) mButtonTableLayout.getChildAt(row);
            for (int i = 0; i < tableRow.getChildCount(); ++i) {
                tableRow.getChildAt(i).setEnabled(false);
            }
        }
    }

    private MediaPlayer mPlayer = null;

    private void playSound(int resId) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
        mPlayer = MediaPlayer.create(this, resId);
        mPlayer.start();
    }

    private void saveScore() {
        double score = (100 * TOTAL_QUESTIONS) / (double) mTotalGuesses;

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_SCORE, String.format("%.1f", score));
        cv.put(DatabaseHelper.COL_DIFFICULTY, mDifficulty);
        long insertResult = mDatabase.insert(DatabaseHelper.TABLE_NAME, null, cv);

        if (insertResult == 1) {
            Log.d(TAG, "Insert data into database: OK");
        } else if (insertResult == -1) {
            Log.d(TAG, "Insert data into database: FAILED !");
        }
    }

    /*************************************************************************/
    private static final String ANSWER_TEXT_VIEW = "answerTextView";
    private static final String ANSWER_TEXT_VIEW_COLOR = "answerTextViewColor";
    private static final String QUESTION_NUMBER_TEXT_VIEW = "questionNumberTextView";
    private static final String ANSWER_FILE_NAME = "questionImageFileName";
    private static final String CHOICE_WORDS = "choiceWords";
    private static final String SCORE = "score";
    private static final String TOTAL_GUESSES = "totalGuesses";
    private static final String QUIZ_WORDS_LIST = "quizWordsList";
    private static final String BUTTON_STATES_LIST = "buttonStatesList";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ANSWER_TEXT_VIEW, mAnswerTextView.getText().toString());
        outState.putInt(ANSWER_TEXT_VIEW_COLOR, mAnswerTextView.getCurrentTextColor());
        outState.putString(QUESTION_NUMBER_TEXT_VIEW,
                mQuestionNumberTextView.getText().toString());
        outState.putString(ANSWER_FILE_NAME, mAnswerFileName);

        outState.putStringArrayList(CHOICE_WORDS, mChoiceWords);
        outState.putInt(SCORE, mScore);
        outState.putInt(TOTAL_GUESSES, mTotalGuesses);

        outState.putStringArrayList(QUIZ_WORDS_LIST, mQuizWordsList);

        ArrayList<Boolean> buttonStatesList = new ArrayList<Boolean>();
        for (int row = 0; row < mNumChoices / COLUMNS_PER_ROW; row++) {
            TableRow currentTableRow = (TableRow) mButtonTableLayout.getChildAt(row);

            for (int column = 0; column < COLUMNS_PER_ROW; column++) {
                Button choiceButton = (Button) currentTableRow.getChildAt(column);
                if (choiceButton != null) {
                    buttonStatesList.add(choiceButton.isEnabled());
                }
            }
        }

        boolean[] buttonStatesArray = new boolean[buttonStatesList.size()];
        for (int i = 0; i < buttonStatesList.size(); i++) {
            buttonStatesArray[i] = buttonStatesList.get(i);
        }
        outState.putBooleanArray(BUTTON_STATES_LIST, buttonStatesArray);
    }
    /*************************************************************************/

}
