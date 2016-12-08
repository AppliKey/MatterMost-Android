package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter<T> extends ArrayAdapter<T> implements Filterable {

    private List<T> mSuggestions = new ArrayList<>();
    private final T[] mOriginalValues;
    private ContainsFilter mFilter;

    public AutoCompleteAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
        this.mOriginalValues = objects;
    }

    @Override
    public void clear() {
        mSuggestions.clear();
    }

    @Override
    @NonNull
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ContainsFilter();
        }
        return mFilter;
    }

    private class ContainsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null) {
                mSuggestions.clear();
                for (T object : mOriginalValues) {
                    if (object.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        mSuggestions.add(object);
                    }
                }

                filterResults.values = mSuggestions;
                filterResults.count = mSuggestions.size();
            }
            return filterResults;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results == null) {
                return;
            }
            List<T> filteredList = (List<T>) results.values;
            if (results.count > 0) {
                clear();
                Stream.of(filteredList).forEach(AutoCompleteAdapter.this::add);
                notifyDataSetChanged();
            }
        }
    }
}
