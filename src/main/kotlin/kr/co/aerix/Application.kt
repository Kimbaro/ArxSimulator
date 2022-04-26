package kr.co.aerix

import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.coroutines.async
import kr.co.aerix.plugins.*
import kr.co.aerix.plugins.DatabaseInitializer.hikariConfig
import kr.co.aerix.router.*
import kr.co.aerix.service.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureMonitoring()
    configureHTTP()
    configureSerialization()
    DatabaseInitializer.init(
        environment.config.property("db_init.insert").getString(),
        environment.config.property("db_init.create").getString(),
        hikariConfig(
            environment.config.property("db_info.driverClassName").getString(),
            environment.config.property("db_info.jdbcUrl").getString(),
            environment.config.property("db_info.maximumPoolSize").getString(),
            environment.config.property("db_info.isAutoCommit").getString(),
            environment.config.property("db_info.username").getString(),
            environment.config.property("db_info.password").getString(),
            environment.config.property("db_info.transactionIsolation").getString()
        )
    )

    install(Routing) {
        project(
            ProjectService(),
            MqttService(),
            environment.config.property("mqttmodule.ip").getString(),
            environment.config.property("mqttmodule.port").getString()
        );
        device(DeviceService(), ProjectService(), GatewayService());
        sensor(SensorService());
        mqtt(ProjectService());
    }
    async {
        configureRealtimeUpdate(ProjectService())   // 가상 센서의 현재값을 업데이트합니다
    }
}
