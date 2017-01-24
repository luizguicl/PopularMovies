package com.luizguilherme.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.models.Trailer;

import java.util.List;

import static com.luizguilherme.popularmovies.Constants.YOUTUBE_VIDEO_PARAMETER;
import static com.luizguilherme.popularmovies.Constants.YOUTUBE_WATCH_BASE_URL;

public class TrailersAdapter extends ArrayAdapter<Trailer> {

    public TrailersAdapter(Context context, List<Trailer> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_trailer, parent, false);
        }

        final Uri builtUri = Uri.parse(YOUTUBE_WATCH_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_PARAMETER, trailer.getKey())
                .build();

        ImageButton playIcon = (ImageButton) convertView.findViewById(R.id.play_icon);
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, builtUri);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
