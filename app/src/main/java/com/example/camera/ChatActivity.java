package com.example.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    static RecyclerView recyclerView;
    EditText editText;
    static ChatRecyclerAdapter adapter;
    List<String> data = new ArrayList<>();
    static InputMethodManager imm;
    static MyHandler handler;
    static int heightDifference = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        recyclerView = findViewById(R.id.recycler);
        editText = findViewById(R.id.et_input);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    data.add(editText.getText().toString());
                    recyclerView.removeAllViews();
                    adapter = new ChatRecyclerAdapter(getApplicationContext(),data);
                    recyclerView.setAdapter(adapter);
                    editText.setText("");
                    Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();

                    return true;
                }
                return false;
            }
        });
        editText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){

            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight =  getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                heightDifference = screenHeight - r.bottom;
                Log.d("Keyboard Size", "Size: " + heightDifference);
            }

        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        data.add("12344346913123654");
        data.add("4354646136349+64h");
        adapter = new ChatRecyclerAdapter(this,data);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);

        handler = new MyHandler(this);

        handler.sendEmptyMessageDelayed(100,100);
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        float y1 = 0;
//        float y2 = 0;
//        switch (event.getAction()){
//
//            case MotionEvent.ACTION_DOWN:
//                y1 = event.getY();
//                System.out.println("y1的值："+y1);
//                break;
//            case MotionEvent.ACTION_UP:
//                y2 = event.getY();
//                System.out.println("y1的值："+y2);
//                if ((y2 - y1)> 60 ){
//                    handler.removeMessages(100);
//                }
//                break;
//        }
//        return false;
//    }


    static class  MyHandler extends Handler{

        WeakReference<Activity> weakReference;

        public MyHandler(Activity activity) {
            this.weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            Activity activity = weakReference.get();

            if (activity == null){
                return;
            }

            switch (msg.what){
                case 100:
                    System.out.println("软键盘是否弹出"+heightDifference);
                    if (heightDifference >0 ){
                        recyclerView.scrollToPosition(adapter.getItemCount()-1);
                        handler.sendEmptyMessageDelayed(100,100);
                        heightDifference = 0;
                    }

                    break;
            }
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeMessages(100);
        }
    }
}
