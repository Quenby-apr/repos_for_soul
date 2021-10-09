package com.example.lab_1;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonHelper {
    @RequiresApi(api = Build.VERSION_CODES.O)
    static ArrayList<MyObject> importFromJSON(Context context) throws IOException {
        ByteArrayOutputStream outputStream = null;
        byte buf[] = new byte[1024];
        int len;
        try (InputStream XmlFileInputStream = context.getResources().openRawResource(context.getResources().getIdentifier("my_object", "raw", context.getPackageName()))){ //try with res
            outputStream = new ByteArrayOutputStream();
            while ((len = XmlFileInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        MyObject[] objs = gson.fromJson(outputStream.toString(), MyObject[].class);
        return new ArrayList<>(Arrays.asList(objs));
    }
}
