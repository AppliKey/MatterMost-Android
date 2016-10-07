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
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseServerActivity extends BaseMvpActivity implements ChooseServerView {

    @Bind(R.id.et_server)
    EditText mEtServerUrl;
    @Bind(R.id.b_proceed)
    Button mBtnProceed;
    @Bind(R.id.sp_http)
    Spinner mSpHttp;

    @InjectPresenter
    ChooseServerPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_server);

        ButterKnife.bind(this);

        mEtServerUrl.addTextChangedListener(mTextWatcher);
    }

    @OnClick(R.id.b_proceed)
    void onProceed() {
        final String httpPrefix = mSpHttp.getSelectedItem().toString();
        final String serverUrl = mEtServerUrl.getText().toString();

        showLoadingDialog();
        mPresenter.chooseServer(httpPrefix, serverUrl);
    }

    @Override
    public void showValidationError() {
        hideLoadingDialog();
        final String message = getResources().getString(R.string.invalid_server_url);
        mEtServerUrl.setError(message);
    }

    @Override
    public void onValidServerChosen() {
        hideLoadingDialog();
        startActivity(LogInActivity.getIntent(this));
    }

    private void disableButton() {
        mBtnProceed.setClickable(false);
        mBtnProceed.setBackgroundResource(R.drawable.round_button_gradient_disabled);
    }

    private void enableButton() {
        mBtnProceed.setClickable(true);
        mBtnProceed.setBackgroundResource(R.drawable.round_button_gradient);
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
