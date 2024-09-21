import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static final String NOTIFICATION_PREFS = "notifications";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Armazena a notificação quando recebida
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        saveNotification(title, message);
    }

    // Armazena a notificação nas SharedPreferences
    private void saveNotification(String title, String message) {
        SharedPreferences prefs = getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String existingNotifications = prefs.getString("notifications_list", "");
        String newNotification = title + ": " + message + "\n";
        editor.putString("notifications_list", existingNotifications + newNotification);
        editor.apply();
    }
}
