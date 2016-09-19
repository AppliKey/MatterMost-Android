package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.team.Team;

import java.util.List;

@SuppressWarnings("NullableProblems")
public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder> {

    private List<Team> mDataSet = null;
    private TeamClickListener mTeamClickListener = null;

    public TeamListAdapter(List<Team> dataSet) {
        super();

        mDataSet = dataSet;
    }

    public void setItemClickAdapter(TeamClickListener listener) {
        mTeamClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_team, parent, false);

        final ViewHolder vh = new ViewHolder(v);

        vh.getRoot().setOnClickListener(mOnClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        final Team team = mDataSet.get(position);
        vh.getName().setText(team.getDisplayName());
        vh.getRoot().setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private final View.OnClickListener mOnClickListener = v -> {
        final int position = (Integer) v.getTag();

        final Team team = mDataSet.get(position);

        if (mTeamClickListener != null) {
            mTeamClickListener.onTeamClicked(team);
        }
    };

    /* package */
    class ViewHolder extends RecyclerView.ViewHolder {

        private final View mRoot;
        private final TextView mName;

        ViewHolder(View itemView) {
            super(itemView);

            mRoot = itemView;
            mName = (TextView) itemView.findViewById(R.id.name);
        }

        TextView getName() {
            return mName;
        }

        View getRoot() {
            return mRoot;
        }
    }

    public interface TeamClickListener {
        void onTeamClicked(Team team);
    }
}
