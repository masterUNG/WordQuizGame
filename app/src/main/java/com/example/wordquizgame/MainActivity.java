package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* เรียกเมธอด setDefaultValues เพื่อกำหนดค่าดีฟอลต์ของตัวเลือกลงใน shared
           preference file สำหรับตัวเลือกที่มีการระบุแอตทริบิวต์ android:defaultValue ไว้
           เนื่องจากแอพของเราอาจจะอ่านค่าตัวเลือกโดยที่ผู้ใช้ยังไม่เคยเข้าหน้า Settings เลย (ซึ่ง
           ตัวเลือกต่างๆจะยังไม่มีค่าใน shared preference file) */
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        Button playGameButton = (Button) findViewById(R.id.playGameButton);
        playGameButton.setOnClickListener(this);

        Button highScoreButton = (Button) findViewById(R.id.highScoreButton);
        highScoreButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.playGameButton:
                AlertDialog.Builder chooseDiffDialog = new AlertDialog.Builder(MainActivity.this);

                chooseDiffDialog.setTitle(R.string.choose_difficulty_title);
                chooseDiffDialog.setCancelable(true);
                chooseDiffDialog.setItems(R.array.difficulty,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                                intent.putExtra(GameActivity.DIFFICULTY, which);
                                startActivity(intent);
                            }
                        });

                DifficultyOptionsAdapter adapter = new
                        DifficultyOptionsAdapter(
                        MainActivity.this,
                        R.layout.difficulty_row,
                        new ArrayList<String>(Arrays.asList(
                                getResources().getStringArray(R.array.difficulty)))
                );

                chooseDiffDialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        intent.putExtra(GameActivity.DIFFICULTY, which);
                        startActivity(intent);
                    }
                });

                chooseDiffDialog.show();
                break;

            case R.id.highScoreButton:
                Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class DifficultyOptionsAdapter extends ArrayAdapter<String> {

        private Context context;
        private int itemLayoutId;
        private ArrayList<String> difficulties;

        public DifficultyOptionsAdapter(Context context, int itemLayoutId,
                                        ArrayList<String> difficulties) {
            super(context, itemLayoutId, difficulties);

            this.context = context;
            this.itemLayoutId = itemLayoutId;
            this.difficulties = difficulties;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(itemLayoutId, parent, false);

            TextView difficultyTextView = (TextView) row.findViewById(R.id.difficultyTextView);
            ImageView difficultyImageView = (ImageView) row.findViewById(R.id.difficultyImageView);

            String diff = difficulties.get(position);
            difficultyTextView.setText(diff);

            if (diff.equals(context.getString(R.string.easy_label))) {
                difficultyImageView.setImageResource(R.drawable.dog_easy);
            } else if (diff.equals(context.getString(R.string.medium_label))) {
                difficultyImageView.setImageResource(R.drawable.dog_medium);
            } else if (diff.equals(context.getString(R.string.hard_label))) {
                difficultyImageView.setImageResource(R.drawable.dog_hard);
            }

            return row;
        }
    }
}
