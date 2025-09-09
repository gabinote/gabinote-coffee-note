package com.gabinote.coffeenote.common.util.exception.controller

class GatewayAuthFailed : ControllerException() {

    override val errorMessage: String = "Gateway authentication failed"

    override val logMessage: String = errorMessage
}