package com.example.belarusgeogame;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.MultiPolygon;
import com.cocoahero.android.geojson.Polygon;
import com.cocoahero.android.geojson.Position;
import com.cocoahero.android.geojson.Ring;

import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyView myView;
    private int scale = 2000;
    private float la0 = (float) toRad(27);
    private float fi0 = (float) toRad(48);

    public static double toRad(double fi) {
        return fi / 180 * Math.PI;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myView = findViewById(R.id.my_view);
        List<Country> countries = new ArrayList<>();
        try {
            InputStream stream = getResources().openRawResource(R.raw.world_l);
            GeoJSONObject geoJSON = GeoJSON.parse(stream);
            readCountries(geoJSON, countries);
            //Log.d("FileInputStream", "" + (geoJSON instanceof FeatureCollection));
            myView.addCountries(countries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PointF project(Position position) {
        return project(toRad(position.getLongitude()), toRad(position.getLatitude()));
    }

    public PointF project(double la, double fi) {
        double x = Math.sin((la - la0) * Math.sin(fi)) / Math.tan(fi);
        double y = fi - fi0 + (1 - Math.cos((la - la0) * Math.sin(fi))) / Math.tan(fi);
        return new PointF((float) x * scale, (float) (1 - y) * scale);
    }

    public void readPolygon(Polygon polygon, List<PointF[]> dest) {
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

    public void readCountries(GeoJSONObject geoJSON, List<Country> countries) throws JSONException {
        FeatureCollection featureCollection = (FeatureCollection) (geoJSON);
        List<Feature> features = featureCollection.getFeatures();
        int p = 0;
        for (Feature feature : features) {
            Country country = new Country(feature.getProperties().getString("sovereignt"));
            List<PointF[]> border = new ArrayList<>();
            Log.d("Country", country.getName() + " " + feature.getGeometry().getType());
              /*  if (feature.getGeometry().getType() == "Point") {
                    Point point = (Point) feature.getGeometry();
                    point.getPosition().getLatitude();
                    text += point.getPosition().getLatitude() + " " + point.getPosition().getLongitude() + " " + point.getPosition().getAltitude();
                } else*/
            if (feature.getGeometry().getType() == "Polygon") {
                readPolygon((Polygon) feature.getGeometry(), border);
                Log.d("Polygon", "");
            } else if (feature.getGeometry().getType() == "MultiPolygon") {
                MultiPolygon multiPolygon = (MultiPolygon) feature.getGeometry();
                List<Polygon> polygons = multiPolygon.getPolygons();
                for (Polygon polygon : polygons) {
                    readPolygon(polygon, border);
                }
            }
            Log.d("Points", "Points count " + p);
            country.setBorder(border);
            countries.add(country);
        }
        Toast.makeText(this, countries.get(0).getName(), Toast.LENGTH_LONG);
    }
}