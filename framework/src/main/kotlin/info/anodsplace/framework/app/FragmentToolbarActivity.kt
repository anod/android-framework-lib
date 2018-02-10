package info.anodsplace.framework.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import info.anodsplace.framework.R

import info.anodsplace.framework.AppLog

/**
 * @author Alex Gavrishev
 * @date 16/12/2016.
 */
typealias FragmentCreator = () -> Fragment

class FragmentToolbarActivity : ToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeRes = intentExtras.getInt("themeRes", 0)
        if (themeRes > 0) {
            setTheme(themeRes)
        }
        setContentView(R.layout.activity_fragment)
        setupToolbar()

        if (savedInstanceState == null) {
            val fragmentTag = intent.getStringExtra(EXTRA_FRAGMENT)
            val f = createFragment(fragmentTag)
            if (f == null) {
                AppLog.e("Missing fragment for tag: " + fragmentTag)
                finish()
                return
            }
            f.arguments = intent.getBundleExtra(EXTRA_ARGUMENTS)

            supportFragmentManager.beginTransaction()
                    .add(R.id.activity_content, f, fragmentTag)
                    .commit()
        }
    }

    private fun createFragment(fragmentTag: String): Fragment? {
        val creator = fragments[fragmentTag]
        if (creator != null) {
            return creator()
        }
        return null
    }

    companion object {
        private const val EXTRA_FRAGMENT = "extra_fragment"
        private const val EXTRA_ARGUMENTS = "extra_arguments"
        private var fragments: MutableMap<String, FragmentCreator> = mutableMapOf()

        fun register(tag: String, creator: FragmentCreator) {
            fragments[tag] = creator
        }

        fun intent(tag: String, creator: FragmentCreator, themeRes: Int, args: Bundle, context: Context): Intent {
            register(tag, creator)
            val intent = Intent(context, FragmentToolbarActivity::class.java)
            intent.putExtra(EXTRA_FRAGMENT, tag)
            intent.putExtra(EXTRA_ARGUMENTS, args)
            intent.putExtra("themeRes", themeRes)
            return intent
        }
    }
}
