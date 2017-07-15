package info.anodsplace.android.widget.recyclerview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.atomic.AtomicBoolean;

public class EndlessAdapter extends AdapterWrapper {
    public static final int VIEW_TYPE_LOAD_MORE = -1;
    private AtomicBoolean keepOnAppending=new AtomicBoolean(false);
    private int loadMoreViewId;
    private final Context context;

    public EndlessAdapter(@NonNull RecyclerView.Adapter adapter, @NonNull Context context, @LayoutRes int loadMoreViewId) {
        super(adapter);
        this.loadMoreViewId = loadMoreViewId;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        int count = adapter.getItemCount();
        if (keepOnAppending.get()) {
            if(count > getMinItemCount()) {
                return count + 1; // one more for loading view
            }
        }

        return count;
    }

    public int getItemViewType(int position) {
        if (position == adapter.getItemCount()) {
            return VIEW_TYPE_LOAD_MORE;
        }

        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        if (position == adapter.getItemCount()) {
            return position;
        }
        return super.getItemId(position);
    }

    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            View view = LayoutInflater.from(context).inflate(loadMoreViewId, parent, false);
            return new FooterViewHolder(view);
        }

        return super.onCreateViewHolder(parent, viewType);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            return;
        }
        super.onBindViewHolder(holder, position);
    }

    public void setKeepOnAppending(boolean newValue) {
        boolean same = newValue==keepOnAppending.get();

        keepOnAppending.set(newValue);

        if (!same) {
            int count = adapter.getItemCount();
            if (newValue) {
                if (count > getMinItemCount()) {
                    notifyItemInserted(count);
                }
            } else {
                notifyItemRemoved(count + 1);
            }
        }
    }

    private int getMinItemCount() {
        if (adapter instanceof HeaderAdapter) {
            return ((HeaderAdapter) adapter).getHeaderCount();
        }
        return 0;
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View loadMoreView) {
            super(loadMoreView);
        }
    }
}