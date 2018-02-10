package info.anodsplace.framework.widget.recyclerview

import android.support.v4.util.ArrayMap
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import java.util.ArrayList

open class MergeRecyclerAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    private val adapters = ArrayList<RecyclerView.Adapter<VH>>()
    private var adapterOffset: Int = 0
    private val viewTypesMap = ArrayMap<Int, RecyclerView.Adapter<VH>>()

    /** Append the given adapter to the list of merged adapters.  */
    fun addAdapter(adapter: RecyclerView.Adapter<VH>): Int {
        val index = adapters.size
        addAdapter(index, adapter)
        return index
    }

    /** Append the given adapter to the list of merged adapters.  */
    fun addAdapter(index: Int, adapter: RecyclerView.Adapter<VH>) {
        adapters.add(index, adapter)
        adapter.registerAdapterDataObserver(ForwardingDataSetObserver(adapters.size - 1))
    }

    val size: Int
        get() = adapters.size

    fun getAdapter(index: Int): RecyclerView.Adapter<VH> {
        return adapters[index]
    }

    override fun getItemCount(): Int {
       return adapters.sumBy { it.itemCount }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        val adapter = getAdapterOffsetForItem(position)
        val viewType = adapter!!.getItemViewType(position - adapterOffset)
        viewTypesMap[viewType] = adapter
        return viewType
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VH {
        val adapter = viewTypesMap[viewType]!!
        return adapter.onCreateViewHolder(viewGroup, viewType)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        val adapter = getAdapterOffsetForItem(position)
        adapter!!.onBindViewHolder(viewHolder, position - adapterOffset)
    }

    private inner class ForwardingDataSetObserver constructor(private val adapterIndex: Int) : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)

            val offset = getOffsetForAdapterIndex(adapterIndex)
            notifyItemRangeChanged(offset + positionStart, itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            val offset = getOffsetForAdapterIndex(adapterIndex)
            notifyItemRangeInserted(offset + positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            val offset = getOffsetForAdapterIndex(adapterIndex)
            notifyItemRangeRemoved(offset + positionStart, itemCount)
        }
    }

    protected open fun getOffsetForAdapterIndex(adapterIndex: Int): Int {
        if (adapterIndex == 0) {
            return 0
        }
        var i = 0
        var offset = 0

        while (i < adapterIndex) {
            val adapter = adapters[i]
            offset += adapter.itemCount
            i++
        }
        return offset

    }

    /**
     * For a given merged position, find the corresponding Adapter and local position within that Adapter by iterating through Adapters and
     * summing their counts until the merged position is found.
     *
     * @param position a merged (global) position
     * @return the matching Adapter and local position, or null if not found
     */
    protected open fun getAdapterOffsetForItem(position: Int): RecyclerView.Adapter<VH>? {
        val adapterCount = adapters.size
        var i = 0
        var count = 0

        adapterOffset = 0
        while (i < adapterCount) {
            val adapter = adapters[i]
            val newCount = count + adapter.itemCount
            if (position < newCount) {
                return adapter
            }
            count = newCount
            adapterOffset = count
            i++
        }
        return null
    }

}