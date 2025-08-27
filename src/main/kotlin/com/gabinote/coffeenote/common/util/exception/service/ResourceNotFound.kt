package com.gabinote.coffeenote.common.util.exception.service

class ResourceNotFound(
    name: String,
    identifier: String,
    identifierType: String? = null,
) : ServiceException() {

    override val errorMessage : String = "$name not found with identifier($identifierType): $identifier"

    override val logMessage: String = errorMessage
}