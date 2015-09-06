package info.anodsplace.android.widget.recyclerview;

import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MergeRecyclerAdapter extends RecyclerView.Adapter {

	private ArrayList<RecyclerView.Adapter> mAdapters = new ArrayList<>();
	private int mAdapterOffset;
	private ArrayMap<Integer, RecyclerView.Adapter> mViewTypesMap = new ArrayMap<>();

	public MergeRecyclerAdapter() {
	}

	/** Append the given adapter to the list of merged adapters. */
	public void addAdapter(RecyclerView.Adapter adapter) {
		addAdapter(mAdapters.size(), adapter);
	}

	/** Append the given adapter to the list of merged adapters. */
	public void addAdapter(int index, RecyclerView.Adapter adapter) {
		mAdapters.add(index, adapter);
		adapter.registerAdapterDataObserver(new ForwardingDataSetObserver(mAdapters.size()-1));
	}

	public RecyclerView.Adapter getAdapter(int index) {
		return mAdapters.get(index);
	}

	@Override
	public int getItemCount() {
		int count = 0;
		for (RecyclerView.Adapter adapter : mAdapters) {
			count += adapter.getItemCount();
		}
		return count;
	}


	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public int getItemViewType(int position) {
		RecyclerView.Adapter adapter = getAdapterOffsetForItem(position);
		int viewType = adapter.getItemViewType(mAdapterOffset + position);
		mViewTypesMap.put(viewType, adapter);
		return viewType;
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		RecyclerView.Adapter adapter  = mViewTypesMap.get(viewType);
		return adapter.onCreateViewHolder(viewGroup,viewType);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		RecyclerView.Adapter adapter = getAdapterOffsetForItem(position);
		adapter.onBindViewHolder(viewHolder, mAdapterOffset+position);
	}

	private class ForwardingDataSetObserver extends RecyclerView.AdapterDataObserver {
		private int mAdapterIndex;

		private ForwardingDataSetObserver(int adapterIndex) {
			mAdapterIndex = adapterIndex;
		}

		@Override
		public void onChanged() {
			notifyDataSetChanged();
		}

		@Override
		public void onItemRangeChanged(int positionStart, int itemCount) {
			super.onItemRangeChanged(positionStart, itemCount);

			int offset = getOffsetForAdapterIndex(mAdapterIndex);
			notifyItemRangeChanged(offset + positionStart, itemCount);
		}

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			super.onItemRangeInserted(positionStart, itemCount);
			int offset = getOffsetForAdapterIndex(mAdapterIndex);
			notifyItemRangeInserted(offset + positionStart, itemCount);
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			super.onItemRangeRemoved(positionStart, itemCount);
			int offset = getOffsetForAdapterIndex(mAdapterIndex);
			notifyItemRangeRemoved(offset + positionStart, itemCount);
		}
	}

	protected int getOffsetForAdapterIndex(int adapterIndex) {
		if (adapterIndex == 0) {
			return 0;
		}
		int i = 0;
		int offset = 0;

		while (i < adapterIndex) {
			RecyclerView.Adapter adapter = mAdapters.get(i);
			offset += adapter.getItemCount();
			i++;
		}
		return offset;

	}

	/**
	 * For a given merged position, find the corresponding Adapter and local position within that Adapter by iterating through Adapters and
	 * summing their counts until the merged position is found.
	 *
	 * @param position a merged (global) position
	 * @return the matching Adapter and local position, or null if not found
	 */
	protected RecyclerView.Adapter getAdapterOffsetForItem(final int position) {
		final int adapterCount = mAdapters.size();
		int i = 0;
		int count = 0;

		mAdapterOffset = 0;
		while (i < adapterCount) {
			RecyclerView.Adapter adapter = mAdapters.get(i);
			int newCount = count + adapter.getItemCount();
			if (position < newCount) {
				return adapter;
			}
			count = newCount;
			mAdapterOffset = count;
			i++;
		}
		return null;
	}

}