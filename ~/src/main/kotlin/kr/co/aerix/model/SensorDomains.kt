package kr.co.aerix.model

import kr.co.aerix.entity.Sensor
import kr.co.aerix.entity.SensorScheme
import java.time.LocalDateTime

data class Sensor_domain(
    val id: Int,
    val mac: String,
    val provider: String,
    val model: String,
    val variable: Boolean,
    val value: Int,
    val min: Int,
    val max: Int,
    val curValue: Int,
    val projectID: Int,
    val scannerID: Int,
) {
    companion object {
        fun of(sensor: Sensor) = Sensor_domain(
            id = sensor.id.value,
            mac = sensor.mac,
            provider = sensor.provider,
            model = sensor.model,
            variable = sensor.variable,
            value = sensor.value,
            min = sensor.min,
            max = sensor.max,
            curValue = sensor.curValue,
            projectID = sensor.project_id,
            scannerID = sensor.scanner_id,
        )
    }
};

data class SensorReq_Domain(
    val id: Int,
    val projectID: Int,
    val scannerID: Int,
    val mac: String,
    val provider: String,
    val model: String,
    val variable: Boolean,
    val value: Int,
    val min: Int,
    val max: Int
)
