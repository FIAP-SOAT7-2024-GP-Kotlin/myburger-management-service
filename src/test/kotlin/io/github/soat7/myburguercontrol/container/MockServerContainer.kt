package io.github.soat7.myburguercontrol.container

import io.github.oshai.kotlinlogging.KotlinLogging
import org.mockserver.client.MockServerClient
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.utility.DockerImageName

private val log = KotlinLogging.logger { }

object MockServerContainer {

    private const val VERSION = "5.15.0"

    val mockserver =
        MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:mockserver-$VERSION"),
        ).apply {
            withReuse(true)
//            withLogConsumer {
//                when (it.type) {
//                    OutputFrame.OutputType.STDERR -> log.error { it.utf8StringWithoutLineEnding }
//                    else -> log.info { it.utf8StringWithoutLineEnding }
//                }
//            }
            start()
        }

    fun client() =
        run {
            do {
                Thread.sleep(100)
            } while (!mockserver.isCreated)

            MockServerClient(mockserver.host, mockserver.serverPort)
        }
}
