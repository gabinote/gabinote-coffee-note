package com.gabinote.coffeenote.common.util.controller

import org.springframework.http.ResponseEntity

/**
 * ResponseEntity 관련 유틸리티 함수 모음
 */
object ResponseEntityHelper {

    /**
     * 본문이 없는 204 No Content 응답 생성
     */
    fun noContent(): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }

    /**
     * 본문이 있는 201 Created 응답 생성
     *
     * @param body 응답 본문
     */
    fun <T> created(body: T): ResponseEntity<T> {
        return ResponseEntity.status(201).body(body)
    }

}