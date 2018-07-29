package com.myroutes.helper;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by tsmud on 28-12-2016.
 */

public class GpxCreator {
  public static ArrayList<LatLng> decodeGPX(Context context, File file) {
    //TODO runnable
    ArrayList<LatLng> path = new ArrayList<>();

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      FileInputStream fileInputStream = new FileInputStream(file);
      Document document = documentBuilder.parse(fileInputStream);
      Element elementRoot = document.getDocumentElement();

      NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");

      for(int i = 0; i < nodelist_trkpt.getLength(); i++){
        Node node = nodelist_trkpt.item(i);
        NamedNodeMap attributes = node.getAttributes();

        String newLatitude = attributes.getNamedItem("lat").getTextContent();
        Double newLatitude_double = Double.parseDouble(newLatitude);

        String newLongitude = attributes.getNamedItem("lon").getTextContent();
        Double newLongitude_double = Double.parseDouble(newLongitude);

        LatLng newLocation = new LatLng(newLatitude_double, newLongitude_double);

        path.add(newLocation);
      }

      fileInputStream.close();

    } catch (Exception e) {
      Toast.makeText(context.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
    return path;
  }
  public static void encodeGPX(Context context, File file, String n, ArrayList<LatLng> points) {
    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MyRoutes for Android\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
    String name = "<name>" + n + "</name>\n<trkseg>\n";

    String segments = "";
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    //7 days ago
    long timestamp = new Date().getTime();
    for (LatLng l : points) {
      if (l.latitude != -1) {
        segments += "<trkpt lat=\"" + l.latitude + "\" lon=\"" + l.longitude + "\"><time>" + df.format(timestamp) + "</time></trkpt>\n";
      }
    }
    String footer = "</trkseg></trk></gpx>";

    try {
      FileWriter writer = new FileWriter(file, false);
      writer.append(header);
      writer.append(name);
      writer.append(segments);
      writer.append(footer);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      Toast.makeText(context.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
  }
}
