package com.example.bucketapptest.Service;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyQSTileService extends TileService {
    public MyQSTileService() {
        super();
        Log.d(">>>", "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(">>>", "onDestroy()");
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.d(">>>", "onTileAdded()");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Log.d(">>>", "onTileRemoved()");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.d(">>>", "onStartListening()");
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Log.d(">>>", "onStopListening()");
    }

    @Override
    public void onClick() {
        super.onClick();

        Tile tile = getQsTile(); // get Instance.
        switch (tile.getState()) {
            case Tile.STATE_ACTIVE:
                tile.setState(Tile.STATE_INACTIVE);
                break;
            case Tile.STATE_INACTIVE:
                tile.setState(Tile.STATE_ACTIVE);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tile);
        }
        Log.d(">>>", "onClick: tile.state: " + tile.getState());
        tile.updateTile();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
