package com.gabinote.coffeenote.testSupport.testConfig.meiliSearch

import com.gabinote.coffeenote.testSupport.testConfig.container.ContainerNetworkHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer

private val log = KotlinLogging.logger {}

class MeiliSearchContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {


    companion object {
        @JvmStatic
        val meiliSearch = GenericContainer("getmeili/meilisearch:v1.22.2").apply {
            withNetwork(ContainerNetworkHelper.testNetwork)
            withNetworkAliases("meilisearch")
            withExposedPorts(7700)
            withLabel("test-container", "meilisearch")
            withEnv("MEILI_MASTER_KEY", "masterKey")
            withReuse(true)
        }
    }

    override fun initialize(context: ConfigurableApplicationContext) {
        // 테스트 컨테이너 시작
        meiliSearch.start()

        log.debug { "meilisearch start with ports : ${meiliSearch.boundPortNumbers}" }

        TestPropertyValues.of(
            "meilisearch.url=http://${meiliSearch.host}:${meiliSearch.getMappedPort(7700)}",
            "meilisearch.apiKey=masterKey"
        ).applyTo(context.environment)

    }

}