package jp.techacademy.saito.maiko.kadai05_autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Handler;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Button mBackButton;
    Button mForwardButton;
    Button mPlayStopButton;
    Cursor cursor;
    Timer mTimer;
    double mTimerSec = 0.0;
    boolean play_flg = false;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(this);

        mForwardButton = (Button) findViewById(R.id.forward_button);
        mForwardButton.setOnClickListener(this);

        mPlayStopButton = (Button) findViewById(R.id.play_stop_button);
        mPlayStopButton.setOnClickListener(this);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                } else {
                    Toast toast = Toast.makeText(this, "READ_EXTERNAL_STORAGEを許可しないと画像表示できません", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    mPlayStopButton.setEnabled(false);
                    mBackButton.setEnabled(false);
                    mForwardButton.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    public void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor != null) {
            cursor.moveToFirst();
            // indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

//                Log.d("MAIKO_LOG", "fieldIndex : " + fieldIndex);
//                Log.d("MAIKO_LOG", "id : " + id);
//                Log.d("MAIKO_LOG", "ColumnCount : " + cursor.getColumnCount());
            Log.d("MAIKO_LOG", "Count初期 : " + cursor.getCount());
            Log.d("MAIKO_LOG", "URI初期 : " + imageUri.toString());
            Log.d("MAIKO_LOG", "Position初期 : " + cursor.getPosition());
            ImageView imageView = (ImageView) findViewById(R.id.slide);
            imageView.setImageURI(imageUri);
            //cursor.close();
        }else{
            Log.d("MAIKO_LOG", "Cursor初期なし");
        }
    }

    @Override
    public void onClick (View v){
        if (v.getId() == R.id.back_button) {
            getPreviousInfo();
        } else if (v.getId() == R.id.forward_button) {
            getNextInfo();
        } else if (v.getId() == R.id.play_stop_button) {
            slideShow();
        }
    }

    private void getPreviousInfo() {

        if(!cursor.moveToPrevious()) {
            cursor.moveToLast();
        }
        setImageView();
        }

    private void getNextInfo() {

        if(!cursor.moveToNext()) {
            cursor.moveToFirst();
        }
        setImageView();
    }

    private void setImageView() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        ImageView imageView = (ImageView) findViewById(R.id.slide);
        imageView.setImageURI(imageUri);
        Log.d("MAIKO_LOG", "URI : " + imageUri.toString());
        Log.d("MAIKO_LOG", "Position初期 : " + cursor.getPosition());
    }

    private void slideShow() {
        if (!play_flg) {
            play_flg = true;
            mPlayStopButton.setText("停止");
            mBackButton.setEnabled(false);
            mForwardButton.setEnabled(false);

            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimerSec += 2.0;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            getNextInfo();
                        }
                    });
                }
            }, 2000, 2000);
        } else if (play_flg) {
            play_flg = false;
            mPlayStopButton.setText("再生");
            mBackButton.setEnabled(true);
            mForwardButton.setEnabled(true);
            mTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        cursor.close();
    }
}


