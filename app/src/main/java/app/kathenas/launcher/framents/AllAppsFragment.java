package app.kathenas.launcher.framents;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import app.kathenas.launcher.AppObject;
import app.kathenas.launcher.R;
import app.kathenas.launcher.adaptors.AllAppsAdaptor;

public class AllAppsFragment extends Fragment {

    private AllAppsAdaptor allAppsAdaptor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_all_apps, container, false);

        RecyclerView allAppsRecyclerView = view.findViewById(R.id.allAppsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        allAppsRecyclerView.setLayoutManager(horizontalLayoutManager);

        allAppsAdaptor = new AllAppsAdaptor(getContext(), getInstalledApps(), app -> {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            startActivity(intent);
        });
        allAppsRecyclerView.setAdapter(allAppsAdaptor);

        return  view;
    }

    private ArrayList<AppObject> getInstalledApps() {
        ArrayList<AppObject> installedApps = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory((Intent.CATEGORY_LAUNCHER));

        List<ResolveInfo> meshedAppsList = getContext().getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo app : meshedAppsList) {
            String appName = app.activityInfo.loadLabel(getActivity().getPackageManager()).toString();
            String packageName = app.activityInfo.packageName;
            Drawable appImage = app.activityInfo.loadIcon(getActivity().getPackageManager());
            AppObject appObject = new AppObject(appName, packageName, appImage);

            if (!installedApps.contains(appObject)) {
                installedApps.add(appObject);
            }
        }
        Collections.sort(installedApps);

        return installedApps;
    }
}