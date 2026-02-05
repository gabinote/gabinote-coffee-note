package com.gabinote.coffeenote.common.util.context

/**
 * UserContext가 존재하지 않을 때 발생하는 예외
 */
class UserContextNotFound : RuntimeException("User context not found")