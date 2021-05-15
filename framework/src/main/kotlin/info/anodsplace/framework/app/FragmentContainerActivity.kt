package info.anodsplace.framework.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import info.anodsplace.framework.AppLog
import info.anodsplace.framework.R
import java.io.Serializable

open class FragmentFactory(val fragmentTag: String) : Serializable {
    open fun create(): Fragment? {
        return null
    }
}

class FragmentContainerActivity: FragmentActivity() {

    companion object {
        private const val extraFactory = "extra_factory"
        private const val extraArguments = "extra_arguments"

        fun intent(context: Context, factory: FragmentFactory, arguments: Bundle = Bundle.EMPTY, clazz: Class<*> = FragmentContainerActivity::class.java): Intent {
            return Intent(context, clazz).apply {
                putExtra(extraFactory, factory)
                putExtra(extraArguments, arguments)
            }
        }

        fun attach(@IdRes containerViewId: Int, activity: FragmentActivity) {
            val factory: FragmentFactory = activity.intent.getSerializableExtra(extraFactory) as FragmentFactory
            val f = factory.create()
            if (f == null) {
                AppLog.e("Missing fragment for tag: ${factory.fragmentTag}")
                activity.finish()
                return
            }
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