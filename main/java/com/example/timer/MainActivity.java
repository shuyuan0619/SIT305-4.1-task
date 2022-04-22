package com.example.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public String time = "00:00:00" ;
    public String name = "...";

    public ImageButton button1;
    public ImageButton button2;
    public ImageButton button3;

    public TextView TitleView;
    public TextView TitleClock;
    public EditText nameOfUser;
    public LinearLayout linearLayout;

    boolean flag = false;
    boolean flag2 = false;
    boolean isCounting = false;
    public ClockThread myThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TitleView = findViewById(R.id.TitleView);
        TitleClock = findViewById(R.id.TitleClock);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        nameOfUser = findViewById(R.id.nameOfUser);
        linearLayout = findViewById(R.id.linearLayout);
        myThread = new ClockThread();


        if(savedInstanceState!=null){
            int hour = 0;
            int minute = 0;
            int second = 0;
            if(savedInstanceState.containsKey("hour")){
                hour = savedInstanceState.getInt("hour");
            }
            if(savedInstanceState.containsKey("minute")){
                minute = savedInstanceState.getInt("minute");
            }
            if(savedInstanceState.containsKey("second")){
                second = savedInstanceState.getInt("second");
            }
            if(savedInstanceState.containsKey("flag2")){
                flag2 = savedInstanceState.getBoolean("flag2");
            }
            ConstraintLayout.LayoutParams lp_btn = (ConstraintLayout.LayoutParams) linearLayout.getLayoutParams();
            ConstraintLayout.LayoutParams lp_TitleView = (ConstraintLayout.LayoutParams) TitleView.getLayoutParams();
            ConstraintLayout.LayoutParams lp_TitleClock = (ConstraintLayout.LayoutParams) TitleClock.getLayoutParams();
            if(flag2){
                Log.e("TAG", "1111111111111111111" );
                lp_btn.topMargin = 10;
                lp_TitleView.topMargin=20;
                lp_TitleClock.topMargin=10;
            }else{
                lp_btn.topMargin = 100;
                lp_TitleView.topMargin=100;
                lp_TitleClock.topMargin=180;
                Log.e("TAG", "222222222222222222" );
            }
            linearLayout.setLayoutParams(lp_btn);
            TitleView.setLayoutParams(lp_TitleView);
            TitleClock.setLayoutParams(lp_TitleClock);

            isCounting = savedInstanceState.getBoolean("isCounting");
            TitleClock.setText(savedInstanceState.getString("time"));
            myThread.hour = hour;
            myThread.minute = minute;
            myThread.second = second;
            if(isCounting){
                myThread.start();
                flag = true;
            }
            String str1, str2, str3;
            if(second >= 10){
                str1 = String.valueOf(second);
            }else{
                str1 = "0"+String.valueOf(second);
            }
            if(minute >= 10){
                str2 = String.valueOf(minute);
            }else{
                str2 = "0"+String.valueOf(minute);
            }
            if(hour >= 10){
                str3 = String.valueOf(hour);
            }else{
                str3 = "0"+String.valueOf(hour);
            }
            TitleClock.setText(str3+":"+str2+":"+str1);
            flag = false;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        if(sharedPreferences == null) TitleView.setText("You spent 00:00 on ... last time");
        else{
            name = sharedPreferences.getString("name","...");
            time = sharedPreferences.getString("time","00:00");
            TitleView.setText("You spent "+time+" on "+name+" last time");
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flag){
                    myThread.start();
                    flag = true;
                }else myThread.pauseFlag = false;
                isCounting = true;
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                myThread.pauseFlag = true;
                isCounting = false;
            }

        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                if(myThread.hour!=0){
                    editor.putString("time",myThread.strOfHour+":"+myThread.strOfMinute+":"+myThread.strOfSecond);
                }else{
                    editor.putString("time",myThread.strOfMinute+":"+myThread.strOfSecond);
                }

                String str = nameOfUser.getText().toString();
                if(str!=null){
                    editor.putString("name",str);
                }else{
                    editor.putString("name","...");
                }
                editor.apply();
                myThread.pauseFlag = true;
                isCounting = false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        flag2 = !flag2;
        Log.e("TAG", "onSaveInstanceState:    " + flag2);
        outState.putBoolean("flag2", flag2);
        outState.putBoolean("isCounting", isCounting);
        outState.putBoolean("flag", true);
        if(myThread.hour == 0){
            outState.putInt("minute", myThread.minute);
            outState.putInt("second", myThread.second);
        }else{
            outState.putInt("hour", myThread.hour);
            outState.putInt("minute", myThread.minute);
            outState.putInt("second", myThread.second);
        }
        outState.putString("time", myThread.strOfHour+":"+myThread.strOfMinute+":"+myThread.strOfSecond);
    }

    class ClockThread extends Thread{

        boolean pauseFlag = false;

        public int hour = 0;

        public int minute = 0;

        public int second = 0;

        public String strOfHour = "00";

        public String strOfMinute = "00";

        public String strOfSecond = "00";

        @Override
        public void run() {
            super.run();
            while(true){
                while(pauseFlag){

                }
                try {
                    sleep(1000);
                    second = second + 1;
                    if(second >= 60){
                        second=0;
                        minute = minute + 1;
                    }
                    if(minute>=60){
                        minute=0;
                        hour = hour + 1;
                    }
                    if(hour >= 24) hour = 0;
                    if(second >= 10){
                        strOfSecond = String.valueOf(second);
                    }else{
                        strOfSecond = "0" + String.valueOf(second);
                    }
                    if(minute >= 10){
                        strOfMinute = String.valueOf(minute);
                    }else{
                        strOfMinute = "0" + String.valueOf(minute);
                    }
                    if(hour >= 10){
                        strOfHour = String.valueOf(hour);
                    }else{
                        strOfHour = "0" + String.valueOf(hour);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TitleClock.setText(strOfHour+":"+strOfMinute+":"+strOfSecond);
                        }
                    });
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

}