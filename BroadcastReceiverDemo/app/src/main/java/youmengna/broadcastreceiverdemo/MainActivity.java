package youmengna.broadcastreceiverdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button sendData;
    private Button registerBtn;
    private Button unregisterBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendData= (Button) findViewById(R.id.sendData);
        sendData.setOnClickListener(this);
        registerBtn= (Button) findViewById(R.id.register);
        registerBtn.setOnClickListener(this);
        unregisterBtn= (Button) findViewById(R.id.unregister);
        unregisterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendData:
                //Intent intent=new Intent(MainActivity.this,MyReceiver.class);
                Intent intent=new Intent(MyReceiver.ACTION);
                //sendBroadcast(intent);
                sendOrderedBroadcast(intent,null);
                break;
            case R.id.register:
                if(myReceiver==null){
                    myReceiver=new MyReceiver();
                    registerReceiver(myReceiver,new IntentFilter(MyReceiver.ACTION));
                }
                break;
            case R.id.unregister:
                if(myReceiver!=null){
                    unregisterReceiver(myReceiver);
                    myReceiver=null;
                }
                break;
        }
    }

    private MyReceiver myReceiver=null;
}
