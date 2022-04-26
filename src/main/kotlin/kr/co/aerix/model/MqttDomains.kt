package kr.co.aerix.model

import com.google.gson.JsonArray
import java.time.LocalDateTime

data class Project_Mqtt_Res_domain(
    val id: Int,
    val mode: Boolean,
    val scanners: MutableList<Scanner_Mqtt_Res_Domain>,
    val gateways: MutableList<Gateway_Domain>
);

data class Project_RealtimeUpdate_Res_domain(
    val id: Int,
    val mode: Boolean,
    val sensors: List<Sensor_Mqtt_Res_Domain>,
);

data class Mqtt_Res_domain(
    val id: Int,
    val mode: Boolean,
    val datas: JsonArray,
);

data class Scanner_Mqtt_Res_Domain(
    val id: Int,
    val ip: String,
    val mqtt_ip: String,
    val mqtt_port: String,
    val mqtt_topic: String,
    val mode: Boolean,
    val enable: Boolean,
    val parsing: Boolean,
    val scanDuration: Int,
    val sensors: List<Sensor_Mqtt_Res_Domain>,
)

data class Sensor_Mqtt_Res_Domain(
    val id: Int,
    val projectID: Int,
    val scannerID: Int,
    val mac: String,
    val provider: String,
    val model: String,
    val variable: Boolean,
    val value: Int,
    val min: Int,
    val max: Int,
    var curValue: Int
)