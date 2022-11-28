package com.varanegar.vaslibrary.ui.fragment.new_fragment.videoPlayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.validation.FormValidator;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.LoginFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

import java.util.UUID;

public class MediaPlayerFragment extends VaranegarFragment {
    StyledPlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.layout_fragment_mediaplayer, container, false);
        playerView = view.findViewById(R.id.playerView);
        String url=getArguments().getString("urlPlayer","");

        simpleExoPlayer = new SimpleExoPlayer.Builder(getContext()).build();
        playerView.setPlayer(simpleExoPlayer);
        MediaItem mediaItem = MediaItem
                .fromUri(url);
        simpleExoPlayer.addMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void   releasePlayer(){
        simpleExoPlayer.stop();
        simpleExoPlayer.clearMediaItems();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
    }
}
