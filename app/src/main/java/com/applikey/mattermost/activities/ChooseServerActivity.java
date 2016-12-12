package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.AutoCompleteAdapter;
import com.applikey.mattermost.mvp.presenters.ChooseServerPresenter;
import com.applikey.mattermost.mvp.views.ChooseServerView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class ChooseServerActivity extends BaseMvpActivity implements ChooseServerView {

    @Bind(R.id.et_server)
    AutoCompleteTextView mEtServerUrl;

    @Bind(R.id.b_proceed)
    Button mBtnProceed;

    @Bind(R.id.sp_http)
    Spinner mSpHttp;

    @InjectPresenter
    ChooseServerPresenter mPresenter;

    private final TextView.OnEditorActionListener mOnInputDoneActionListener =
            (v, actionId, event) -> {
                onProceed();
                return true;
            };

    public static Intent getIntent(Context context) {
        final Intent intent = new Intent(context, ChooseServerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_server);

        ButterKnife.bind(this);

        mEtServerUrl.addTextChangedListener(mTextWatcher);

        mEtServerUrl.setOnEditorActionListener(mOnInputDoneActionListener);
        disableButton();
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

    @Override
    public void showPresetServer(String url) {
        mEtServerUrl.setText(url);
    }

    @Override
    public void setAutoCompleteServers(String[] urls) {
        final AutoCompleteAdapter<String> adapter =
                new AutoCompleteAdapter<>(this, android.R.layout.simple_list_item_1, urls);
        mEtServerUrl.setAdapter(adapter);
    }

    @OnClick(R.id.b_proceed)
    void onProceed() {
        final String httpPrefix = mSpHttp.getSelectedItem().toString();
        final String serverUrl = mEtServerUrl.getText().toString();

        showLoadingDialog();
        mPresenter.chooseServer(httpPrefix, serverUrl);
    }

    @OnEditorAction(R.id.et_server)
    boolean onDoneClick(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onProceed();
        }
        return true;
    }

    private void disableButton() {
        mBtnProceed.setEnabled(false);
    }

    private void enableButton() {
        mBtnProceed.setEnabled(true);
    }

    private void handleButtonVisibility(String input) {
        if (input.trim().isEmpty()) {
            disableButton();
        } else {
            enableButton();
        }
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String value = s.toString();

            handleButtonVisibility(value);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
