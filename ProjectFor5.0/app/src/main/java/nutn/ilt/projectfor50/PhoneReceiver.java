package nutn.ilt.projectfor50;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Mu on 2017/10/31.
 */

public class PhoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //去電
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            String PhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Toast.makeText(context, "Outgoing Number: " + PhoneNumber, Toast.LENGTH_LONG).show();
            //設置定時器
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            final long[] vibrate_effect = {0, 1000, 1000, 1000};
            final int DoneTime = 5000;
            int periodTime = 1000;
            builder.setContentTitle("警告!!!")
                    .setContentText("通話時間已達設定之時間 " + DoneTime / 1000 + "秒")
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setVibrate(vibrate_effect);
            boolean myb = Menu3.isChecked();
            /*new CountDownTimer(DoneTime, periodTime) {
                @Override
                public void onTick(long l) {
                }
                @Override
                public void onFinish() {
                    nm.notify(0, builder.build());
                    vibrator.vibrate(DoneTime);
                }
            }.start();*/

            if (myb==true){
                new CountDownTimer(DoneTime, periodTime) {
                    @Override
                    public void onTick(long l) {
                    }
                    @Override
                    public void onFinish() {
                        nm.notify(0, builder.build());
                        vibrator.vibrate(DoneTime);
                    }
                }.start();
            }
            else {
                new CountDownTimer(DoneTime, periodTime){
                    @Override
                    public void onTick(long l) {
                    }
                    @Override
                    public void onFinish() {
                        nm.notify(0, builder.build());
                    }
                }.start();
            }
        }
    }

}
