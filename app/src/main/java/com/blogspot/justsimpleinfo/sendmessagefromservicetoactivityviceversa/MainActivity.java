package com.blogspot.justsimpleinfo.sendmessagefromservicetoactivityviceversa;

import android.app.ActivityManager;
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
    TextView mServiceStatusTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartServiceBtn = (Button) this.findViewById(R.id.start_btn);
        mStartServiceBtn.setOnClickListener(this);
        mSendMessageBtn = (Button) this.findViewById(R.id.snd_message_btn);
        mSendMessageBtn.setOnClickListener(this);

        mServiceResponseTextView = (TextView) this.findViewById(R.id.service_response_display);
        mServiceStatusTextView = (TextView) this.findViewById(R.id.service_status_textview);

        if(isMyServiceRunning(MyService.class)){

            mServiceStatusTextView.setText("Service is running");

            Intent myService = new Intent(getApplicationContext(), MyService.class);
            bindService(myService, serviceConnection, Context.BIND_AUTO_CREATE);
        }

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
    public void sendMessage(View view) {
        if (isBound) {
            try {


                Message message = Message.obtain(null, MyService.MESSAGE, 1, 1);
                message.replyTo = replyMessenger;

                MessageData messageData = new MessageData();
                messageData.setMessage("Hello Service!!!");

                Bundle bundle = new Bundle();

                bundle.putParcelable(MyService.MESSAGE_TAG, messageData);

                message.setData(bundle);

                mServiceMessenger.send(message); //sending message to service

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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


            mServiceStatusTextView.setText("Service is running and bounded");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Toast.makeText(MainActivity.this,"not service connected",Toast.LENGTH_SHORT).show();
            isBound = false;

            mServiceStatusTextView.setText("Service is not bounded");
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
           // String serviceResponse = msg.obj.toString(); //msg received from service
            Bundle bundle =  msg.getData();
            bundle.setClassLoader(MessageData.class.getClassLoader());
            MessageData messageData = bundle.getParcelable(MyService.MESSAGE_TAG);

            mServiceResponseTextView.append("\n"+ messageData.getMessage());

        }
    }
}
