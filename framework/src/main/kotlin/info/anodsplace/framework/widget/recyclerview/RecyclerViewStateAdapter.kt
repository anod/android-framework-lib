package info.anodsplace.framework.widget.recyclerview

import android.support.v7.widget.RecyclerView

open class RecyclerViewStateAdapter<VH : RecyclerView.ViewHolder>(adapters: Array<RecyclerView.Adapter<VH>>): MergeRecyclerAdapter<VH>() {

    constructor() : this(emptyArray())

    init {
        adapters.forEach { addAdapter(it) }
    }

    var selectedId = 0

    override fun getItemCount(): Int {
        return getAdapter(selectedId).itemCount
    }

    override fun getOffsetForAdapterIndex(adapterIndex: Int): Int {
        return 0
    }

    override fun getAdapterOffsetForItem(position: Int): RecyclerView.Adapter<VH> {
        return getAdapter(selectedId)
    }

}