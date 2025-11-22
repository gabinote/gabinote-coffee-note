package com.gabinote.coffeenote.common.util.exception.service

class ResourceQuotaLimit(
    name: String,
    quotaType: String,
    quotaLimit: Int,
) : ServiceException() {

    override val errorMessage: String =
        "The resource quota limit for $name has been reached. The maximum allowed $quotaType is $quotaLimit."


    override val logMessage: String = errorMessage

}