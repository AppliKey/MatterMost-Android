package com.applikey.mattermost.adapters;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class UltimateAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER_TYPE_ID = -1;
    public static final int FOOTER_TYPE_ID = -2;
    protected LayoutInflater mLayoutInflater;
    private boolean mFooterVisibility;
    private boolean mHeaderVisibility;

    public interface HeaderInterface {

        HeaderVH getHeaderVH(View v);

        @LayoutRes
        int getHeaderViewResId();

        void bindHeaderVH(RecyclerView.ViewHolder vh);
    }

    public interface FooterInterface {

        FooterVH getFooterVH(View v);

        @LayoutRes
        int getFooterViewResId();

        void bindFooterVH(RecyclerView.ViewHolder vh);
    }

    public UltimateAdapter() {
        setHasStableIds(true);
        // check footer
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        if (viewType == HEADER_TYPE_ID) {
            HeaderInterface headerInterface = getThisHeader();
            return headerInterface.getHeaderVH(
                    getViewById(headerInterface.getHeaderViewResId(), parent));
        } else if (viewType == FOOTER_TYPE_ID) {
            FooterInterface footerInterface = getThisFooter();
            return footerInterface.getFooterVH(
                    getViewById(footerInterface.getFooterViewResId(), parent));
        } else {
            View v = getViewById(getDataViewResId(viewType), parent);
            return getDataViewHolder(v, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0 && withHeader()) {
            // bind header
            getThisHeader().bindHeaderVH(holder);
            ((HeaderVH) holder).hideHeader(!mHeaderVisibility);
        } else if (position == getFooterPosition() && withFooter()) {
            // bind footer
            getThisFooter().bindFooterVH(holder);
            ((FooterVH) holder).hideFooter(!mFooterVisibility);
        } else {
            // bind data
            if (withHeader()) {
                position -= 1;
            }
            bindDataVH((T) holder, position);
        }
    }

    @Override
    public final int getItemViewType(int absolutePosition) {
        if (absolutePosition == 0 && withHeader()) {
            return HEADER_TYPE_ID;
        } else if (withFooter() && absolutePosition == getFooterPosition()) {
            return FOOTER_TYPE_ID;
        } else {
            int dataType = getDataViewType(convertAbsolutePositionToData(absolutePosition));
            if (dataType == HEADER_TYPE_ID || dataType == FOOTER_TYPE_ID) {
                throw new IllegalArgumentException(
                        "Data type can't be " + dataType + ", this value is reserved");
            }
            return dataType;
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == 0 && withHeader()) {
            return -111L;
        } else if (withFooter() && position == getFooterPosition()) {
            return -222L;
        } else {
            return getDataId(withHeader() ? position - 1 : position);
        }
    }

    @Override
    public final int getItemCount() {
        int result = 0;
        if (withFooter()) {
            result++;
        }
        if (withHeader()) {
            result++;
        }
        result += getDataSize();
        return result;
    }

    public boolean withFooter() {
        return this instanceof FooterInterface;
    }

    public boolean withHeader() {
        return this instanceof HeaderInterface;
    }

    public abstract int getDataSize();

    @LayoutRes
    public abstract int getDataViewResId(int viewType);

    public abstract long getDataId(int dataPosition);

    public abstract int getDataViewType(int dataPosition);

    @NonNull
    public abstract T getDataViewHolder(@NonNull View v, int dataViewType);

    public abstract void bindDataVH(@NonNull T vh, int dataPosition);

    public int getDataPosition(int generalPosition) {
        if (withHeader()) {
            return generalPosition - 1;
        }
        return generalPosition;
    }

    public void setFooterVisibility(boolean visible) {
        mFooterVisibility = visible;
        notifyDataSetChanged();
    }

    public void setHeaderVisibility(boolean visible) {
        mHeaderVisibility = visible;
        notifyDataSetChanged();
    }

    public boolean hasData() {
        return getDataSize() > 0;
    }

    protected int convertAbsolutePositionToData(int absolutePosition) {
        int result = absolutePosition;
        if (withHeader()) {
            result--;
        }
        return result;
    }

    protected int getFooterPosition() {
        int result = 0;
        if (withHeader()) {
            result++;
        }
        result += getDataSize();
        return result;
    }

    private View getViewById(@LayoutRes int id, ViewGroup parent) {
        return mLayoutInflater.inflate(id, parent, false);
    }

    private FooterInterface getThisFooter() {
        return (FooterInterface) this;
    }

    private HeaderInterface getThisHeader() {
        return (HeaderInterface) this;
    }

    public abstract static class FooterVH extends RecyclerView.ViewHolder {

        public FooterVH(View itemView) {
            super(itemView);
        }

        public abstract void hideFooter(boolean hide);
    }

    public abstract static class HeaderVH extends RecyclerView.ViewHolder {

        public HeaderVH(View itemView) {
            super(itemView);
        }

        public abstract void hideHeader(boolean hide);
    }

}
