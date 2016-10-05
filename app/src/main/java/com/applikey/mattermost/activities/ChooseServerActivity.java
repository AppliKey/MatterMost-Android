package com.applikey.mattermost.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.ChooseServerPresenter;
import com.applikey.mattermost.mvp.views.ChooseServerView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseServerActivity extends BaseMvpActivity implements ChooseServerView {

    @Bind(R.id.et_server)
    EditText etServerUrl;
    @Bind(R.id.b_proceed)
    Button bProceed;
    @Bind(R.id.sp_http)
    Spinner spHttp;

    @InjectPresenter
    ChooseServerPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_server);

        ButterKnife.bind(this);

        etServerUrl.addTextChangedListener(mTextWatcher);
    }

    @OnClick(R.id.b_proceed)
    void onProceed() {
        final String httpPrefix = spHttp.getSelectedItem().toString();
        final String serverUrl = etServerUrl.getText().toString();

        presenter.chooseServer(httpPrefix, serverUrl);
    }

    @Override
    public void showValidationError() {
        final String message = getResources().getString(R.string.invalid_server_url);
        etServerUrl.setError(message);
    }

    @Override
    public void onValidServerChosen() {
        startActivity(LogInActivity.getIntent(this));
    }

    private void disableButton() {
        bProceed.setClickable(false);
        bProceed.setBackgroundResource(R.drawable.round_button_gradient_disabled);
    }

    private void enableButton() {
        bProceed.setClickable(true);
        bProceed.setBackgroundResource(R.drawable.round_button_gradient);
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String value = s.toString();

            if (value.trim().isEmpty()) {
                disableButton();
            } else {
                enableButton();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
