package kr.co.aerix.plugins

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import kotlinx.coroutines.*
import kr.co.aerix.entity.Sensor
import kr.co.aerix.model.Project_Mqtt_Res_domain
import kr.co.aerix.model.Project_RealtimeUpdate_Res_domain
import kr.co.aerix.model.Scanner_Mqtt_Res_Domain
import kr.co.aerix.model.Sensor_Mqtt_Res_Domain
import kr.co.aerix.service.ProjectService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

suspend fun Application.configureRealtimeUpdate(service_project: ProjectService) {
    coroutineScope {
        var projects: List<Project_RealtimeUpdate_Res_domain>? = null
        launch(Dispatchers.IO) {
            println("값을 가져옴")
            while (isActive) {
                try {
                    projects = service_project.getAllByProjectAndSensorData()
                } catch (e: Exception) {
                    println("EEEEEE ${e}")
                }
                delay(1000L)
            }
        }
        launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    if (!projects.isNullOrEmpty()) {
                        projects!!.forEach {
                            val sensors: MutableList<Sensor_Mqtt_Res_Domain> = it.sensors.toMutableList()
                            service_project.updateSensor(sensors)
                        }
                    }
                } catch (e: Exception) {
                    println("EEEEEE ${e}")
                }
                delay(1000L)
            }
        }
    }
}