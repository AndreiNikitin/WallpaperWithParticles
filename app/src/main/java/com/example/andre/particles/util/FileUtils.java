package com.example.andre.particles.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Andrei Nikitin
 */
public final class FileUtils {
    public static String readTextFromRaw(Context context, int resourceId){
        StringBuilder builder = new StringBuilder("");

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(resourceId)))){
            while(reader.ready()){
                builder.append(reader.readLine());
                builder.append(System.lineSeparator());
            }
        } catch (IOException ex){
            throw new RuntimeException("Could not open resource: " + resourceId, ex);
        } catch (Resources.NotFoundException ex){
            throw new RuntimeException("Resource not found: " + resourceId, ex);
        }

        return builder.toString();
    }
}
