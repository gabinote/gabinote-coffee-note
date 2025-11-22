package com.gabinote.coffeenote.common.util.meiliSearch.client.data

//{
//    "displayedAttributes": [
//    "*"
//    ],
//    "searchableAttributes": [
//    "*"
//    ],
//    "filterableAttributes": [],
//    "sortableAttributes": [],
//    "rankingRules":
//    [
//        "words",
//        "typo",
//        "proximity",
//        "attribute",
//        "sort",
//        "exactness"
//    ],
//    "stopWords": [],
//    "nonSeparatorTokens": [],
//    "separatorTokens": [],
//    "dictionary": [],
//    "synonyms": {},
//    "distinctAttribute": null,
//    "typoTolerance": {
//    "enabled": true,
//    "minWordSizeForTypos": {
//    "oneTypo": 5,
//    "twoTypos": 9
//},
//    "disableOnWords": [],
//    "disableOnAttributes": []
//},
//    "faceting": {
//    "maxValuesPerFacet": 100
//},
//    "pagination": {
//    "maxTotalHits": 1000
//},
//    "proximityPrecision": "byWord",
//    "facetSearch": true,
//    "prefixSearch": "indexingTime",
//    "searchCutoffMs": null,
//    "embedders": {},
//    "chat": {},
//    "vectorStore": "stable"
//}
data class SettingReq(
    val displayedAttributes: List<String>? = null,
    val searchableAttributes: List<String>? = null,
    val filterableAttributes: List<String>? = null,
    val sortableAttributes: List<String>? = null,
    val rankingRules: List<String>? = null,
    val stopWords: List<String>? = null,
)