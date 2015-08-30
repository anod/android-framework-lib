package info.anodsplace.android.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author alex
 * @date 2015-07-05
 */
public class TopOffsetItemDecorator extends RecyclerView.ItemDecoration {
    private int mTopOffsetPixel;

    public TopOffsetItemDecorator(int topOffsetPixel) {
        mTopOffsetPixel = topOffsetPixel;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        if (pos == 0) {
            outRect.set(0, mTopOffsetPixel, 0, 0);
        }
    }
}