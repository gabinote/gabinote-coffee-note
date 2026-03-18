package com.gabinote.coffeenote.user.web.controller


import com.gabinote.coffeenote.common.aop.auth.NeedAuth
import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.common.util.controller.ResponseEntityHelper
import com.gabinote.coffeenote.user.dto.user.controller.UserFullResControllerDto
import com.gabinote.coffeenote.user.dto.user.controller.UserMinimalResControllerDto
import com.gabinote.coffeenote.user.dto.user.controller.UserRegisterReqControllerDto
import com.gabinote.coffeenote.user.dto.user.controller.UserUpdateReqControllerDto
import com.gabinote.coffeenote.user.mapping.user.UserMapper
import com.gabinote.coffeenote.user.service.user.UserService
import com.gabinote.coffeenote.user.service.userWithdraw.UserWithdrawService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RequestMapping("/api/v1/user")
@RestController
class UserApiController(
    private val userService: UserService,
    private val userMapper: UserMapper,
    private val userContext: UserContext,
    private val userWithdrawService: UserWithdrawService,
) {

    @NeedAuth
    @GetMapping("/me")
    fun getMyInfo(): ResponseEntity<UserFullResControllerDto> {
        val user = userService.getUserByUid(userContext.uidWithUUID())
        val res = userMapper.toFullResControllerDto(user)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/{uid}")
    fun getOpenProfileUserInfo(
        @PathVariable uid: UUID,
    ): ResponseEntity<UserMinimalResControllerDto> {
        val user = userService.getOpenProfileUserByUid(uid, userContext.uidWithUUID())
        val res = userMapper.toMinimalResControllerDto(user)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @PostMapping("/me/register")
    fun register(
        @Valid
        @RequestBody
        req: UserRegisterReqControllerDto,
    ): ResponseEntity<UserFullResControllerDto> {
        val reqDto = userMapper.toRegisterReqServiceDto(req, userContext.uidWithUUID())
        val user = userService.createUser(reqDto)
        val res = userMapper.toFullResControllerDto(user)
        return ResponseEntityHelper.created(res)
    }

    @NeedAuth
    @PutMapping("/me")
    fun updateMyInfo(
        @Valid
        @RequestBody
        req: UserUpdateReqControllerDto,
    ): ResponseEntity<UserFullResControllerDto> {
        val reqDto = userMapper.toUpdateReqServiceDto(req, userContext.uidWithUUID())
        val user = userService.updateUser(reqDto)
        val res = userMapper.toFullResControllerDto(user)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @PostMapping("/me/withdraw")
    fun withdraw(): ResponseEntity<Void> {
        userWithdrawService.withdrawUser(userContext.uidWithUUID())
        return ResponseEntity.noContent().build()
    }


}