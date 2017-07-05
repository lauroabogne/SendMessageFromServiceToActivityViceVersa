package com.blogspot.justsimpleinfo.sendmessagefromservicetoactivityviceversa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Messenger mServiceMessenger;
    boolean isBound;

    Button mStartServiceBtn;
    Button mSendMessageBtn;
    TextView mServiceResponseTextView;



    public void sendMessage(View view) {
        if (isBound) {
            try {
                Message message = Message.obtain(null, MyService.MESSAGE, 1, 1);
                message.replyTo = replyMessenger;

                Bundle bundle = new Bundle();
                bundle.putString(MyService.MESSAGE_TAG, "Hi service");
                message.setData(bundle);

                mServiceMessenger.send(message); //sending message to service

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartServiceBtn = (Button) this.findViewById(R.id.start_btn);
        mStartServiceBtn.setOnClickListener(this);
        mSendMessageBtn = (Button) this.findViewById(R.id.snd_message_btn);
        mSendMessageBtn.setOnClickListener(this);

        mServiceResponseTextView = (TextView) this.findViewById(R.id.service_response_display);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isBound){

            unbindService(serviceConnection);
        }
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if(viewId == R.id.snd_message_btn){

            sendMessage(view);

        }else if(viewId == R.id.start_btn){

            Intent myService = new Intent(getApplicationContext(), MyService.class);
            startService(myService);
            bindService(myService, serviceConnection, Context.BIND_AUTO_CREATE); //Binding to the service!

        }
    }

    /**
     * Responsible for connection of service and activity
     */
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Toast.makeText(MainActivity.this,"Service connected",Toast.LENGTH_SHORT).show();
            isBound = true;
            mServiceMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Toast.makeText(MainActivity.this,"not service connected",Toast.LENGTH_SHORT).show();
            isBound = false;
        }
    };
    /**
     * responsible for receiving data from service
     */
    Messenger replyMessenger = new Messenger(new HandlerReplyMsg());
    class HandlerReplyMsg extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String serviceResponse = msg.obj.toString(); //msg received from service
            mServiceResponseTextView.append("\n"+serviceResponse);

        }
    }
}
