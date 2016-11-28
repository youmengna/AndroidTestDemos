package youmengna.broadcastreceiverdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    public static final String ACTION="youmengna.broadcastreceiverdemo.intent.action.BroadcastReceiver";
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("info","接收到消息了");
    }
}
