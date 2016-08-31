/**
 * @author dawson dong
 */

package com.applikey.mattermost.utils.kissUtils.helper;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {

    private final SparseArray<View> views;
    private final View convertView;

    private ViewHolder(View view) {
        this.views = new SparseArray<>();
        this.convertView = view;
        view.setTag(this);
    }

    public static ViewHolder get(View view) {
        final Object tag = view.getTag();
        if (tag instanceof ViewHolder) {
            return (ViewHolder) tag;
        } else {
            return new ViewHolder(view);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(int id) {
        View view = views.get(id);
        if (view == null) {
            view = convertView.findViewById(id);
            views.put(id, view);
        }

        return (T) view;
    }
}
