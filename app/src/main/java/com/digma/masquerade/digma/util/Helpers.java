package com.digma.masquerade.digma.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by janidham on 05/01/16.
 */
public class Helpers {

    public static void renderMessage(String message, int time, Context context) {
        Toast toast = Toast.makeText(context, message, time);
        toast.show();
    }
}
