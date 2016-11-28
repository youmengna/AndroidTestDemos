package youmengna.broadcastreceiverdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver1 extends BroadcastReceiver {
    public MyReceiver1() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("info","MyReceiver1收到消息啦");
    }
}
