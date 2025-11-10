package com.nhom3.personalfinance.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Xử lý khi nhận thông báo
        Toast.makeText(context, "Đã nhận thông báo!", Toast.LENGTH_SHORT).show();
    }
}
