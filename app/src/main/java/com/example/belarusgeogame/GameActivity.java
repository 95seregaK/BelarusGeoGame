package com.example.belarusgeogame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.belarusgeogame.geoobjects.GeoObject;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private MapView mapView;
    private View enlargeBtn, decreaseBtn;
    private TextView textCurrentGeoobject;
    private List<GeoObject> geoObjects;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mapView = findViewById(R.id.my_view);
        textCurrentGeoobject = findViewById(R.id.text_current_geo);
        enlargeBtn = findViewById(R.id.enlarge);
        decreaseBtn = findViewById(R.id.decrease);
        enlargeBtn.setOnClickListener(v -> {
            game.scale(1.25f);
        });
        decreaseBtn.setOnClickListener(v -> {
            game.scale(0.8f);
        });
        geoObjects = new ArrayList<>();
        GeoObjectReader reader = new GeoObjectReader();
        try {
            reader.readGeoObjects(getResources().openRawResource(R.raw.europe_m), geoObjects);
        } catch (Exception e) {
            Log.d("MyException", e.getMessage());
            e.printStackTrace();
        }
        game = new Game(geoObjects, mapView);
        game.setAttemptCallback((res) -> {
            if (res != Game.CODE_END)
                textCurrentGeoobject.setText(game.getCurrentGeoObject().getName());
            else textCurrentGeoobject.setText("Game over!");
        });
        game.startGame();
        if (game.getCurrentGeoObject() != null)
            textCurrentGeoobject.setText(game.getCurrentGeoObject().getName());
    }

}