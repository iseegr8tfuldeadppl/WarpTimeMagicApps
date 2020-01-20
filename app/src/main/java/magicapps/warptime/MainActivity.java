package magicapps.warptime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import magicapps.warptime.SQLite.SQL;
import magicapps.warptime.SQLite.SQLSharing;
import magicapps.warptime.background.ProcessMainClass;
import magicapps.warptime.background.restarter.RestartServiceBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    private LinearLayout blackscreen;
    private Vibrator v;
    private int shortvibrate = 25, longvibrate= 60;
    private TextView hoursdisplay, minutesdisplay, twodots, date;
    private boolean doubletapmode = false;
    private int counter = 0, doubletapcounter = -1;
    private String displayeddatefullstring = "";
    private boolean first = true, second = true;
    private int hours=12, minutes=30, displayedhours, displayedminutes;
    private boolean pm = false;
    private int curBrightnessValue = 255;
    private String todayfullstring = "";
    private String displayedday = "", displayedmonth = "", displayeddayofmonth = "";
    private Thread mythread, mythread2;
    private int CODE_WRITE_SETTINGS_PERMISSION = 1338;
    private boolean tutorial = false;
    private TextView reload,confirm,addone,addtwo,addthree;

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

        sql();
        SQLSharing.mycursor.moveToFirst();
        if(SQLSharing.mycursor.getString(1).equals("no"))
            tutorial = true;
        close_sql();


        if(tutorial){
            addthree = findViewById(R.id.addthree);
            addtwo = findViewById(R.id.addtwo);
            addone = findViewById(R.id.addone);
            confirm = findViewById(R.id.confirm);
            reload = findViewById(R.id.reload);
            Resources resources = getResources();
            addone.setBackground(resources.getDrawable(R.drawable.addone));
            addtwo.setBackground(resources.getDrawable(R.drawable.addtwo));
            addthree.setBackground(resources.getDrawable(R.drawable.addthree));
            confirm.setBackground(resources.getDrawable(R.drawable.confirm));
            addone.setText("Add 1" + '\n' + "minute");
            addtwo.setText("Add 2" + '\n' + "minutes");
            addthree.setText("Add 3" + '\n' + "minutes");
            confirm.setText("Confirm" + '\n' + "Number");
            reload.setText("Reset");
        }

        // background service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }

    private void sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
        SQLSharing.mydb = new SQL(this);
        SQLSharing.mycursor = SQLSharing.mydb.getAllDate();
    }

    private void close_sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(curBrightnessValue==0)
            setBrightness(255);
        else
            setBrightness(curBrightnessValue);

        running = false;

        try {
            mythread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(curBrightnessValue==0)
            setBrightness(255);
        else
            setBrightness(curBrightnessValue);
    }

    private void setBrightness(int level) {
        if(!tutorial) {
            android.provider.Settings.System.putInt(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
                    level);
        }
    }

    private boolean dontvibrateforonce = true;
    @Override
    protected void onResume() {
        super.onResume();

        if(dontvibrateforonce)
            dontvibrateforonce = false;
        else
            doublevibrate();

        reload();

        update_time();

        running = true;
        live_updates();

        save_current_brightness();

        lower_brightness();
    }

    private void doublevibrate() {

        vibrate(300);
        Runnable r=new Runnable() {
            @Override
            public void run() {
                long futuretime = System.currentTimeMillis() + 500;

                while (System.currentTimeMillis() < futuretime){
                    synchronized (this){
                        try{
                            wait(futuretime - System.currentTimeMillis());
                        } catch( Exception ignored){}
                    }
                }

                vibrator.sendEmptyMessage(0);
            }
        };

        Thread getMythread3 = new Thread(r);
        getMythread3.start();

    }

    private void lower_brightness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(this))
                setBrightness(0);
            else
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).setData(Uri.parse("package:"+getPackageName()) ),0);
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED)
                setBrightness(0);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, CODE_WRITE_SETTINGS_PERMISSION);
        }
    }

    private void save_current_brightness() {
        curBrightnessValue = 255;
        try {
            curBrightnessValue = android.provider.Settings.System.getInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void reload() {
        hideNavigationBar();
        setBrightness(0);

        if(tutorial) {
            addone.setText("Add 1" + '\n' + "minute");
            addtwo.setText("Add 2" + '\n' + "minutes");
            addthree.setText("Add 3" + '\n' + "minutes");
            confirm.setText("Confirm" + '\n' + "Number");
            reload.setText("Reset");
        }

        magicbeingdone = false;
        first = true;
        second = true;
        blackscreen.setVisibility(View.VISIBLE);
        doubletapmode = false;
        counter = 0;
        doubletapcounter = 0;
    }

    private void update_time() {
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
        displayedminutes = minutes;
        displayedhours = hours;

        if(displayedminutes<10)
            minutesdisplay.setText("0" + String.valueOf(displayedminutes));
        else
            minutesdisplay.setText(String.valueOf(displayedminutes));

        if(hours==0)
            hoursdisplay.setText("0" + String.valueOf(displayedhours));
        else
            hoursdisplay.setText(String.valueOf(displayedhours));

        todayfullstring = today + ", " + month + " " + dayofmonth;
        date.setText(todayfullstring);
    }

    private int olderminute=0, newerminute=0;
    private boolean magicbeingdone = false;
    private void live_updates() {
        Runnable r=new Runnable() {@Override public void run() { try {
            while(running) {

                wait_1_second();
                if(!magicbeingdone) {
                    newerminute = Integer.valueOf(String.valueOf(new Date()).split(" ")[3].split(":")[1]);
                    if (newerminute != olderminute) {
                        olderminute = newerminute;
                        updatetimehandler.sendEmptyMessage(0);
                    }
                }

            }
        } catch(Exception ignored){} }};

        //anti lag
        mythread2 = new Thread(r); //to thread the runnable object we launched
        mythread2.start();
    }

    private boolean running = true;
    private void wait_1_second() {
        long futuretime = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < futuretime && running) {
            //prevents multiple threads from crashing into each other
            synchronized (this) {
                try {
                    wait(futuretime - System.currentTimeMillis());
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        reload();
    }

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
            if(tutorial) {
                if(counter<=10)
                    print("Clock is now " + String.valueOf(counter) + " minutes ahead");
            }
            /*display.setText(String.valueOf(counter));*/
            limitcounter();
        }
    }

    private void limitcounter() {
        if(counter>10){
            vibrate(longvibrate*5);
            counter = 0;
            if(tutorial) {
                print2("You have passed 10 minutes! clock has been reset to current time");
            }
            /*display.setText(String.valueOf(counter));*/
        }
    }

    public void DoubleincrementerClicked(View view) {
        if(doubletapmode)
            exiter();
        else {
            vibrate(shortvibrate);
            counter += 2;
            if(tutorial) {
                if(counter<=10)
                    print("Clock is now " + String.valueOf(counter) + " minutes ahead");
            }
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
            if(tutorial) {
                if(counter<=10)
                    print("Clock is now " + String.valueOf(counter) + " minutes ahead");
            }
            /*display.setText(String.valueOf(counter));*/
            limitcounter();
        }
    }

    public void vibratorClicked(View view) {

        if(!doubletapmode){ doubletapmode = true;
            vibrate(longvibrate);
            if(tutorial){
                addone.setText("Double tap" + '\n' + "anywhere");
                reload.setText("Don't double tap here!");
                addtwo.setText("Double tap anywhere");
                addthree.setText("Double tap" + '\n' + "anywhere");
                confirm.setText("Double tap" + '\n' + "anywhere");
            }
        }
        exiter();

    }

    public void reloadClicked(View view) {
        reload();
        if(tutorial){
            print("Clock has been reset to current time");
        }
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
            if(curBrightnessValue==0)
                setBrightness(255);
            else
                setBrightness(curBrightnessValue);
            blackscreen.setVisibility(View.GONE);
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            magicbeingdone = true; // i put it here so it starts working at the very last moment
            the_good_work();
        }
    };
    private Handler vibrator = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            vibrate(300);
        }
    };

    private boolean oncelol = true;
    private Handler updatetimehandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(tutorial){
                if(oncelol)
                    oncelol = false;
                else
                    print2("Time is into a new minute! it is advised to do the trick early in a new minute like now!");
            }
            update_time();
        }
    };

    private int delayer = 1350;
    private void dramatic_effect() {
        if(first){
            first = false;
            delayer = 200;
        } else if(second){
            second = false;
            delayer = 850;
        } else
            delayer = 630;
    }

    private void the_good_work() {
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
            dramatic_effect();
            if(displayedminutes<10)
                minutesdisplay.setText("0" + String.valueOf(displayedminutes));
            else
                minutesdisplay.setText(String.valueOf(displayedminutes));

            if(hours==0)
                hoursdisplay.setText("0" + String.valueOf(displayedhours));
            else
                hoursdisplay.setText(String.valueOf(displayedhours));

            date.setText(displayeddatefullstring);
            starttheshit();
        } else {
            magicbeingdone = false;
            if(tutorial) {
                print("Done!");
                print("Press back or home button to reset the trick!");
            }
            counter = 0;
        }
    }

    // TODO: live time updates, don't update UI if a boolean says trick is being done
    // TODO: just copy onresume
    // TODO: state variable is only set when we boutta remove the black background

    private void print(Object lol){
        Toast.makeText(getApplicationContext(), String.valueOf(lol), Toast.LENGTH_SHORT).show();
    }
    private void print2(Object lol){
        Toast.makeText(getApplicationContext(), String.valueOf(lol), Toast.LENGTH_LONG).show();
    }

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

        mythread = new Thread(r);
        mythread.start();

    }

}
