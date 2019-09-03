package jat.vijesh;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {


    public static String getCurrentTime(String timeDatePattern) {
        String formattedDate = "";
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat(timeDatePattern); //  "HH:mm:ss"
        formattedDate = dateFormat.format(date);

        return formattedDate;
    }

    public static void writeLogFileToDevice(Context context, String text) {

        if (context == null)
            return;

        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result != PackageManager.PERMISSION_GRANTED || result2 != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        File fileDir = Environment.getExternalStorageDirectory();
        File logFile = new File(fileDir.getAbsolutePath() + "/", "TestGeoFence.txt");

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(getCurrentTime("yyyy-MM-dd HH:mm:ss a") + " -- " + text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
