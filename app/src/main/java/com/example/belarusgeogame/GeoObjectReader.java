package com.example.belarusgeogame;

import android.graphics.PointF;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.MultiPolygon;
import com.cocoahero.android.geojson.Point;
import com.cocoahero.android.geojson.Polygon;
import com.cocoahero.android.geojson.Position;
import com.cocoahero.android.geojson.Ring;
import com.example.belarusgeogame.geometries.PolygonG;
import com.example.belarusgeogame.geoobjects.Country;
import com.example.belarusgeogame.geoobjects.GeoObject;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GeoObjectReader {
    private static final String STRING_NAME = /*"STATE_NAME"*/"admin";
    private static final String STRING_TYPE = "type";
    private static float scale = 1;
    private static float la0 = (float) toRad(27.5);
    private static float fi0 = (float) toRad(53.8);

    public static double toRad(double fi) {
        return fi / 180 * Math.PI;
    }

    public static PointF project(double la, double fi) {
        float scale = 2000;
        double x = (la - la0) * Math.cos(fi0);
        double y = fi - fi0;
        return new PointF((float) x * scale, -(float) y * scale);
    }

    /* public static PointG project(double la, double fi) {
         float scale = 1000;
         double x = Math.sin((la - la0) * Math.sin(fi)) / Math.tan(fi);
         double y = fi - fi0 + (1 - Math.cos((la - la0) * Math.sin(fi))) / Math.tan(fi);
         return new PointG((float) x * scale, -(float) y * scale);
     }*/

    /*public static PointG project(double la, double fi) {
        float scale = 1000;
        double a = Math.cos(la - la0);
        double b = Math.sin(la - la0);
        double c = Math.cos(fi);
        double d = Math.sin(fi);
        double e = Math.cos(fi0);
        double f = Math.sin(fi0);
        double k = Math.sqrt(2 / (1 + f * d + e * c * a));
        double x = k * c * b;
        double y = k * (e * d - f * c * a);
        return new PointG((float) x * scale, -(float) y * scale);
    }*/

    public PointF project(Position position) {
        return project(toRad(position.getLongitude()), toRad(position.getLatitude()));
    }

    public void readPolygon(com.cocoahero.android.geojson.Polygon polygon, List<PointF[]> dest) {
        List<Ring> rings = polygon.getRings();
        for (Ring ring : rings) {
            List<Position> positions = ring.getPositions();
            int n = positions.size();
            PointF[] points = new PointF[n];
            for (int i = 0; i < positions.size(); i++) {
                points[i] = project(positions.get(i));
            }
            dest.add(points);
        }
    }

    public void readGeoObjects(InputStream stream, List<GeoObject> geoObjects) throws JSONException, IOException {
        GeoJSONObject geoJSON = GeoJSON.parse(stream);
        FeatureCollection featureCollection = (FeatureCollection) (geoJSON);
        List<Feature> features = featureCollection.getFeatures();
        for (Feature feature : features) {
            String type = feature.getProperties().getString(STRING_TYPE);
            String name = feature.getProperties().getString(STRING_NAME);
            Log.d("Country", name + " " + type);
            if (type.compareTo("Sovereign country") == 0 || type.compareTo("Country") == 0) {
                GeoObject geoObject = new Country(feature.getProperties().getString(STRING_NAME));
                List<PointF[]> polygonsF = new ArrayList<>();
                if (feature.getGeometry().getType() == "PointG") {
                    Point point = (Point) feature.getGeometry();
                    point.getPosition().getLatitude();
                } else if (feature.getGeometry().getType() == "Polygon") {
                    readPolygon((Polygon) feature.getGeometry(), polygonsF);
                } else if (feature.getGeometry().getType() == "MultiPolygon") {
                    MultiPolygon multiPolygon = (MultiPolygon) feature.getGeometry();
                    List<Polygon> polygons = multiPolygon.getPolygons();
                    for (Polygon polygon : polygons) {
                        readPolygon(polygon, polygonsF);
                    }
                }
                geoObject.setGeometry(new PolygonG(polygonsF));
                geoObjects.add(geoObject);
            }
        }
        //Toast.makeText(this, countries.get(0).getName(), Toast.LENGTH_LONG);
    }
}
