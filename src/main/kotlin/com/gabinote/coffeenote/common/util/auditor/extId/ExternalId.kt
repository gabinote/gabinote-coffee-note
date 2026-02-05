package com.gabinote.coffeenote.common.util.auditor.extId

/**
 * MongoDB 엔티티의 필드중 External ID로 사용될 필드에 붙이는 어노테이션
 * 해당 필드는 저장 전에 UUID 값으로 자동 설정됨
 * @see ExternalIdListener
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ExternalId
