package com.gabinote.coffeenote.template.domain.template

import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * 템플릿 리포지토리 인터페이스
 * MongoDB를 사용하여 템플릿 데이터를 관리
 * @author 황준서
 */
interface TemplateRepository : MongoRepository<Template, ObjectId> {

    /**
     * 외부 식별자로 템플릿을 조회
     * @param externalId 외부 식별자
     * @return 템플릿 또는 null
     */
    fun findByExternalId(externalId: String): Template?

    /**
     * 소유자에 따른 템플릿 목록 조회
     * @param owner 소유자
     * @param pageable 페이징 정보
     * @return 템플릿 슬라이스
     */
    fun findAllByOwner(owner: String, pageable: Pageable): Slice<Template>

    /**
     * 기본 템플릿 여부에 따른 템플릿 목록 조회
     * @param isDefault 기본 템플릿 여부 (기본값: true)
     * @param pageable 페이징 정보
     * @return 템플릿 슬라이스
     */
    fun findAllByIsDefault(isDefault: Boolean = true, pageable: Pageable): Slice<Template>

    /**
     * 소유자 또는 기본 템플릿 여부에 따른 템플릿 목록 조회
     * @param owner 소유자
     * @param isDefault 기본 템플릿 여부 (기본값: true)
     * @param pageable 페이징 정보
     * @return 템플릿 슬라이스
     */
    fun findAllByOwnerOrIsDefault(owner: String, isDefault: Boolean = true, pageable: Pageable): Slice<Template>

    fun findAllBy(pageable: Pageable): Slice<Template>
}