package com.gabinote.coffeenote.common.util.img

/**
 * 이미지 관련 유효성 검증을 도와주는 헬퍼 클래스
 */
object ImgValidationHelper {
    const val UUID_REGEX =
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\.[a-zA-Z0-9]+$"

    /**
     * 올바른 이미지 파일 이름 형식인지 (uuid + 확장자 형식인지 ) 검증
     * 예시: "123e4567-e89b-12d3-a456-426614174000.jpg" -> true, "invalid_image.png" -> false
     * 이때 확장자는 검증하지 않으니 유의
     * @param fileName 검증할 파일 이름
     * @return 올바른 이미지 파일 이름 형식이면 true, 그렇지 않으면 false
     */
    fun isImage(fileName: String): Boolean {
        val regex = UUID_REGEX.toRegex()
        return regex.matches(fileName)
    }
}