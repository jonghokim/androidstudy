package com.example.kimjongho.androidstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kimjongho.androidstudy.api.GitHubService;
import com.example.kimjongho.androidstudy.api.model.Contributor;
import com.example.kimjongho.androidstudy.jackson.JacksonConverterFactory;
import com.example.kimjongho.androidstudy.recyclerview.DividerItemDecoration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    public static final String API_URL = "https://api.github.com";

    // retrofit
    @Bind(R.id.recycler_view) RecyclerView retrofitRecyclerView;
    @Bind(R.id.api_response) TextView retrofitResponseTextView;
    @Bind(R.id.api_call_button) View retrofitApiButton;

    // OKHTTP
    @Bind(R.id.recycler_view_okhttp) RecyclerView okhttpRecyclerView;
    @Bind(R.id.api_response_okhttp) TextView okhttpResponseTextView;
    @Bind(R.id.api_call_button_okhttp) View okhttpApiButton;

    private ContributorAdapter adapter;
    private Gson gson;
    private ObjectMapper objectMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeMapper();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
        final GitHubService gitHubService = retrofit.create(GitHubService.class);

        retrofitApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<Contributor>> call = gitHubService.contributors("square", "retrofit");
                call.enqueue(new Callback<List<Contributor>>() {
                    @Override
                    public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                        List<Contributor> contributors = response.body();
                        retrofitResponseTextView.setText(response.message());
                        setRetrofitContent(contributors);
                    }

                    @Override
                    public void onFailure(Call<List<Contributor>> call, Throwable t) {
                    }
                });
            }
        });

        gson = new Gson();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        okhttp3.Response response = chain.proceed(request);
                        return response;
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        okhttp3.Response response = chain.proceed(request);
                        return response;
                    }
                })
                .build();

        okhttpApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request.Builder()
                        .url("https://api.github.com/repos/square/retrofit/contributors")
                        .build();

                okHttpClient.newCall(request)
                        .enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                String result = response.body().string();
                                final List<Contributor> contributors = gson.fromJson(result, new TypeToken<List<Contributor>>(){}.getType());
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setOkhttpContent(contributors);
                                    }
                                });
                            }
                        });
            }
        });

    }

    /**
     * API 로 받아온 데이터를 {@link RecyclerView} 에 그린다
     * @param contributors API response
     */
    private void setRetrofitContent(List<Contributor> contributors) {
        adapter = new ContributorAdapter(contributors);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        retrofitRecyclerView.setLayoutManager(layoutManager);
        retrofitRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        retrofitRecyclerView.setAdapter(adapter);
    }

    private void setOkhttpContent(List<Contributor> contributors) {
        adapter = new ContributorAdapter(contributors);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        okhttpRecyclerView.setLayoutManager(layoutManager);
        okhttpRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        okhttpRecyclerView.setAdapter(adapter);
    }

    /**
     * RecyclerView ViewHolder
     */
    public static class ContributorItemHolder extends RecyclerView.ViewHolder {
        public TextView loginTextView;
        public TextView contributionsTextView;
        public TextView htmlUrlTextView;

        public ContributorItemHolder(View itemView) {
            super(itemView);

            loginTextView = (TextView) itemView.findViewById(R.id.item_login);
            contributionsTextView = (TextView) itemView.findViewById(R.id.item_contributions);
            htmlUrlTextView = (TextView) itemView.findViewById(R.id.item_html_url);
        }
    }

    /**
     * initialize Jackson ObjectMapper
     */
    private void initializeMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * RecyclerView Adapter
     */
    private class ContributorAdapter extends RecyclerView.Adapter<ContributorItemHolder> {

        private List<Contributor> contributors = Lists.newArrayList();

        public ContributorAdapter(List<Contributor> contributors) {
            this.contributors = contributors;
        }

        @Override
        public ContributorItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ContributorItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contributor, parent, false));
        }

        @Override
        public void onBindViewHolder(ContributorItemHolder holder, int position) {
            Contributor contributor = contributors.get(position);
            holder.contributionsTextView.setText(String.valueOf(contributor.getContributions()));
            holder.loginTextView.setText(contributor.getLogin());
            holder.htmlUrlTextView.setText(contributor.getHtmlUrl());
        }

        @Override
        public int getItemCount() {
            return contributors.size();
        }
    }
}
