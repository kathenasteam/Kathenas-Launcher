package app.kathenas.launcher;

import android.graphics.drawable.Drawable;

public class AppObject {
    private String appName,packageName;
    private Drawable appImage;

    public AppObject(String appName, String packageName, Drawable appImage){
        this.appName = appName;
        this.packageName = packageName;
        this.appImage = appImage;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getAppImage() {
        return appImage;
    }

}
