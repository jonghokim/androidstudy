package com.example.kimjongho.androidstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JacksonActivity extends AppCompatActivity {
    @Bind(R.id.jackson_parse_button) View jacksonParseButton;
    @Bind(R.id.jackson_parse_result_view) TextView jacksonParseResultView;
    private ObjectMapper objectMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jackson);
        ButterKnife.bind(this);
        initializeMapper();
    }

    /**
     * initialize Jackson ObjectMapper
     */
    private void initializeMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
