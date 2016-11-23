package com.applikey.mattermost.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DialogJoinChannel extends DialogFragment {

    @Bind(R.id.tv_join_offer)
    TextView mTvJoinOffer;

    @Bind(R.id.btn_join_channel)
    Button mBtnJoinChannel;

    private static final String KEY_CHANNEL_NAME = "key-channel-name";
    private String mChannelName;

    public static DialogJoinChannel newInstance(String channelName) {
        final Bundle args = new Bundle();
        final DialogJoinChannel fragment = new DialogJoinChannel();
        args.putString(KEY_CHANNEL_NAME, channelName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_join_chat, container);
        ButterKnife.bind(this, view);
        mTvJoinOffer.setText(getResources().getString(R.string.join_offer, mChannelName));
        mBtnJoinChannel.setOnClickListener(button -> sendJoinRequest());

        return view;
    }


    private void sendJoinRequest() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChannelName = getArguments().getString(KEY_CHANNEL_NAME);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getDialog().show();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
