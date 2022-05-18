package com.example.aramtracker.properties;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import no.stelar7.api.r4j.basic.APICredentials;

public class Props {

    private final Properties properties = new Properties();
    private final static String PROPERTIES_FILE_NAME = "config.properties";
    private final static String LOL_API_KEY = "lol.api-key";

    private final Context context;

    public Props(Context context) {
        this.context = context;
        loadProperties();
    }

    public void loadProperties() {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(PROPERTIES_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLolApiKey() {
        return properties.getProperty(LOL_API_KEY);
    }

    public APICredentials getApiCredentials() {
        return new APICredentials(getLolApiKey(), null, null, null, null);
    }
}