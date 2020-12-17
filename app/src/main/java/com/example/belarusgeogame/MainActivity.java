package com.example.belarusgeogame;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);
        try {
            InputStream stream = getResources().openRawResource(R.raw.coordinates);

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
                }
                else if(feature.getGeometry().getType() == "Polygon"){
                    Polygon polygon = (Polygon) feature.getGeometry();
                    List<Ring> rings = polygon.getRings();
                    for (Ring ring : rings) {
                        for (Position position : ring.getPositions()) {
                            position.getLatitude();
                        }
                    }
                }
            }
            textView.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}