package kr.co.aerix.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object SensorScheme : IntIdTable("sensor") {
    val mac = text("mac");
    val provider = text("provider");
    val model = text("model");
    val variable = bool("variable");
    val value = integer("value");
    val min = integer("min");
    val max = integer("max");
    val curValue = integer("curValue").default(0);
    val project_id = integer("project_id").references(ProjectScheme.id)
    val scanner_id = integer("scanner_id").references(ScannerScheme.id)
    val createdAt = datetime("created_at").index().default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

class Sensor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Sensor>(SensorScheme)
    var mac by SensorScheme.mac
    var provider by SensorScheme.provider
    var model by SensorScheme.model
    var variable by SensorScheme.variable
    var value by SensorScheme.value
    var min by SensorScheme.min
    var max by SensorScheme.max
    var curValue by SensorScheme.curValue
    var project_id by SensorScheme.project_id
    var scanner_id by SensorScheme.scanner_id

    val createdAt by SensorScheme.createdAt
    val updatedAt by SensorScheme.updatedAt
}