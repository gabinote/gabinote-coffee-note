package com.gabinote.coffeenote.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.gabinote.coffeenote.common.util.json.fieldType.FieldTypeDeserializer
import com.gabinote.coffeenote.common.util.json.fieldType.FieldTypeModule
import com.gabinote.coffeenote.common.util.json.fieldType.FieldTypeSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Jackson JSON 라이브러리 설정 클래스
 * JSON 변환 관련 설정 및 커스터마이징을 담당
 * @author 황준서
 */
@Configuration
class JacksonConfig(
    private val fieldTypeSerializer: FieldTypeSerializer,
    private val fieldTypeDeserializer: FieldTypeDeserializer
) {

    /**
     * ObjectMapper 빈 구성
     * Kotlin과 Java 8 시간 타입을 위한 모듈 등록
     * @param builder Jackson2ObjectMapperBuilder 인스턴스
     * @return 구성된 ObjectMapper
     */
    @Bean
    fun objectMapper(
        builder: Jackson2ObjectMapperBuilder,
        fieldTypeModule: FieldTypeModule
    ): ObjectMapper {
        val objectMapper = builder.createXmlMapper(false).build<ObjectMapper>()

        // Kotlin 모듈 등록
        objectMapper.registerModule(KotlinModule.Builder().build())

        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addSerializer(
            LocalDateTime::class.java,
            LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
        objectMapper.registerModule(javaTimeModule)

        // Custom Module 등록
        objectMapper.registerModule(fieldTypeModule)


        // Serialization 설정
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        return objectMapper
    }

    /**
     * Jackson2ObjectMapperBuilder 빈 구성
     * @return Jackson2ObjectMapperBuilder 인스턴스
     */
    @Bean
    fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilder {
        return Jackson2ObjectMapperBuilder()
    }

//    @Bean
//    fun fieldTypeModule(): FieldTypeModule {
//        // 주입받은 Bean들을 사용하여 모듈을 생성하고, 이 모듈을 Bean으로 등록
//        return FieldTypeModule(fieldTypeSerializer, fieldTypeDeserializer)
//    }
}