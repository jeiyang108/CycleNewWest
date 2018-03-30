package com.project.lyt.cyclenewwest.Handler;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Class which handles calls to local storage.
 *
 */
public class LocalStorageHandler {
    private static final String FILE_BIKEWAYS = "parks_bikeways.txt";
    private static String FILE_GREENWAYS = "parks_greenways.txt";


    /**
     * Saves JSON values to local storage for use when no internet connection is available.
     * @param context
     *       MainActivity context for saving the file.
     * @param jsonString
     *      The JSON string to save.
     * @param greenway
     *      True for saving GreenWay values, false for saving BikeWay values.
     */
    public static void saveJSONToStorage(Context context, String jsonString, boolean greenway) {
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(greenway ? FILE_GREENWAYS : FILE_BIKEWAYS, Context.MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }

    /**
     * Gets a JSON string from local storage.
     * @param context
     *      MainActivity context for retrieving files.
     * @param greenway
     *      True for GreenWay file, false for BikeWay file.
     * @return
     *      String of JSON data, if found.
     */
    public static String getJSONFromStorage(Context context, boolean greenway) {
        FileInputStream inputStream;

        try {
            inputStream = context.openFileInput(greenway ? FILE_GREENWAYS : FILE_BIKEWAYS);
            byte[] byteStream = new byte[inputStream.available()];
            inputStream.read(byteStream);

            String returnString = new String(byteStream);

            return returnString;
        } catch (Exception e) {
        }

        return "";
    }
}
