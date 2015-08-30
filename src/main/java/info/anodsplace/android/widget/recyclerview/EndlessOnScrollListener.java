package info.anodsplace.android.widget.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {
    private final int mVisibleThreshold;

    private LinearLayoutManager mLinearLayoutManager;
    private int lastCall;

    public EndlessOnScrollListener(RecyclerView.LayoutManager linearLayoutManager, int threshold) {
        mLinearLayoutManager = (LinearLayoutManager) linearLayoutManager;
        mVisibleThreshold = threshold;
    }
 
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount = recyclerView.getAdapter().getItemCount();
        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (lastCall != totalItemCount && firstVisibleItem + mVisibleThreshold > lastCall) {
            lastCall = totalItemCount;
            onLoadMore();
        }
    }

    public void reset() {
        lastCall = 0;
    }
    /**
     *
     * @return true if there is more data
     */
    public abstract void onLoadMore();
}