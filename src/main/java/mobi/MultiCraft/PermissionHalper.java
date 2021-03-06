package mobi.MultiCraft;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static mobi.MultiCraft.PreferencesHelper.getLaunchTimes;

class PermissionHalper {
    private Activity activity;
    private SharedPreferences sharedPreferences;
    static ArrayList<String> permissionsToRequest;
    static ArrayList<String> permissionsRejected;

    PermissionHalper(Activity activity) {
        this.activity = activity;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    String[] requestPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(WRITE_EXTERNAL_STORAGE);
        if (getLaunchTimes() > 2) {
            permissions.add(ACCESS_COARSE_LOCATION);
        }
  
        permissionsToRequest = findUnAskedPermissions(permissions);
        permissionsRejected = findRejectedPermissions(permissions);
        if (permissionsToRequest.size() > 0) {
            for (String perm : permissionsToRequest) {
                markAsAsked(perm);
            }
            return permissionsToRequest.toArray(new String[permissionsToRequest.size()]);
        } else if (permissionsRejected.size() > 0 && getLaunchTimes() % 3 == 0) {
            return permissionsRejected.toArray(new String[permissionsRejected.size()]);
        }
        return new String[]{};
    }

    boolean hasPermission(String permission) {
        return (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean shouldWeAsk(String permission) {
        return sharedPreferences.getBoolean(permission, true);
    }

    private void markAsAsked(String permission) {
        sharedPreferences.edit().putBoolean(permission, false).apply();
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && shouldWeAsk(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private ArrayList<String> findRejectedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && !shouldWeAsk(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
}

