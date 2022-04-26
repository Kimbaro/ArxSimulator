package kr.co.aerix.service

import io.ktor.http.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.Gateway_Domain
import kr.co.aerix.plugins.DatabaseInitializer.query
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or

class GatewayService {
    suspend fun new(body: Gateway_Domain) = query {
        if (searchProjectID(body.projectID)) {
            return@query HttpStatusCode.Forbidden.description("E01")
        } else {
            if (Gateway.find { GatewayScheme.ip eq (body.ip) }.empty()) {
                Gateway.new {
                    this.ip = body.ip
                    this.mqtt_ip = body.mqtt
                    this.mqtt_port = body.port
                    this.gateway_mode = body.mode
                    this.virtual_ip = body.virtualIP
                    this.state = body.state
                    this.project_id = body.projectID
                }
            } else {
                return@query HttpStatusCode.Forbidden.description("G01")
            }
            return@query HttpStatusCode.OK
        }
    }

    suspend fun delete(id: Int) = query {
        Gateway.findById(id)!!.delete()
        return@query HttpStatusCode.OK
    }

    suspend fun update(body: Gateway_Domain) = query {
        if (searchProjectID(body.projectID)) {
            return@query HttpStatusCode.Forbidden.description("E01")
        } else {
            val patcher =
                Gateway.findById(body.id as Int)
                    ?: return@query HttpStatusCode.Forbidden.description("E01");
            if (Gateway.find { GatewayScheme.ip eq (body.ip) }.empty()) else {
                if (!patcher.ip.equals(body.ip)) {
                    return@query HttpStatusCode.Forbidden.description("G01")
                }
            }
            patcher.apply {
                this.ip = body.ip
                this.mqtt_ip = body.mqtt
                this.mqtt_port = body.port
                this.gateway_mode = body.mode
                this.virtual_ip = body.virtualIP
                this.state = body.state
                this.project_id = body.projectID
            }
            return@query HttpStatusCode.OK.description("OK")
        }
    }

    private fun searchProjectID(project_id: Int): Boolean {
        return Project.findById(project_id) == null
    }

}