package kr.co.aerix.model

import kr.co.aerix.entity.GatewayScheme
import kr.co.aerix.entity.GatewayScheme.default
import kr.co.aerix.entity.GatewayScheme.index
import kr.co.aerix.entity.GatewayScheme.references
import kr.co.aerix.entity.ProjectScheme
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class Gateway_Domain(
    var id: Int?,
    val projectID: Int,
    val ip: String,
    val mqtt: String,
    val port: Int,
    val mode: Boolean,
    val virtualIP: String,
    val state: Boolean
)


data class ScannerReq_Domain(
    val id: Int,
    val projectID: Int,
    val ip: String,
    val mqtt: String,
    val port: String,
    val topic: String,
    val name: String,
    val mode: Boolean,
    val enable: Boolean,
    val parsing: Boolean,
    val scanDuration: Int,
)

data class ScannerRes_Domain(
    val id: Int,
    val ip: String,
    val mqtt: String,
    val port: String,
    val topic: String,
    val name: String,
    val mode: Boolean,
    val enable: Boolean,
    val parsing: Boolean,
    val scanDuration: Int,
    val sensors: List<Sensor_domain>,
    val projectID: Int
)