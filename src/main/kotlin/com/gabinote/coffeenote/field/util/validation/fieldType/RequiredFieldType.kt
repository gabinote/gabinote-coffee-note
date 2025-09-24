//package com.gabinote.coffeenote.field.util.validation.fieldType
//
//import jakarta.validation.Constraint
//import jakarta.validation.Payload
//import kotlin.reflect.KClass
//
///**
// * 유효한 필드 타입을 검증하는 애노테이션
// * 필드 타입이 등록된 타입 중 하나인지 검증
// * @author 황준서
// */
//@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
//@Retention(AnnotationRetention.RUNTIME)
//@Constraint(validatedBy = [RequiredFieldTypeValidator::class])
//annotation class RequiredFieldType(
//    /**
//     * null 값 허용 여부
//     */
//    val allowedNull: Boolean = false,
//
//    /**
//     * 검증 그룹
//     */
//    val groups: Array<KClass<*>> = [],
//
//    /**
//     * 검증 실패 시 메시지
//     */
//    val message: String = "Field type not valid",
//
//    /**
//     * 검증 페이로드
//     */
//    val payload: Array<KClass<out Payload>> = []
//)
