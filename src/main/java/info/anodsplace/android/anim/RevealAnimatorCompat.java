package info.anodsplace.android.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * @author alex
 * @date 2015-05-25
 */
public class RevealAnimatorCompat {
    private static final int ANIM_DURATION = 300;

    public static Animator show(@NonNull final View viewRoot, int x, int y, int delay) {
        Animator anim;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());
            anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
            anim.setDuration(ANIM_DURATION);
        } else {
            // Kitkat compatibility
            anim = ValueAnimator.ofInt(0, 1);
            anim.setDuration(10);
        }
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewRoot.setVisibility(View.VISIBLE);
            }
        });
        anim.setStartDelay(delay);
        return anim;
    }

    public static Animator hide(@NonNull final View viewRoot, int x, int y, int delay) {
        Animator anim;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int initialRadius = viewRoot.getWidth();
            anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, initialRadius, 0);
            anim.setDuration(ANIM_DURATION);
        } else {
            // Kitkat compatibility
            anim = ValueAnimator.ofInt(0, 1);
            anim.setDuration(10);
        }
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(ANIM_DURATION);
        anim.setStartDelay(delay);
        return anim;
    }
}
