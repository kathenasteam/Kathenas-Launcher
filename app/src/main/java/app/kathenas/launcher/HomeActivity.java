package app.kathenas.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.kathenas.launcher.adaptors.AllAppsAdaptor;
import app.kathenas.launcher.framents.AllApps;
import app.kathenas.launcher.framents.Personalise_Fragment;

import static android.Manifest.permission.PACKAGE_USAGE_STATS;
import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private AllAppsAdaptor allAppsAdaptor;
    AppObject weatherApp;
    AppObject calenderApp;
    AppObject playStoreApp;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        getInstalledApps();

        dialog = new Dialog(this);

        ConstraintLayout googleSearch = findViewById(R.id.googleSearch);
        ConstraintLayout weatherInfo = findViewById(R.id.weatherInfo);
        ConstraintLayout calenderInfo = findViewById(R.id.calenderInfo);
        TextView calenderInfoText = findViewById(R.id.calenderInfoText);
        ConstraintLayout newAppsInfo = findViewById(R.id.newAppsInfo);
        ConstraintLayout personaliseLayout = findViewById(R.id.personaliseLayout);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
        String date = dateFormat.format(calendar.getTime());
        calenderInfoText.setText(date);

        googleSearch.setOnClickListener(this);
        weatherInfo.setOnClickListener(this);
        calenderInfo.setOnClickListener(this);
        newAppsInfo.setOnClickListener(this);
        personaliseLayout.setOnClickListener(this);


        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment allAppsFragment = new AllApps();
        fragTransaction.add(R.id.allAppsLayout, allAppsFragment , "allAppsFragment");
        fragTransaction.commit();

    }

    private ArrayList<AppObject> getInstalledApps() {
        ArrayList<AppObject> installedApps = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory((Intent.CATEGORY_LAUNCHER));

        List<ResolveInfo> meshedAppsList = getApplicationContext().getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo app : meshedAppsList) {
            String appName = app.activityInfo.loadLabel(getPackageManager()).toString();
            String packageName = app.activityInfo.packageName;
            Drawable appImage = app.activityInfo.loadIcon(getPackageManager());
            AppObject appObject = new AppObject(appName, packageName, appImage);

            if (!installedApps.contains(appObject)) {
                installedApps.add(appObject);
                
                if (appName.toLowerCase().contains("weather")) {
                    weatherApp = appObject;
                }
                if (appName.toLowerCase().contains("calender")) {
                    calenderApp = appObject;
                }
                if (appName.toLowerCase().contains("play store")) {
                    playStoreApp = appObject;
                }
            }
        }

        return installedApps;
    }

    private void getRecentAppList() {
        Log.e("recent AppInfo", "FF");
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  System.currentTimeMillis(),  System.currentTimeMillis());
        for (UsageStats app : appList) {
            Log.e("recent AppInfo", app.getPackageName());

            // String packageName = appProcessInfo.importanceReasonComponent.getPackageName();


            // Log.e("recent AppInfo", packageName);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.googleSearch:

                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, "");
                startActivity(intent);

                break;

            case R.id.calenderInfo:
                Intent calIntent = new Intent(Intent.ACTION_MAIN);
                calIntent.addCategory(Intent.CATEGORY_APP_CALENDAR);
                startActivity(calIntent);
                break;

            case R.id.weatherInfo:
                if (weatherApp != null) {
                    Intent weatherIntent = getPackageManager().getLaunchIntentForPackage(weatherApp.getPackageName());
                    startActivity(weatherIntent);
                } else {
                    view.setVisibility(View.GONE);
                }
                break;

            case R.id.newAppsInfo:
                if (playStoreApp != null) {
                    Intent newAppsIntent = getPackageManager().getLaunchIntentForPackage(playStoreApp.getPackageName());
                    startActivity(newAppsIntent);
                } else {
                    view.setVisibility(View.GONE);
                }
                break;

            case R.id.personaliseLayout:
                DialogFragment dialogFragment = new Personalise_Fragment();
                dialogFragment.show(getSupportFragmentManager(),null);
                Log.e("dialog","dialogHome");

                break;
            default:
                // code block
        }
    }


    private boolean getPermission() {
        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getApplicationContext().getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            return (this.checkCallingOrSelfPermission(PACKAGE_USAGE_STATS) == PERMISSION_GRANTED);
        } else {
            return (mode == MODE_ALLOWED);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!getPermission()) {
            Log.e("getting permission","not done");
            View settingsView = LayoutInflater.from(this).inflate(R.layout.ask_permission,null);

            dialog.setContentView(settingsView);
            dialog.setCanceledOnTouchOutside(false);
            Button settings = settingsView.findViewById(R.id.settings);
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                  Uri uri = Uri.fromParts("package", getPackageName(), null);
                  intent.setData(uri);
                  startActivity(intent);

                }
            });

            dialog.show();
        }else{
            dialog.dismiss();
            Log.e("getting permission","done");
            getRecentAppList();
        }

    }

}