package io.realm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Copied from {@link io.realm.RealmRecyclerViewAdapter} to extend the access to {@link RealmChangeListener};
 * <p>
 * The RealmBaseRecyclerAdapter class is an abstract utility class for binding RecyclerView UI elements to Realm data.
 * <p>
 * This adapter will automatically handle any updates to its data and call notifyDataSetChanged() as appropriate.
 * Currently there is no support for RecyclerView's data callback methods like notifyItemInserted(int),
 * notifyItemRemoved(int),
 * notifyItemChanged(int) etc.
 * It means that, there is no possibility to use default data animations.
 * <p>
 * The RealmAdapter will stop receiving updates if the Realm instance providing the {@link OrderedRealmCollection} is
 * closed.
 *
 * @param <T>  type of {@link RealmModel} stored in the adapter.
 * @param <VH> type of RecyclerView.ViewHolder used in the adapter.
 */
public abstract class LowRateRealmRecyclerViewAdapter<T extends RealmModel, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    @Nullable
    private OrderedRealmCollection<T> adapterData;
    private LowRateRealmChangeListener listener;
    private final boolean hasAutoUpdates;

    public LowRateRealmRecyclerViewAdapter(@NonNull Context context,
                                           @Nullable OrderedRealmCollection<T> data,
                                           boolean autoUpdate) {
        //noinspection ConstantConditions
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }

        this.adapterData = data;
        this.hasAutoUpdates = autoUpdate;
    }

    /**
     * Returns the current ID for an item. Note that item IDs are not stable so you cannot rely on the item ID being the
     * same after notifyDataSetChanged() or {@link #updateData(OrderedRealmCollection)} has been called.
     *
     * @param index position of item in the adapter.
     * @return current item ID.
     */
    @Override
    public long getItemId(final int index) {
        return index;
    }

    @Override
    public int getItemCount() {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.size() : 0;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // Right now don't use generics, since we need maintain two different
        // types of listeners until RealmList is properly supported.
        // See https://github.com/realm/realm-java/issues/989
        this.listener = hasAutoUpdates ? new LowRateRealmChangeListener() {
            @Override
            public void onChangeCallback() {
                notifyDataSetChanged();
            }
        } : null;

        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            addListener(adapterData);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            removeListener(adapterData);
        }

        listener.unSubscribe();
        listener = null;
    }

    /**
     * Returns the item associated with the specified position.
     * Can return {@code null} if provided Realm instance by {@link OrderedRealmCollection} is closed.
     *
     * @param index index of the item.
     * @return the item at the specified position, {@code null} if adapter data is not valid.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public T getItem(int index) {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.get(index) : null;
    }

    /**
     * Returns data associated with this adapter.
     *
     * @return adapter data.
     */
    @Nullable
    public OrderedRealmCollection<T> getData() {
        return adapterData;
    }

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new {@link OrderedRealmCollection} to display.
     */
    @SuppressWarnings("WeakerAccess")
    public void updateData(@Nullable OrderedRealmCollection<T> data) {
        if (hasAutoUpdates) {
            if (adapterData != null) {
                removeListener(adapterData);
            }
            if (data != null) {
                addListener(data);
            }
        }

        this.adapterData = data;
        notifyDataSetChanged();
    }

    private void addListener(@NonNull OrderedRealmCollection<T> data) {
        if (data instanceof RealmResults) {
            final RealmResults realmResults = (RealmResults) data;
            //noinspection unchecked
            realmResults.addChangeListener(listener);
        } else if (data instanceof RealmList) {
            final RealmList realmList = (RealmList) data;
            //noinspection unchecked
            realmList.realm.handlerController.addChangeListenerAsWeakReference(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private void removeListener(@NonNull OrderedRealmCollection<T> data) {
        if (data instanceof RealmResults) {
            final RealmResults realmResults = (RealmResults) data;
            realmResults.removeChangeListener(listener);
        } else if (data instanceof RealmList) {
            final RealmList realmList = (RealmList) data;
            //noinspection unchecked
            realmList.realm.handlerController.removeWeakChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private boolean isDataValid() {
        return adapterData != null && adapterData.isValid();
    }
}
