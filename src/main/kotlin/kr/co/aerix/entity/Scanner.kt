package kr.co.aerix.entity

import kr.co.aerix.entity.ScannerScheme.default
import kr.co.aerix.entity.ScannerScheme.index
import kr.co.aerix.entity.SensorScheme.references
import kr.co.aerix.entity.SensorScheme.uniqueIndex
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object ScannerScheme : IntIdTable("scanner") {
    val ip = text("ip");
    val mqtt_ip = text("mqtt_ip");
    val mqtt_port = text("mqtt_port")
    val mqtt_topic = text("mqtt_topic")
    val name = text("name");
    val mode = bool("mode");
    val enable = bool("enable");
    val parsing = bool("parsing");
    val scanDuration = integer("scanDuration");
    val project_id = integer("scanner_id").references(ProjectScheme.id)
    val createdAt = datetime("created_at").index().default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

class Scanner(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Scanner>(ScannerScheme)

    var ip by ScannerScheme.ip
    var mqtt_ip by ScannerScheme.mqtt_ip
    var mqtt_port by ScannerScheme.mqtt_port
    var mqtt_topic by ScannerScheme.mqtt_topic
    var name by ScannerScheme.name
    var mode by ScannerScheme.mode
    var enable by ScannerScheme.enable
    var parsing by ScannerScheme.parsing
    var scanDuration by ScannerScheme.scanDuration
    var project_id by ScannerScheme.project_id
    var createdAt by ScannerScheme.createdAt
    var updatedAt by ScannerScheme.updatedAt
}