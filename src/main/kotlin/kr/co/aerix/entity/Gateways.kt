package kr.co.aerix.entity

import kr.co.aerix.entity.GatewayScheme.default
import kr.co.aerix.entity.GatewayScheme.index
import kr.co.aerix.entity.GatewayScheme.references
import kr.co.aerix.entity.ProjectScheme.default
import kr.co.aerix.entity.ProjectScheme.index
import kr.co.aerix.entity.SensorScheme.default
import kr.co.aerix.entity.SensorScheme.index
import kr.co.aerix.entity.SensorScheme.references
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object GatewayScheme : IntIdTable("gateway") {
    val ip = text("ip").uniqueIndex()
    val mqtt_ip = text("mqtt_ip")
    val mqtt_port = integer("mqtt_port")
    val gateway_mode = bool("mqtt_mode")
    val virtual_ip = text("virtual_ip")
    val state = bool("state")
    val project_id = integer("project_id").references(ProjectScheme.id)
    val createdAt = datetime("created_at").index().default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

class Gateway(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Gateway>(GatewayScheme)

    var ip by GatewayScheme.ip
    var mqtt_ip by GatewayScheme.mqtt_ip
    var mqtt_port by GatewayScheme.mqtt_port
    var gateway_mode by GatewayScheme.gateway_mode
    var virtual_ip by GatewayScheme.virtual_ip
    var state by GatewayScheme.state
    var project_id by GatewayScheme.project_id
    var createdAt by GatewayScheme.createdAt
    var updatedAt by GatewayScheme.updatedAt
}