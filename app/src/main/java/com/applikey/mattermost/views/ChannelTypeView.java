package com.applikey.mattermost.views;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ChannelTypeView extends LinearLayout {

    @Bind(R.id.switch_channel_type)
    SwitchCompat mSwitchChannelType;

    @Bind(R.id.tvTypePublic)
    TextView mTvTypePublic;

    @Bind(R.id.tvTypePrivate)
    TextView mTvTypePrivate;

    private CompoundButton.OnCheckedChangeListener mExternalCheckedChangeListener;

    private CompoundButton.OnCheckedChangeListener mOnCheckedListener = (view, checked) -> {
        if (checked) {
            mTvTypePublic.setEnabled(false);
            mTvTypePrivate.setEnabled(true);
            mTvTypePublic.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_public_channel_unselected, 0, 0, 0);
            mTvTypePrivate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_private_channel_selected, 0);
        } else {
            mTvTypePublic.setEnabled(true);
            mTvTypePrivate.setEnabled(false);
            mTvTypePublic.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_public_channel_selected, 0, 0, 0);
            mTvTypePrivate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_private_channel_unselected, 0);

        }
        if (mExternalCheckedChangeListener != null) {
            mExternalCheckedChangeListener.onCheckedChanged(view, checked);
        }
    };

    public ChannelTypeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflate(context, R.layout.channel_type_view, this);
        ButterKnife.bind(this);
        mSwitchChannelType.setOnCheckedChangeListener(mOnCheckedListener);
        mSwitchChannelType.setChecked(true);
    }

    public boolean isChecked() {
        return mSwitchChannelType.isChecked();
    }

    public void setChecked(boolean checked) {
        mSwitchChannelType.setChecked(checked);
    }

    public void setOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener checkedChangedListener) {
        mExternalCheckedChangeListener = checkedChangedListener;
    }

}
