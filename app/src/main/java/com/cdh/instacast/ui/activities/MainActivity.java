package com.cdh.instacast.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.cdh.instacast.R;
import com.cdh.instacast.pojo.InstagramMedia;
import com.cdh.instacast.restapi.RestManager;
import com.cdh.instacast.restapi.RxTransformer;
import com.cdh.instacast.ui.adapters.InstagramMediaAdapter;
import com.cdh.instacast.utils.Consts;
import com.cdh.instacast.utils.LocationHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mediaList) RecyclerView mInstagramMediaList;
    @BindView(R.id.radiusBtn) FloatingActionButton mRadiusBtn;
    @BindView(R.id.progress_bar) ProgressBar mProgressbar;

    private InstagramMediaAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription;
    private int mRadius = Consts.DEFAULT_DISTANCE;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getLocation();
    }

    @OnClick(R.id.radiusBtn)
    public void onRadiusBtnClick(View view){
        if(mLocation != null) {
            showRadiusDialog();
        } else {
            showMassageDialog("Pleas enable your GPS and open the app again.");
        }
    }

    private void getLocation() {
        if(weHaveLocationPermission()) {

            LocationHelper locationHelper = new LocationHelper(this);

            locationHelper.getCurrentLocation(new LocationHelper.OnLocationReady() {
                @Override
                public void onLocationReady(Location location) {
                    mLocation = location;
                    getImagesByLocation();
                }

                @Override
                public void onLocationDisabled() {
                    showMassageDialog("Pleas enable your GPS and open the app again.");
                }
            });

        } else {
            requestForLocationPermission();
        }
    }

    private void getImagesByLocation() {
        // init CompositeSubscription
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }

        // clear prev subscriptions
        mCompositeSubscription.clear();

        // execute instagram media request
        mCompositeSubscription.add(RestManager.get().api().getImageByLocation(mLocation.getLatitude(), mLocation.getLongitude(), mRadius)
                .compose(RxTransformer.applyIOSchedulers())
                .subscribe(instagramMedia -> setMediaList(instagramMedia), MainActivity.this::onError));

    }

    private boolean weHaveLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Consts.PERMISSION_ACCESS_FINE_LOCATION);
    }

    private void setMediaList(InstagramMedia instagramMedia) {

        if(mAdapter == null){

            // set layout manager
            mInstagramMediaList.setLayoutManager(new LinearLayoutManager(this));

            // set adapter
            mAdapter = new InstagramMediaAdapter();
            mInstagramMediaList.setAdapter(mAdapter);
        }

        if(instagramMedia.getData().isEmpty()){

            if(mRadius < Consts.MAX_DISTANCE){

                mRadius = mRadius * 2; // multiple distance for better results
                getImagesByLocation();     // try again to get images

            } else {

                mRadius = Consts.MAX_DISTANCE;
                showMassageDialog("Nothing to show around you :(");

            }

        } else {
            mProgressbar.setVisibility(View.GONE);
            mInstagramMediaList.setVisibility(View.VISIBLE);
            mAdapter.setMediaList(instagramMedia);
        }
    }

    private void onError(Throwable t) {
        showMassageDialog("oops something went wrong :(");
    }

    private void showRadiusDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_radius, null);
        builder.setView(v)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> getImagesByLocation())
                .setNegativeButton(android.R.string.cancel, null)
           .setTitle("Set Search Radius");

        // radius title
        AppCompatTextView currentRadiusTxt = v. findViewById(R.id.dialog_radius_text);
        currentRadiusTxt.setText(String.format("Radius: %dm", mRadius));
        SeekBar sbBetVal = v.findViewById(R.id.dialog_radius_seek_bar);
        sbBetVal.setMax(Consts.MAX_DISTANCE);
        sbBetVal.setProgress(mRadius);
        sbBetVal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                currentRadiusTxt.setText(String.format("Radius: %dm", progress));
                mRadius = progress;
            }
        });

        builder.create();
        builder.show();

    }

    private void showMassageDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder .setTitle("InstaCast")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish());

        builder.create();
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Consts.PERMISSION_ACCESS_FINE_LOCATION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // get location
                getLocation();

            } else {

                // permission denied, boo!
                showMassageDialog("This App Need Location Permission To Work!");

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()){
            mCompositeSubscription.unsubscribe();
        }
    }
}
