package com.luizguilherme.popularmovies.adapters.viewholders;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.MovieDetailType;
import com.luizguilherme.popularmovies.models.Trailer;

import static com.luizguilherme.popularmovies.Constants.YOUTUBE_VIDEO_PARAMETER;
import static com.luizguilherme.popularmovies.Constants.YOUTUBE_WATCH_BASE_URL;

public class TrailerViewHolder extends MovieDetailTypeViewHolder {
    private final TextView trailerName;

    public TrailerViewHolder(View itemView) {
        super(itemView);

        trailerName = (TextView) itemView.findViewById(R.id.trailer_name);
    }

    @Override
    public void bind(MovieDetailType movieDetailType) {
        Trailer trailer = (Trailer) movieDetailType;
        trailerName.setText(trailer.getName());

        bindActions(trailer);
    }

    @Override
    public void bindActions(MovieDetailType movieDetailType) {
        Trailer trailer = (Trailer) movieDetailType;

        final Uri builtUri = Uri.parse(YOUTUBE_WATCH_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_PARAMETER, trailer.getKey())
                .build();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, builtUri);
                context.startActivity(intent);
            }
        });
    }
}
