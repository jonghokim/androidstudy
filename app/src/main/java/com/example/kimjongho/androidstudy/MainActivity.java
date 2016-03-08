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
import com.google.common.collect.Lists;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String API_URL = "https://api.github.com";

    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    @Bind(R.id.api_response) TextView responseTextView;
    @Bind(R.id.api_call_button) View apiButton;

    private ContributorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final GitHubService gitHubService = retrofit.create(GitHubService.class);

        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<Contributor>> call = gitHubService.contributors("square", "retrofit");
                call.enqueue(new Callback<List<Contributor>>() {
                    @Override
                    public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                        List<Contributor> contributors = response.body();
                        responseTextView.setText(response.message());
                        setContent(contributors);
                    }

                    @Override
                    public void onFailure(Call<List<Contributor>> call, Throwable t) {

                    }
                });
            }
        });
    }

    /**
     * API 로 받아온 데이터를 {@link RecyclerView} 에 그린다
     * @param contributors API response
     */
    private void setContent(List<Contributor> contributors) {
        adapter = new ContributorAdapter(contributors);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * RecyclerView ViewHolder
     */
    public static class ContributorItemHolder extends RecyclerView.ViewHolder {
        public TextView loginTextView;
        public TextView contributionsTextView;
        public ContributorItemHolder(View itemView) {
            super(itemView);

            loginTextView = (TextView) itemView.findViewById(R.id.item_login);
            contributionsTextView = (TextView) itemView.findViewById(R.id.item_contributions);
        }
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
            holder.contributionsTextView.setText(String.valueOf(contributor.contributions));
            holder.loginTextView.setText(contributor.login);
        }

        @Override
        public int getItemCount() {
            return contributors.size();
        }
    }
}
