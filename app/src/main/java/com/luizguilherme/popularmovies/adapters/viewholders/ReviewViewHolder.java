package com.luizguilherme.popularmovies.adapters.viewholders;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.luizguilherme.popularmovies.R;
import com.luizguilherme.popularmovies.adapters.MovieDetailType;
import com.luizguilherme.popularmovies.models.Review;

public class ReviewViewHolder extends MovieDetailTypeViewHolder {
    private final TextView author;
    private final TextView content;

    public ReviewViewHolder(View itemView) {
        super(itemView);

        author = (TextView) itemView.findViewById(R.id.author);
        content = (TextView) itemView.findViewById(R.id.content);
    }

    @Override
    public void bind(MovieDetailType movieDetailType) {
        final Review review = (Review) movieDetailType;
        author.setText(review.getAuthor());
        content.setText(review.getContent());
    }

    @Override
    public void bindActions(MovieDetailType movieDetailType) {
        final Review review = (Review) movieDetailType;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
                context.startActivity(intent);
            }
        });
    }
}
