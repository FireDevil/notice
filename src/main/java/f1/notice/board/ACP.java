package f1.notice.board;

import android.app.Application;
import android.content.Context;

import com.android.vending.billing.Purchase;

/**
 * Created by Antec on 17.01.14.
 */
public class ACP extends Application {

    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;
    private static String pub_key;
    private static String ad_removal;
    private static boolean ad_free;

    public static String getAd_removal() {
        return ad_removal;
    }

    public static boolean isAd_free() {
        return ad_free;
    }

    public static void setAd_free(boolean ad_free) {
        ACP.ad_free = ad_free;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
        pub_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAurDlGmJI1SRS3WdePuyORGjCE48uoZwGE27J1VbPnI8McSoCJNFOEFaQFgGWUuqpHgMwqcMyKw/hGSCDWhpPUZx5OIQ+e5mPwKS/b8j6p/x6E4Gc0p/alrdkZMa9pjU4naSttd4cFvF0iNDBwxJa21dQQ7TyZ0d1zvPZ9Rmbd1TMJe+Ze0kMtbFSLBSR0wddodudRUcxWL+YJ8mhFK7nTbp7wAjA2ceEcQsOhmUWkG1ipcCqZeQYiTk3YEpWIN8xawH3ERO09UnXDe20ZTkEJm2hmIz8zFoVeUfBvEGoNlC4CHbHQkkd2/hfjN4YVNWN3LHQoHycYDVJb5qFSJ+NkwIDAQAB";
        ad_removal = "fnb.permanent.ad_removal";
        ad_free = false;
    }

    public static boolean verifyDeveloperPayload(Purchase purchase){
        purchase.getPurchaseState();
        return true;
    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }
    public static String getPub_key() {return pub_key;  }
}
