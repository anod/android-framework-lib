package info.anodsplace.compose.chooser

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface ChooserLoader {
    fun load(): Flow<List<ChooserEntry>>
}

class StaticChooserLoader(private val list: List<ChooserEntry>) : ChooserLoader {
    override fun load(): Flow<List<ChooserEntry>> = flowOf(list)
}