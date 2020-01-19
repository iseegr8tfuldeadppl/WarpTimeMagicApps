package magicapps.warptime.background.restarter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import magicapps.warptime.background.Globals;


public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        context.sendBroadcast(broadcastIntent);
    }



}