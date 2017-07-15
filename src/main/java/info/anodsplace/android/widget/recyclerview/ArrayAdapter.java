package info.anodsplace.android.widget.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class ArrayAdapter<T extends Object, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
 
    private List<T> objects;
 
    public ArrayAdapter(@NonNull final List<T> objects) {
        this.objects = objects;
    }
 
    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(@NonNull final T object) {
        objects.add(object);
        notifyItemInserted(objects.size() - 1);
    }

    /**
     *
     * @param objects List of objects to be added
     */
    public void addAll(@NonNull List<T> objects) {
        int count = this.objects.size();
        this.objects.addAll(objects);
        notifyItemRangeInserted(count, objects.size());
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        objects.clear();
        notifyDataSetChanged();
    }
 
    @Override
    public int getItemCount() {
        return objects.size();
    }
 
    public T getItem(final int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }
 
    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(final T item) {
        return objects.indexOf(item);
    }
 
    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(@NonNull final T object, int index) {
        objects.add(index, object);
        notifyItemInserted(index);
 
    }
 
    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(@NonNull T object) {
        final int position = getPosition(object);
        objects.remove(object);
        notifyItemRemoved(position);
    }
 
    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained in this adapter.
     */
    public void sort(@NonNull Comparator<? super T> comparator) {
        Collections.sort(objects, comparator);
        notifyItemRangeChanged(0, objects.size());
    }


}