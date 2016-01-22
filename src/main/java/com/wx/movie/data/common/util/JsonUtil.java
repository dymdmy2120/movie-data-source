package com.wx.movie.data.common.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class JsonUtil {

  public static JSONObject getJsonObjectFromXml(String xmlData) {
    JSONObject json = null;
    XMLSerializer xmlSerializer = new XMLSerializer();
    json = JSONObject.fromObject(xmlSerializer.read(xmlData));
    return json;
  }

  public static JSONArray getJsonArrayFromXml(String xmlData) {
    JSONArray json = null;
    XMLSerializer xmlSerializer = new XMLSerializer();
    json = JSONArray.fromObject(xmlSerializer.read(xmlData));
    return json;
  }

  /**
   * If JSONObject does not contains the key, JSONException will be thrown.
   * use this method to avoid exception.
   */
  public static String getString(JSONObject json, String key) {
    if (json.containsKey(key)) {
      return json.getString(key);
    } else {
      return null;
    }
  }

}
