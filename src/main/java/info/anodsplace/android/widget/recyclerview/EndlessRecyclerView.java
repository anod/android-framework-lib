package info.anodsplace.android.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import info.anodsplace.android.R;

/**
 * @author alex
 * @date 2015-06-22
 */
public class EndlessRecyclerView extends RecyclerView {

    private EndlessOnScrollListener mScrollListener;
    private boolean mHasMoreData;
    private OnLoadMoreListener mListener;

    public EndlessRecyclerView(Context context) {
        this(context, null);
    }

    public EndlessRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndlessRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void init(LayoutManager layout, Adapter adapter, int scrollLimit) {
        super.setLayoutManager(layout);

        EndlessAdapter endlessAdapter = new EndlessAdapter(adapter, getContext(), R.layout.list_item_loadmore);
        super.setAdapter(endlessAdapter);

        mScrollListener = new EndlessOnScrollListener(layout, scrollLimit) {

            @Override
            public void onLoadMore() {
                if (mListener != null) {
                    mListener.onLoadMore();
                }
            }
        };
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mListener = listener;
    }

    public void setHasMoreData(boolean hasMoreData) {
        mScrollListener.reset();
        if (mHasMoreData == hasMoreData) {
            return;
        }
        mHasMoreData = hasMoreData;
        if (hasMoreData) {
            addOnScrollListener(mScrollListener);
            ((EndlessAdapter) getAdapter()).setKeepOnAppending(true);
        } else {
            removeOnScrollListener(mScrollListener);
            ((EndlessAdapter) getAdapter()).setKeepOnAppending(false);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
