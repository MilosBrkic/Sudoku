package com.example.sudoku;

import android.widget.TextView;

public class Timer extends Thread {

    TextView textTime;
    int x=0;

    public Timer(TextView textTime) {
        this.textTime = textTime;
    }

    @Override
    public void run() {



        try {


            while(true) {
                sleep(1000);
                x++;
                textTime.setText(x+"");
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    public int getX(){
        return x;
    }
}
