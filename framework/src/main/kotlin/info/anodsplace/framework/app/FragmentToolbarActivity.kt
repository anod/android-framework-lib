package info.anodsplace.framework.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import info.anodsplace.framework.R

/**
 * @author Alex Gavrishev
 * @date 16/12/2016.
 */


open class FragmentToolbarActivity : ToolbarActivity() {

    override val themeRes: Int
        get() = intentExtras.getInt("themeRes", 0)

    override val themeColors: CustomThemeColors
        get() = intentExtras.getParcelable("themeColors")
                ?: CustomThemeColors.none

    override val layoutView: View
        get() = layoutInflater.inflate(R.layout.activity_fragment_toolbar, null, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            FragmentContainerActivity.attach(R.id.activity_content, this)
        }
    }

    companion object {
        fun intent(context: Context, factory: FragmentFactory, arguments: Bundle = Bundle.EMPTY, themeRes: Int = 0, themeColors: CustomThemeColors = CustomThemeColors.none, clazz: Class<*> = FragmentToolbarActivity::class.java): Intent {
            return FragmentContainerActivity.intent(context, factory, arguments).apply {
                putExtra("themeRes", themeRes)
                putExtra("themeColors", themeColors)
            }
        }
    }
}
