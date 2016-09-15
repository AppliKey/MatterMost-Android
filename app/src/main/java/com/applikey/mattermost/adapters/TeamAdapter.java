package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.team.Team;

@SuppressWarnings("NullableProblems")
public class TeamAdapter extends ArrayAdapter<Team> {

    private String mUnknownTeamResource;

    public TeamAdapter(Context context) {
        super(context, R.layout.spinner_item_team);

        setDropDownViewResource(R.layout.spinner_item_team_dropdown);

        mUnknownTeamResource = context.getResources().getString(R.string.unknown);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView view = (TextView) super.getView(position, convertView, parent);

        final Team item = getItem(position);
        view.setText(item != null ? item.getDisplayName() : mUnknownTeamResource);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final TextView view = (TextView) super.getDropDownView(position, convertView, parent);

        final Team item = getItem(position);
        view.setText(item != null ? item.getDisplayName() : mUnknownTeamResource);

        return view;
    }
}
