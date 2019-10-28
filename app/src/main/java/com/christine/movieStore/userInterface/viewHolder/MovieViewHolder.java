package com.christine.movieStore.userInterface.viewHolder;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.christine.movieStore.R;
import com.christine.movieStore.model.Movie;
import com.christine.movieStore.userInterface.utils.MovieClickListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.christine.movieStore.userInterface.activity.movieActivities.movieMainActivity.movieImagePathBuilder;

public class MovieViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_movie_poster)
    ImageView mMoviePoster;
    @BindView(R.id.cv_movie_card)
    CardView mMovieCard;

    public MovieViewHolder(final View itemView){
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void bind(final Movie movie, final MovieClickListener movieClickListener){

        mMovieCard.setLayoutParams(new ViewGroup.LayoutParams(getScreenWidth()/2, getMeasuredPosterHeight(getScreenWidth()/2)));

        Picasso.get()
                .load(movieImagePathBuilder(movie.getPosterPath()))
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(mMoviePoster);

        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){movieClickListener.onMovieClick(movie);}
        });

    }
    private int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getMeasuredPosterHeight(int width){return (int) (width * 1.5f);}
}