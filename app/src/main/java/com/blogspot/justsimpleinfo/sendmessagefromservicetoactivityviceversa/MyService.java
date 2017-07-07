package com.blogspot.justsimpleinfo.sendmessagefromservicetoactivityviceversa;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Lauro-PC on 7/5/2017.
 */

public class MyService extends Service {

    Messenger mainActivityMessanger;
    final static int MESSAGE = 1;
    final static String MESSAGE_TAG = "message";
    Messenger messenger = new Messenger(new IncomingHandler());

    /**
     * Reponsible for receiving message from activity
     */
    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == MESSAGE) {
                Bundle bundle = msg.getData();

                bundle.setClassLoader(MessageData.class.getClassLoader()); //important
                MessageData messageData = bundle.getParcelable(MyService.MESSAGE_TAG);
                Toast.makeText(MyService.this,"Message From Activity :"+ messageData.getMessage(),Toast.LENGTH_SHORT).show();
                /**
                 * set mainActivityMessanger
                 * important
                 */
                mainActivityMessanger = msg.replyTo;
                replyToActivity();
            }
        }
    }

    private void replyToActivity() {
        // do stuff

        if (mainActivityMessanger != null)
            try {

                MessageData messageData = new MessageData();
                messageData.setMessage("Hello Activity");

                Bundle bundle = new Bundle();
                bundle.putParcelable(MyService.MESSAGE_TAG, messageData);

                Message message = new Message();
                message.setData(bundle);
                mainActivityMessanger.send(message);//replying / sending msg to activity

            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        /**
         * important
         */
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Toast.makeText(this,"Service Started",Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }
}
