package info.anodsplace.android.widget.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import info.anodsplace.android.R;

/**
 * @author alex
 * @date 2015-06-22
 */
public class EndlessRecyclerView extends RecyclerView {

    private EndlessOnScrollListener scrollListener;
    private boolean hasMoreData;
    private OnLoadMoreListener listener;

    public EndlessRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public EndlessRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndlessRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(@NonNull LayoutManager layout,@NonNull Adapter adapter, int scrollLimit) {
        super.setLayoutManager(layout);

        EndlessAdapter endlessAdapter = new EndlessAdapter(adapter, getContext(), R.layout.list_item_loadmore);
        super.setAdapter(endlessAdapter);

        scrollListener = new EndlessOnScrollListener(layout, scrollLimit) {
            @Override
            public void onLoadMore() {
                if (listener != null) {
                    listener.onLoadMore();
                }
            }
        };
    }

    public void setOnLoadMoreListener(@Nullable OnLoadMoreListener listener) {
        this.listener = listener;
    }

    public void setHasMoreData(boolean hasMoreData) {
        scrollListener.reset();
        if (this.hasMoreData == hasMoreData) {
            return;
        }
        this.hasMoreData = hasMoreData;
        if (hasMoreData) {
            addOnScrollListener(scrollListener);
            ((EndlessAdapter) getAdapter()).setKeepOnAppending(true);
        } else {
            removeOnScrollListener(scrollListener);
            ((EndlessAdapter) getAdapter()).setKeepOnAppending(false);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
