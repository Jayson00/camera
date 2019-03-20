package com.example.camera;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {

    EditText editText;
    Button CamaraBtn,BitmapBtn,PictureBtn;
    ImageView imageView;
    private String fileName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //请求权限
        requestCameraAccess();

        editText = findViewById(R.id.et_input);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    // 先隐藏键盘
                    ((InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    //操作其他事件
                    Log.e("软键盘监听事件", "onEditorAction: 点击了搜索" );
                    Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });
        CamaraBtn = findViewById(R.id.btn_camara);
        BitmapBtn = findViewById(R.id.btn_camara_bitmap);
        PictureBtn = findViewById(R.id.btn_picture);
        imageView = findViewById(R.id.image);
        CamaraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasCameraAccess();
            }
        });
        BitmapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasCameraAccess2();
            }
        });
        PictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1103);
            }
        });
    }


    /**
     * 6.0申请相机权限
     */
    protected void requestCameraAccess() {
        Log.e("TAG", "requestCameraAccess");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1101);
            }
            //判断是否需要 向用户解释，为什么要申请该权限
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
        } else {
            Toast.makeText(this,"权限已获得",Toast.LENGTH_SHORT).show();
//            hasCameraAccess();
        }
    }


    //获取照片存储在本地的路径
    //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);在使用是会造成onActivityResult里的data为null，这里取的是存储在本地路径
    private void hasCameraAccess() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.e("TAG", "SD card is not avaiable/writeable right now.");
            Toast.makeText(this, "存储卡不可用", Toast.LENGTH_SHORT).show();
        } else {
            /*
            创建myImage文件夹
			 */
            File file = new File(Environment.getExternalStorageDirectory() + "/myImage/");
            if (!file.exists()) {
                file.mkdirs();
            }
            /*
             * 设置图片保存的路径
             */
            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串
            date = new Date();
            String str = format.format(date);
            fileName = Environment.getExternalStorageDirectory() + "/myImage/" + str + ".jpg";
            Log.i("Tag", "photo fileName==" + fileName);
            Uri photoUri = Uri.fromFile(new File(fileName));
            Log.i("TAg", photoUri.getPath());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            int currentapiVersion = Build.VERSION.SDK_INT;
            //获取当前系统的Android版本号
            if (currentapiVersion < 24) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, 1101);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, photoUri.getPath());
                Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                Log.i("TAG", uri.getPath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1101);
            }
        }
    }


    //获取返回的Uri
    private void hasCameraAccess2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1101){

//            这里获取的是地址，而不是方法返回的数据，该处data为null(原图)
            Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            System.out.println("原图bitmap的大小："+bitmap.getByteCount());
            imageView.setImageBitmap(bitmap);
            System.out.println("返回数据："+fileName);

        }
        else if (resultCode == RESULT_OK && requestCode == 1102){

            // 获取相机返回的数据，并转换为Bitmap图片格式，这是缩略图
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            System.out.println("缩略图bitmap的大小："+bitmap.getByteCount());
            imageView.setImageBitmap(bitmap);

        }else if (resultCode == RESULT_OK && requestCode == 1103){

            Uri uriData=data.getData();
            imageView.setImageURI(uriData);

            //两种都可以
//            Uri uriData=data.getData();
//            String[] filePathColum={MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(uriData,filePathColum,null,null,null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColum[0]);
//            String imageFile=cursor.getString(columnIndex);
//            cursor.close();
//            Bitmap bitmap = BitmapFactory.decodeFile(imageFile);
//            imageView.setImageBitmap(bitmap);
//            System.out.println("返回数据："+fileName);

        }
    }



}
