package com.christine.dreamhouseapp.userInterface.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.christine.dreamhouseapp.R;
import com.christine.dreamhouseapp.model.Movie;
import com.christine.dreamhouseapp.model.MoviePageResult;
import com.christine.dreamhouseapp.network.GetMovieDataService;
import com.christine.dreamhouseapp.network.RetrofitInstance;
import com.christine.dreamhouseapp.userInterface.adapter.MovieAdapter;
import com.christine.dreamhouseapp.userInterface.utils.EndlessRecyclerViewScrollListener;
import com.christine.dreamhouseapp.userInterface.utils.MovieClickListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.christine.dreamhouseapp.Constants.MOVIEDB_API_KEY;

public class movieMainActivity extends AppCompatActivity {

    public static final String API_KEY = MOVIEDB_API_KEY;
    private static int totalPages;
    private static int currentSortMode = 1;
    private Call<MoviePageResult> call;
    private RecyclerView recyclerView;
    private List<Movie> movieResults;
    private MovieAdapter movieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_movies);
        GridLayoutManager manager = new GridLayoutManager(this,2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

            @Override
            public int getSpanSize(int position){
                return 1;
            }
        });

        recyclerView.setLayoutManager(manager);

        loadPage(1);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if((page + 1)<= totalPages){
                    loadPage(page + 1);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.sort_by_popularity:
                currentSortMode = 1;
                break;
            case R.id.sort_by_top:
                currentSortMode = 2;
                break;
        }
        loadPage(1);
        return super.onOptionsItemSelected(item);
    }

    private void loadPage (final int page){
        GetMovieDataService movieDataService = RetrofitInstance.getRetrofitInstance().create(GetMovieDataService.class);

        switch (currentSortMode){
            case 1:
                call = movieDataService.getPopularMovies(page,API_KEY);
                break;
            case 2:
                call = movieDataService.getTopRatedMovies(page,API_KEY);
        }

        call.enqueue(new Callback<MoviePageResult>() {
            @Override
            public void onResponse(Call<MoviePageResult> call, Response<MoviePageResult> response) {

                if(page == 1) {
                    movieResults = response.body().getMovieResults();
                    totalPages = response.body().getTotalPages();

                    movieAdapter = new MovieAdapter(movieResults, new MovieClickListener() {
                        @Override
                        public void onMovieClick(Movie movie) {
                            Intent intent = new Intent(movieMainActivity.this, MovieActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("movie", movie);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(movieAdapter);
                } else {
                    List<Movie> movies = response.body().getMovieResults();
                    for(Movie movie : movies){
                        movieResults.add(movie);
                        movieAdapter.notifyItemInserted(movieResults.size() - 1);
                    }
                }

            }

            @Override
            public void onFailure(Call<MoviePageResult> call, Throwable t) {
                Toast.makeText(movieMainActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static String movieImagePathBuilder(String imagePath) {
        return "https://image.tmdb.org/t/p/" +
                "w500" +
                imagePath;
    }


}