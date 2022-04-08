package kr.co.aerix

import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.coroutines.async
import kr.co.aerix.plugins.*
import kr.co.aerix.router.*
import kr.co.aerix.service.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureMonitoring()
    configureHTTP()
    configureSerialization()
    DatabaseInitializer.init()

    install(Routing) {
        project(ProjectService(), MqttService());
        device(DeviceService(), ProjectService(), GatewayService());
        sensor(SensorService());
        mqtt(ProjectService());
    }
    async {
        configureRealtimeUpdate(ProjectService())   // 가상 센서의 현재값을 업데이트합니다
    }
}
