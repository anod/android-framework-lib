package info.anodsplace.compose.chooser

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Stable
interface ChooserLoader {
    fun load(): Flow<List<ChooserEntry>>
}

class StaticChooserLoader(private val list: List<ChooserEntry>) : ChooserLoader {
    override fun load(): Flow<List<ChooserEntry>> = flowOf(list)
}