package com.example.belarusgeogame;

import android.graphics.Path;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Point;
import com.cocoahero.android.geojson.Polygon;
import com.cocoahero.android.geojson.Position;
import com.cocoahero.android.geojson.Ring;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyView myView;
    private int scale = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Path path = new Path();
        myView = findViewById(R.id.my_view);

        try {
            InputStream stream = getResources().openRawResource(R.raw.europe);

            GeoJSONObject geoJSON = GeoJSON.parse(stream);
            String text = "";
            Log.d("FileInputStream", "" + (geoJSON instanceof FeatureCollection));
            FeatureCollection featureCollection = (FeatureCollection) (geoJSON);
            List<Feature> features = featureCollection.getFeatures();
            for (Feature feature : features) {
                if (feature.getGeometry().getType() == "Point") {
                    Point point = (Point) feature.getGeometry();
                    point.getPosition().getLatitude();
                    text += point.getPosition().getLatitude() + " " + point.getPosition().getLongitude() + " " + point.getPosition().getAltitude();
                } else if (feature.getGeometry().getType() == "Polygon") {
                    Polygon polygon = (Polygon) feature.getGeometry();
                    List<Ring> rings = polygon.getRings();
                    for (Ring ring : rings) {
                        PointF point = project(ring.getPositions().get(0));
                        path.moveTo(point.x, point.y);
                        for (Position position : ring.getPositions()) {
                            point = project(position);
                            path.lineTo(point.x, point.y);
                        }
                    }
                }
            }
            //textView.setText(text);
            myView.addPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PointF project(Position position) {
        float x = (float) (scale * (position.getLongitude() - 23));
        float y = (float) (1.5 * scale * (57 - position.getLatitude()));
        return new PointF(x, y);
    }
}