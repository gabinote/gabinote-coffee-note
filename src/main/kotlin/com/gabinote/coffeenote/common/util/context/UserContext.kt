package com.gabinote.coffeenote.common.util.context

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserContext(
    private var _uid: String? = null,
    var roles: List<String> = emptyList()
) {
    var uid: String
        get() = _uid ?: throw UserContextNotFound()
        set(value) {
            _uid = value
        }

    fun isLoggedIn(): Boolean = _uid != null
}