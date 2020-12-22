package com.example.belarusgeogame;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.example.belarusgeogame.geoobjects.Country;
import com.example.belarusgeogame.geoobjects.GeoObject;

import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private View enlargeBtn, decreaseBtn;
    private TextView textCountry;
    private float scale = 1;
    private float la0 = (float) toRad(27.5);
    private float fi0 = (float) toRad(53.8);
    private Toast toast;
    private List<Country> countries;
    private Country currentGeo = null;
    private Random random;
    private int attempt;


    public static double toRad(double fi) {
        return fi / 180 * Math.PI;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.my_view);
        textCountry = findViewById(R.id.text_country);
        enlargeBtn = findViewById(R.id.enlarge);
        decreaseBtn = findViewById(R.id.decrease);
        enlargeBtn.setOnClickListener(v -> {
            mapView.scale(1.25f);
        });
        decreaseBtn.setOnClickListener(v -> {
            mapView.scale(0.8f);
        });
        random = new Random();

        countries = new ArrayList<>();
        try {
            InputStream stream = getResources().openRawResource(R.raw.world_m);
            GeoJSONObject geoJSON = GeoJSON.parse(stream);
            readCountries(geoJSON, countries);
            //Log.d("FileInputStream", "" + (geoJSON instanceof FeatureCollection));
            mapView.addCountries(countries);
            currentGeo = nextGeoObject();
            textCountry.setText(currentGeo.getName());
            mapView.setOnGeoObjectSelectedListener(obj -> {

                if (toast != null) toast.cancel();
                if (obj == currentGeo || attempt == 3) {
                    attempt = 0;
                    toast = Toast.makeText(this, attempt == 3 ? "OOOOO!" : "That's right!", Toast.LENGTH_SHORT);
                    toast.show();
                    countries.remove(currentGeo);
                    mapView.selectedCountries.add(currentGeo);
                    mapView.invalidate();
                    currentGeo = nextGeoObject();
                    textCountry.setText(currentGeo.getName());
                    return true;
                }
                attempt++;
                toast = Toast.makeText(this, obj == null ? "Nothing" : obj.getName(), Toast.LENGTH_SHORT);
                toast.show();
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Country nextGeoObject() {
        return countries.get(random.nextInt(countries.size()));
    }

    ;

    public PointF project(Position position) {
        return project(toRad(position.getLongitude()), toRad(position.getLatitude()));
    }

    /* public PointF project(double la, double fi) {
         float scale = 1000;
         double x = Math.sin((la - la0) * Math.sin(fi)) / Math.tan(fi);
         double y = fi - fi0 + (1 - Math.cos((la - la0) * Math.sin(fi))) / Math.tan(fi);
         return new PointF((float) x * scale, -(float) y * scale);
     }*/

    public PointF project(double la, double fi) {
        float scale = 1000;
        double x = (la - la0) * Math.cos(fi0);
        double y = fi - fi0;
        return new PointF((float) x * scale, -(float) y * scale);
    }

    /*public PointF project(double la, double fi) {
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
        return new PointF((float) x * scale, -(float) y * scale);
    }*/

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