package info.anodsplace.android.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex
 * @date 2015-05-25
 */
public class AnimatorCollection {
    private List<Animator> animators = new ArrayList<>();
    private AnimatorSet set;

    public void add(@Nullable Animator anim) {
        if (anim != null) {
            animators.add(anim);
        }
    }

    public void clear() {
        set = null;
        animators.clear();
    }

    public AnimatorSet sequential() {
        AnimatorSet set = set();
        set.playSequentially(animators);
        return set;
    }

    public AnimatorSet together() {
        AnimatorSet set = set();
        set.playTogether(animators);
        return set;
    }

    public boolean isEmpty() {
        return animators.isEmpty();
    }

    public void addListener(AnimatorListenerAdapter listener) {
        AnimatorSet set = set();
        set.addListener(listener);
    }

    private AnimatorSet set() {
        if (set == null) {
            set = new AnimatorSet();
        }
        return set;
    }
}
