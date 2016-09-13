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
        super(context, android.R.layout.simple_spinner_item);

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
        return getView(position, convertView, parent);
    }
}
