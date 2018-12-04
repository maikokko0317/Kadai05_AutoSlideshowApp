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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Button mBackButton;
    Button mForwardButton;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(this);

        mForwardButton = (Button) findViewById(R.id.forward_button);
        mForwardButton.setOnClickListener(this);

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
        Log.d("MAIKO_LOG", "Count画面初期表示 : " + cursor.getCount());

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
                }
                break;
            default:
                break;
        }
    }

    public void getContentsInfo() {

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
            cursor.moveToPosition( cursor.getPosition() + 1);
        }
    }

    private void getPreviousInfo() {

        if(!cursor.moveToPrevious()) {
            cursor.moveToLast();
        } else {
            cursor.moveToPrevious();
        }
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        //        Log.d("MAIKO_LOG", "URI : " + imageUri.toString());
        ImageView imageView = (ImageView) findViewById(R.id.slide);
        imageView.setImageURI(imageUri);

        Log.d("MAIKO_LOG", "button: BACK");
        Log.d("MAIKO_LOG", "Count戻る : " + cursor.getCount());
        Log.d("MAIKO_LOG", "URI戻る : " + imageUri.toString());
        Log.d("MAIKO_LOG", "Position戻る : " + cursor.getPosition());
        //cursor.close();
        }

    private void getNextInfo() {

        if(!cursor.moveToNext()) {
            cursor.moveToFirst();
        } else {
            cursor.moveToNext();
        }

        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        //        Log.d("MAIKO_LOG", "URI : " + imageUri.toString());
        ImageView imageView = (ImageView) findViewById(R.id.slide);
        imageView.setImageURI(imageUri);

        Log.d("MAIKO_LOG", "button: FORWARD");
        Log.d("MAIKO_LOG", "Count進む : " + cursor.getCount());
        Log.d("MAIKO_LOG", "URI進む : " + imageUri.toString());
        Log.d("MAIKO_LOG", "Position進む : " + cursor.getPosition());
        //cursor.close();
    }


    @Override
    protected void onStop() {
        super.onStop();
        cursor.close();
    }
}


