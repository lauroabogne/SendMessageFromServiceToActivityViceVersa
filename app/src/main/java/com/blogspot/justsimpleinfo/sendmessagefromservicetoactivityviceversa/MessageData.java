package com.blogspot.justsimpleinfo.sendmessagefromservicetoactivityviceversa;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lauro-PC on 7/8/2017.
 */

public class MessageData implements Parcelable {

    String mMessage;

    protected MessageData(){

    }
    protected MessageData(Parcel in) {

        mMessage = in.readString();
    }


    public static final Creator<MessageData> CREATOR = new Creator<MessageData>() {
        @Override
        public MessageData createFromParcel(Parcel in) {
            return new MessageData(in);
        }

        @Override
        public MessageData[] newArray(int size) {
            return new MessageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMessage);
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }
}
