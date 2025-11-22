package com.gabinote.coffeenote.policy.domain.policy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "policy")
data class Policy(
    @Id
    var id: Object? = null,

    var key: String,

    var value: String,
)