package io.github.soat7.myburguercontrol.container

import org.testcontainers.containers.PostgreSQLContainer

object PostgresContainer {

    @JvmStatic
    val postgresql =
        PostgreSQLContainer("postgres:16-alpine").apply {
            withDatabaseName(System.getenv("POSTGRES_DB"))
            withUsername(System.getenv("DATABASE_USER"))
            withPassword(System.getenv("DATABASE_PASSWORD"))
            withEnv(mapOf("PGDATA" to "/var/lib/postgresql/data"))
            withTmpFs(mapOf("/var/lib/postgresql/data" to "rw"))
            withReuse(true)
            start()
        }

    fun waitUntilUp() {
        do {
            Thread.sleep(100)
        } while (!postgresql.isCreated)
    }

    fun stop() {
        postgresql.stop()
    }

}
