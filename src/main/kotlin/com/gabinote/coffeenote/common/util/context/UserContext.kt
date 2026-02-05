package com.gabinote.coffeenote.common.util.context

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

/**
 * 요청(Request) 스코프를 가지는 사용자 컨텍스트 클래스
 * 각 HTTP 요청마다 별도의 UserContext 인스턴스가 생성되어 사용자 정보를 저장
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserContext(
    private var _uid: String? = null,
    var roles: List<String> = emptyList(),
) {
    /**
     * 현재 요청의 사용자 고유 ID
     * 값이 설정되지 않은 상태에서 접근하려고 하면 UserContextNotFound 예외 발생
     *
     * @throws UserContextNotFound 사용자 컨텍스트에 UID가 설정되지 않은 경우 발생
     */
    var uid: String
        get() = _uid ?: throw UserContextNotFound()
        set(value) {
            _uid = value
        }

    /**
     * 사용자가 로그인된 상태인지 여부를 반환
     */
    fun isLoggedIn(): Boolean = _uid != null
}