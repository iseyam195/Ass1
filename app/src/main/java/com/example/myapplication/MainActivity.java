package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import android.os.Handler;


import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
public class MainActivity extends AppCompatActivity {
    private String videoURL = "https://r3---sn-25auxa-b15l.googlevideo.com/videoplayback?expire=1590708153&ei=WfPPXvaTF8_zhwaP9oDAAQ&ip=23.236.138.247&id=o-AJh-oxbDWjhFPPLbPAR9ZhFGrWlx_bRmFh5fPczbPF3g&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&gir=yes&clen=234418577&ratebypass=yes&dur=3520.934&lmt=1580747613112093&fvip=3&c=WEB&txp=5531432&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRgIhAKztEX2sqab8pUYElidFOCZ-fPXgJwUgPweLL-OyzhdwAiEA0EHxFCxXqglr9GXs_i7v6fnYQmfaem8eFFmS5TwRUzk%3D&video_id=lCOF9LN_Zxs&title=Beautiful+Piano+Music%2C+Vol.+1+%7E+Relaxing+Music+for+Studying%2C+Relaxation+or+Sleeping&redirect_counter=1&rm=sn-ab5yl7s&req_id=252d26278179a3ee&cms_redirect=yes&ipbypass=yes&mh=jb&mip=82.205.28.194&mm=31&mn=sn-25auxa-b15l&ms=au&mt=1590686917&mv=m&mvi=2&pl=20&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRAIgE7qWWlpQz68ltGXGt9Ok4Xu-xzEtAjJSaibm1OQRD94CIBIQYNc_S8BbX6uuUnktZlmHPSGXR_gZNkat82d6fA2g";
   private PlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                100);
        playerView = findViewById(R.id.player_view);
    }
    private void initializePlayer() {

        TrackSelection.Factory adaptiveTrackSelection =
                new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        player = ExoPlayerFactory.newSimpleInstance(this,
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(adaptiveTrackSelection),
                new DefaultLoadControl());

        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);


        Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);


        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private MediaSource buildMediaSource(final Uri uri) {
        DefaultBandwidthMeter defaultBandwidthMeter =
                new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Video Exo Player"), defaultBandwidthMeter);

        Handler mainHandler = new Handler();
        MediaSource mediaSource2 = new HlsMediaSource(uri,
                dataSourceFactory, mainHandler, null);



        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }
}
