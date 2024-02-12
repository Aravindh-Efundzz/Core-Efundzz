package com.efundzz.utils;

import org.json.JSONObject;
import org.json.XML;

public class XmlJsonUtils {
    public static JSONObject xmlStringToJson(String xmlString) {
        try {
            return XML.toJSONObject(xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fetchStringValueFromJson(JSONObject jsonObject, String... keys) {
        JSONObject currentObject = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            currentObject = currentObject.getJSONObject(keys[i]);
        }
        return currentObject.getString(keys[keys.length - 1]);
    }

    public static int fetchIntValueFromJson(JSONObject jsonObject, String... keys) {
        JSONObject currentObject = jsonObject;
        for (int i = 0; i < keys.length - 1; i++) {
            currentObject = currentObject.getJSONObject(keys[i]);
        }
        return currentObject.getInt(keys[keys.length - 1]);
    }
}
