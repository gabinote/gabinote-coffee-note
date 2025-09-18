package com.gabinote.coffeenote.common.util.strategy

import jakarta.annotation.PostConstruct

abstract class AbstractStrategyFactory<E : Enum<E>, S : Strategy<E>>(
    private val strategies: List<S>
) {
    private lateinit var strategyMap: Map<E, S>

    @PostConstruct
    fun init() {
        // 주입받은 전략 리스트를 순회하며, 각 전략의 getType()을 Key로 하여 Map을 생성
        this.strategyMap = strategies.associateBy { it.type }
    }

    fun getStrategy(key: E): S {
        return strategyMap[key] ?: throw IllegalArgumentException("Invalid strategy key: $key")
    }

}