package magicapps.warptime.background;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import magicapps.warptime.R;
import magicapps.warptime.background.utilities.Notification;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Service extends android.app.Service {
    protected static final int NOTIFICATION_ID = 1337;
    private static Service mCurrentService;
    private static Timer timer;
    private static TimerTask timerTask;
    private Vibrator v;
    private int olderminute=0, newerminute=0;
    private boolean once = true;

    public Service() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            restartForeground();
        }
        mCurrentService = this;
        newerminute = Integer.valueOf(String.valueOf(new Date()).split(" ")[3].split(":")[1]);
        olderminute = newerminute;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            restartForeground();
        }

        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        newerminute = Integer.valueOf(String.valueOf(new Date()).split(" ")[3].split(":")[1]);
        startTimer();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                    Notification notification = new Notification();
                    startForeground(NOTIFICATION_ID, notification.setNotification(this, "This vibrates your phone every minute when app is open", "It is recommended to do the trick when a new minute starts", R.drawable.ic_launcher_background));
                    startTimer();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: reactivate this if ruins working, you don't want it vibrating to ppl when app is closed so
        // TODO: i also removed multiple lines related to quickboot and bootup
        /*Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();*/
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }

    public void startTimer() {
        stoptimertask();
        timer = new Timer();

        initializeTimerTask();

        timer.schedule(timerTask, 1000, 1000);
    }

    private void vibrate() {
        if(v==null)
            v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(545, VibrationEffect.DEFAULT_AMPLITUDE));
        else
            v.vibrate(545);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                try {
                    newerminute = Integer.valueOf(String.valueOf(new Date()).split(" ")[3].split(":")[1]);
                    if(newerminute!=olderminute){
                        /*if(once){
                            once = false;
                            olderminute = newerminute;
                        } else {*/
                            olderminute = newerminute;
                            vibrate();
                        /*}*/
                    }
                } catch(Exception ignored){}
            }
        };
    }

    private void display_notification(String title, String description) {
        Intent emptyIntent = new Intent();
        int NOT_USED = 1338;
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOT_USED, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(pendingIntent); //Required on Gingerbread and below

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        int mId = 5565;
        notificationManager.notify(mId, mBuilder.build());
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static Service getmCurrentService() { return mCurrentService; }

    public static void setmCurrentService(Service mCurrentService) { Service.mCurrentService = mCurrentService; }

    public static void isServiceRunning(){}

}
