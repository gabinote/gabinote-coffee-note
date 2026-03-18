package com.gabinote.coffeenote.user.web.controller

import com.gabinote.coffeenote.user.dto.user.controller.UserFullResControllerDto
import com.gabinote.coffeenote.user.mapping.user.UserMapper
import com.gabinote.coffeenote.user.service.user.UserService
import com.gabinote.coffeenote.user.service.userWithdraw.UserWithdrawService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RequestMapping("/admin/api/v1/user")
@RestController
class UserAdminApiController(
    private val userService: UserService,
    private val userWithdrawService: UserWithdrawService,
    private val userMapper: UserMapper,
) {
    @GetMapping("/{uid}")
    fun getUserByAdmin(
        @PathVariable uid: UUID,
    ): ResponseEntity<UserFullResControllerDto> {
        val user = userService.getUserByUid(uid)
        val res = userMapper.toFullResControllerDto(user)
        return ResponseEntity.ok(res)
    }

    @PostMapping("/withdraw/purge")
    fun runWithdrawalPurge(): ResponseEntity<Void> {
        userWithdrawService.runForcePurgeWithdrawal()
        return ResponseEntity.ok().build()
    }
}