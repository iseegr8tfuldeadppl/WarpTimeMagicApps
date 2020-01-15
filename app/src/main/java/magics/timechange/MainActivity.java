package magics.timechange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private LinearLayout blackscreen;
    private Vibrator v;
    private TextView hoursdisplay, minutesdisplay, twodots, date;
    private boolean doubletapmode = false;
    private int shortvibrate = 25, longvibrate= 60;
    private int counter = 0, doubletapcounter = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twodots = findViewById(R.id.twodots);
        minutesdisplay = findViewById(R.id.minutesdisplay);
        hoursdisplay = findViewById(R.id.hoursdisplay);
        date = findViewById(R.id.date);
        blackscreen = findViewById(R.id.blackscreen);
        /*display = findViewById(R.id.display);*/
        Typeface coolfont = Typeface.createFromAsset(getAssets(),"fonts/montserrat/" + "Montserrat-Thin.ttf");
        Typeface coolfont2 = Typeface.createFromAsset(getAssets(),"fonts/montserrat/" + "Montserrat-Light.ttf");
        hoursdisplay.setTypeface(coolfont);
        minutesdisplay.setTypeface(coolfont);
        twodots.setTypeface(coolfont);
        date.setTypeface(coolfont2);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    private void setBrightness(int level) {

        /*float curBrightnessValue = 0;

        try {
            curBrightnessValue = android.provider.Settings.System.getInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }*/
        android.provider.Settings.System.putInt(getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                level);
    }

    private boolean pm = false;
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(this)){
                hideNavigationBar();
                blackscreen.setVisibility(View.VISIBLE);
                /*display.setVisibility(View.VISIBLE);*/
                doubletapmode = false;
                counter = 0;
                doubletapcounter = 0;
                setBrightness(0);
                Date lol = new Date();
                String[] day = String.valueOf(lol).split(" ");
                String time = day[3];
                hours = Integer.valueOf(time.split(":")[0]);
                minutes = Integer.valueOf(time.split(":")[1]);
                if(hours>12){
                    pm = true;
                    hours -= 12;
                }
                String today = day[0];
                String month = day[1];
                String dayofmonth = day[2];
                hoursdisplay.setText(String.valueOf(hours));
                minutesdisplay.setText(String.valueOf(minutes));
                todayfullstring = today + ", " + month + " " + dayofmonth;
                date.setText(todayfullstring);
            } else
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).setData(Uri.parse("package:"+getPackageName()) ),0);
        }
    }

    private String todayfullstring;
    private void hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    );
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    );
        }
    }

    public void incrementerClicked(View view) {
        if(doubletapmode)
            exiter();
        else {
            vibrate(shortvibrate);
            counter += 1;
            /*display.setText(String.valueOf(counter));*/
            limitcounter();
        }
    }

    private void limitcounter() {
        if(counter>10){
            vibrate(longvibrate*5);
            counter = 0;
            /*display.setText(String.valueOf(counter));*/
        }
    }

    public void DoubleincrementerClicked(View view) {
        if(doubletapmode)
            exiter();
        else {
            vibrate(shortvibrate);
            counter += 2;
            /*display.setText(String.valueOf(counter));*/
            limitcounter();
        }
    }

    public void TripleincrementerClicked(View view) {
        if(doubletapmode)
            exiter();
        else {
            vibrate(shortvibrate);
            counter += 3;
            /*display.setText(String.valueOf(counter));*/
            limitcounter();
        }
    }

    public void vibratorClicked(View view) {

        if(!doubletapmode){ doubletapmode = true;
            vibrate(longvibrate);
        }
        exiter();

    }

    public void reloadClicked(View view) {
        doubletapmode = false;
        doubletapcounter = 0;
        counter = 0;
        /*display.setText("0");*/
        hideNavigationBar();

        vibrate(longvibrate);
    }

    private void vibrate(int longvibrate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(longvibrate, VibrationEffect.DEFAULT_AMPLITUDE));
        else
            v.vibrate(longvibrate);
    }

    private void exiter() {
        doubletapcounter ++;
        if(doubletapcounter>=3){
            /*counter += 1; // to try n fix the mismatch minute*/
            displayfaketime();
            doubletapcounter = 0;
            doubletapmode = false;
            setBrightness(255);
            blackscreen.setVisibility(View.GONE);
            /*display.setVisibility(View.GONE);*/
            starttheshit();
        }
    }

    private void displayfaketime() {
        displayedminutes = minutes + counter;
        displayedhours = hours;
        displayeddatefullstring = todayfullstring;
        if(displayedminutes>=60){
            displayedminutes -= 60;
            displayedhours = hours + 1;
            if(displayedhours>=12){
                displayedhours -= 12;
                if(pm){
                    String[] todaysplittemparray = (new Date()).toString().split(" ");
                    int day = Integer.valueOf(todaysplittemparray[2]);
                    int year = Integer.valueOf(todaysplittemparray[5]);
                    int month = get_month(todaysplittemparray[1]);

                    GregorianCalendar gc = new GregorianCalendar(year, month-1, day);
                    gc.add(Calendar.DATE, 1);
                    String[] CurrentDisplayedDay = gc.getTime().toString().split(" ");
                    displayedday = CurrentDisplayedDay[0];
                    displayedmonth = CurrentDisplayedDay[1];
                    displayeddayofmonth = CurrentDisplayedDay[2];
                    displayeddatefullstring = displayedday + ", " + displayedmonth + " " + displayeddayofmonth;
                }
            }
        }
        if(displayedminutes==-1) {
            displayedhours -= 1;
            displayedminutes = 59 + displayedminutes;
        }
        if(counter>=0) {
            if(displayedminutes<10)
                minutesdisplay.setText("0" + String.valueOf(displayedminutes));
            else if(displayedminutes>=10)
                minutesdisplay.setText(String.valueOf(displayedminutes));

            if(hours==0)
                hoursdisplay.setText("0" + String.valueOf(displayedhours));
            else
                hoursdisplay.setText(String.valueOf(displayedhours));

            date.setText(displayeddatefullstring);
        }
    }

    private String displayeddatefullstring;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            delayer = 690;
            displayedminutes = minutes + counter;
            displayedhours = hours;
            displayeddatefullstring = todayfullstring;
            if(displayedminutes>=60){
                displayedminutes -= 60;
                displayedhours = hours + 1;
                if(displayedhours>=12){
                    displayedhours -= 12;
                    if(pm){
                        String[] todaysplittemparray = (new Date()).toString().split(" ");
                        int day = Integer.valueOf(todaysplittemparray[2]);
                        int year = Integer.valueOf(todaysplittemparray[5]);
                        int month = get_month(todaysplittemparray[1]);

                        GregorianCalendar gc = new GregorianCalendar(year, month-1, day);
                        gc.add(Calendar.DATE, 1);
                        String[] CurrentDisplayedDay = gc.getTime().toString().split(" ");
                        displayedday = CurrentDisplayedDay[0];
                        displayedmonth = CurrentDisplayedDay[1];
                        displayeddayofmonth = CurrentDisplayedDay[2];
                        displayeddatefullstring = displayedday + ", " + displayedmonth + " " + displayeddayofmonth;
                    }
                }
            }
            if(displayedminutes==-1) {
                displayedhours -= 1;
                displayedminutes = 59 + displayedminutes;
            }
            if(counter>=0) {
                counter -= 1;
                if(displayedminutes<10)
                    minutesdisplay.setText("0" + String.valueOf(displayedminutes));
                else if(displayedminutes>=10)
                    minutesdisplay.setText(String.valueOf(displayedminutes));

                if(hours==0)
                    hoursdisplay.setText("0" + String.valueOf(displayedhours));
                else
                    hoursdisplay.setText(String.valueOf(displayedhours));

                date.setText(displayeddatefullstring);
                starttheshit();
            } else
                counter = 0;
        }
    };

    private void print(Object lol){
        Toast.makeText(getApplicationContext(), String.valueOf(lol), Toast.LENGTH_SHORT).show();
    }

    private String displayedday, displayedmonth, displayeddayofmonth;
    private int get_month(String month){
        switch (month) {
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
        }
        return 1;
    }

    private int hours=12, minutes=30, displayedhours, displayedminutes;
    private int delayer = 1350;
    private void starttheshit() {


        Runnable r=new Runnable() {
            @Override
            public void run() {
                long futuretime = System.currentTimeMillis() + delayer;

                while (System.currentTimeMillis() < futuretime){
                    synchronized (this){
                        try{
                            wait(futuretime - System.currentTimeMillis());
                        } catch( Exception ignored){}
                    }
                }

                handler.sendEmptyMessage(0);
            }
        };

        Thread mythread = new Thread(r);
        mythread.start();

    }

}
