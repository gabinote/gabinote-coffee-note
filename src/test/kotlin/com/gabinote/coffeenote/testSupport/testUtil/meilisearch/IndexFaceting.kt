package com.gabinote.coffeenote.testSupport.testUtil.meilisearch

import com.gabinote.coffeenote.common.util.json.annotation.JsonNoArg
import com.meilisearch.sdk.model.FacetSortValue
import com.meilisearch.sdk.model.Faceting
import java.util.*

@JsonNoArg
data class IndexFaceting(
    val maxValuesPerFacet: Int,
    val sortFacetValuesBy: Map<String, String>,
) {
    fun toConfigFormat(): Faceting {
        val faceting = Faceting()
        faceting.maxValuesPerFacet = maxValuesPerFacet
        faceting.sortFacetValuesBy = toSortHashMap()
        return faceting
    }

    private fun toSortHashMap(): HashMap<String, FacetSortValue> {
        val sortRules = LinkedHashMap<String, FacetSortValue>().apply {
            sortFacetValuesBy.forEach { (key, value) ->
                this[key] = FacetSortValue.valueOf(value.uppercase(Locale.getDefault()))
            }
        }

        return sortRules
    }
}