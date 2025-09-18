package com.gabinote.coffeenote.testSupport.testConfig.jackson

import com.gabinote.coffeenote.common.config.JacksonConfig
import com.gabinote.coffeenote.common.util.json.fieldType.FieldTypeDeserializer
import com.gabinote.coffeenote.common.util.json.fieldType.FieldTypeModule
import com.gabinote.coffeenote.common.util.json.fieldType.FieldTypeSerializer
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

@Import(
    JacksonConfig::class,
    FieldTypeSerializer::class,
    FieldTypeDeserializer::class,
    FieldTypeModule::class
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class UseJackson
