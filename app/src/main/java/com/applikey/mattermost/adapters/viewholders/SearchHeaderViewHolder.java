package com.applikey.mattermost.adapters.viewholders;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Anatoliy Chub
 */

public class SearchHeaderViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_header)
    TextView mTvHeader;

    public SearchHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setHeader(@StringRes int res) {
        mTvHeader.setText(res);
    }
}
