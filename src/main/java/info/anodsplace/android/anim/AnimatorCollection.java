package info.anodsplace.android.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex
 * @date 2015-05-25
 */
public class AnimatorCollection {
    private List<Animator> mAnimators;
    private AnimatorSet mSet;

    public AnimatorCollection() {
        mAnimators = new ArrayList<>();
    }

    public void add(Animator anim) {
        if (anim != null) {
            mAnimators.add(anim);
        }
    }

    public void clear() {
        mSet = null;
        mAnimators.clear();
    }

    public AnimatorSet sequential() {
        AnimatorSet set = set();
        set.playSequentially(mAnimators);
        return set;
    }

    public AnimatorSet together() {
        AnimatorSet set = set();
        set.playTogether(mAnimators);
        return set;
    }

    public boolean isEmpty() {
        return mAnimators.isEmpty();
    }

    public void addListener(AnimatorListenerAdapter listener) {
        AnimatorSet set = set();
        set.addListener(listener);
    }

    private AnimatorSet set() {
        if (mSet == null) {
            mSet = new AnimatorSet();
        }
        return mSet;
    }
}
