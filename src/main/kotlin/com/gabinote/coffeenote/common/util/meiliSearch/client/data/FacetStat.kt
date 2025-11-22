package com.gabinote.coffeenote.common.util.meiliSearch.client.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FacetStat(
    val min: Double,

    val max: Double
)