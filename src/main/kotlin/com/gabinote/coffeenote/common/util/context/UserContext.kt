package com.gabinote.coffeenote.common.util.context

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import java.util.*

/**
 * 요청(Request) 스코프를 가지는 사용자 컨텍스트 클래스
 * 각 HTTP 요청마다 별도의 UserContext 인스턴스가 생성되어 사용자 정보를 저장
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserContext(
    var isAuthorized: Boolean = false,
    private var _uid: String? = null,
    var roles: List<String> = emptyList(),
) {
    var uid: String
        get() = _uid ?: throw UserContextNotFound()
        set(value) {
            _uid = value
        }

    fun isLoggedIn(): Boolean = _uid != null

    fun uidWithUUID(): UUID {
        return UUID.fromString(uid)
    }

    fun setContext(
        uid: String,
        roles: List<String>,
    ) {
        this.isAuthorized = true
        this._uid = uid
        this.roles = roles
    }
}