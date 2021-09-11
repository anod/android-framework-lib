package info.anodsplace.framework.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import info.anodsplace.framework.R
import java.io.Serializable

abstract class FragmentContainerFactory(
        val fragmentTag: String,
        val themeResId: Int = 0
) : Serializable {
    abstract fun create(): Fragment
}

class FragmentContainerActivity: FragmentActivity() {

    companion object {
        private const val extraFactory = "extra_factory"
        private const val extraArguments = "extra_arguments"

        fun intent(context: Context, factory: FragmentContainerFactory, arguments: Bundle = Bundle.EMPTY, clazz: Class<*> = FragmentContainerActivity::class.java): Intent {
            return Intent(context, clazz).apply {
                putExtra(extraFactory, factory)
                putExtra(extraArguments, arguments)
            }
        }

        fun attach(@IdRes containerViewId: Int, activity: FragmentActivity) {
            val factory = activity.intent.getSerializableExtra(extraFactory) as FragmentContainerFactory
            if (factory.themeResId != 0) {
                activity.setTheme(factory.themeResId)
            }
            val f = factory.create()
            if (activity.intent.hasExtra(extraArguments)) {
                val extra = activity.intent.getBundleExtra(extraArguments)!!
                if (f.arguments == null) {
                    f.arguments = extra
                } else {
                    if (!extra.isEmpty) {
                        f.requireArguments().putAll(extra)
                    }
                }
            }

            activity.supportFragmentManager.commit {
                replace(containerViewId, f, factory.fragmentTag)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        if (savedInstanceState == null) {
            attach(R.id.activity_content, this)
        }
    }
}