package com.gabinote.coffeenote.field.domain.field

import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * 필드 엔티티에 대한 데이터 액세스를 제공하는 리포지토리 인터페이스
 * @author 황준서
 */
@Repository
interface FieldRepository : MongoRepository<Field, ObjectId> {

    /**
     * 기본 필드 여부에 따라 필드 목록을 페이징하여 조회
     * @param default 기본 필드 여부
     * @param pageable 페이지 정보
     * @return 필드 슬라이스
     */
    fun findAllByIsDefault(default: Boolean = true, pageable: Pageable): Slice<Field>

    /**
     * 기본 필드이거나 특정 소유자의 필드를 페이징하여 조회
     * @param default 기본 필드 여부
     * @param owner 소유자 식별자
     * @param pageable 페이지 정보
     * @return 필드 슬라이스
     */
    fun findAllByIsDefaultOrOwner(default: Boolean = true, owner: String, pageable: Pageable): Slice<Field>

    /**
     * 특정 소유자의 필드를 페이징하여 조회
     * @param owner 소유자 식별자
     * @param pageable 페이지 정보
     * @return 필드 슬라이스
     */
    fun findAllByOwner(owner: String, pageable: Pageable): Slice<Field>

    /**
     * 외부 식별자로 필드 조회
     * @param externalId 외부 식별자
     * @return 조회된 필드 또는 null
     */
    fun findByExternalId(externalId: String): Field?

    /**
     * 외부 식별자로 필드 삭제
     * @param externalId 외부 식별자
     * @return 삭제된 필드 리스트
     */
    fun deleteByExternalId(externalId: String): List<Field>

    /**
     * 모든 필드를 페이징하여 조회
     * @param pageable 페이지 정보
     * @return 필드 슬라이스
     */
    fun findAllBy(pageable: Pageable): Slice<Field>
}