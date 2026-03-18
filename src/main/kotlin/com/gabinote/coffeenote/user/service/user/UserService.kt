package com.gabinote.coffeenote.user.service.user

import com.gabinote.coffeenote.common.util.exception.service.ForbiddenByPolicy
import com.gabinote.coffeenote.common.util.exception.service.ResourceForbidden
import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.policy.domain.policy.PolicyKey
import com.gabinote.coffeenote.policy.service.policy.PolicyService
import com.gabinote.coffeenote.user.domain.user.User
import com.gabinote.coffeenote.user.domain.user.UserRepository
import com.gabinote.coffeenote.user.dto.user.service.UserRegisterReqServiceDto
import com.gabinote.coffeenote.user.dto.user.service.UserResServiceDto
import com.gabinote.coffeenote.user.dto.user.service.UserUpdateReqServiceDto
import com.gabinote.coffeenote.user.mapping.user.UserMapper
import com.gabinote.coffeenote.user.service.keycloakUser.KeycloakUserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


private val logger = KotlinLogging.logger {}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val policyService: PolicyService,
    private val keycloakUserService: KeycloakUserService,
) {

    fun fetchByUid(uid: UUID): User {
        return userRepository.findByUid(uid.toString())
            ?: throw ResourceNotFound(
                name = "User",
                identifier = uid.toString(),
                identifierType = "iid"
            )
    }

    fun getUserByUid(uid: UUID): UserResServiceDto {
        val user = fetchByUid(uid)
        return userMapper.toResServiceDto(user)
    }

    fun getOpenProfileUserByUid(uid: UUID, requestor: UUID): UserResServiceDto {
        val user = fetchByUid(uid)
        if (!user.isOpenProfile) {
            throw ResourceForbidden(
                name = "User Profile",
                act = "access closed profile",
                blockedUser = requestor.toString()
            )

        }
        return userMapper.toResServiceDto(user)
    }

    @Transactional
    fun createUser(dto: UserRegisterReqServiceDto): UserResServiceDto {
        checkCanRegister()
        val user = userMapper.toUser(dto)
        val savedUser = userRepository.save(user)
        keycloakUserService.updateUserRole(
            userId = dto.uid.toString(),
            roleName = policyService.getByKey(PolicyKey.USER_REGISTER_BASE_ROLE)
        )
        return userMapper.toResServiceDto(savedUser)
    }

    fun updateUser(dto: UserUpdateReqServiceDto): UserResServiceDto {
        val existingUser = fetchByUid(dto.uid)

        userMapper.updateUserFromDto(
            source = dto,
            target = existingUser
        )

        val savedUser = userRepository.save(existingUser)
        return userMapper.toResServiceDto(savedUser)
    }


    fun delete(uid: UUID) {
        val user = fetchByUid(uid)
        userRepository.delete(user)
    }


    fun getAllUsers(pageable: Pageable): Page<UserResServiceDto> {
        return userRepository.findAll(pageable)
            .map { userMapper.toResServiceDto(it) }
    }

    private fun checkCanRegister() {
        val isAllowed = policyService.getByKey(PolicyKey.USER_ENABLED_REGISTER)
        if (!isAllowed.toBoolean()) {
            throw ForbiddenByPolicy(
                reason = "User registration is disabled by policy."
            )
        }
    }

}