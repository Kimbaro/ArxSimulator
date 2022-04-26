package kr.co.aerix.service

import io.ktor.http.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.*
import kr.co.aerix.plugins.DatabaseInitializer.query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll

class DeviceService {

    suspend fun deleteUK(id: Int) = query {
        SensorScheme.deleteWhere { SensorScheme.scanner_id eq (id) }
    }

    suspend fun deletePK(id: Int) = query {
        ScannerScheme.deleteWhere { ScannerScheme.id eq (id) }
    }

    suspend fun update(body: ScannerReq_Domain) = query {
        if (searchProjectID(body.projectID)) {
            return@query HttpStatusCode.Forbidden.description("E01")
        } else {
            val patcher =
                Scanner.findById(body.id) ?: return@query HttpStatusCode.Forbidden.description("E01");
            if (Scanner.find { ScannerScheme.ip eq (body.ip) }.empty()) else {
                if (!patcher.ip.equals(body.ip)) {
                    return@query HttpStatusCode.Forbidden.description("G01")
                }
            }
            patcher.apply {
                this.name = body.name
                this.ip = body.ip
                this.mqtt_ip = body.mqtt
                this.mqtt_port = body.port
                this.mqtt_topic = body.topic
                this.mode = body.mode
                this.enable = body.enable
                this.parsing = body.parsing
                this.scanDuration = body.scanDuration
            }
            return@query HttpStatusCode.OK
        }
    }


    suspend fun new(body: ScannerReq_Domain) = query {
        if (searchProjectID(body.projectID)) {
            return@query HttpStatusCode.Forbidden.description("E01")
        } else {

            if (Scanner.find { ScannerScheme.ip eq (body.ip) }.empty()) {
                Scanner.new {
                    this.project_id = body.projectID
                    this.ip = body.ip
                    this.mqtt_ip = body.mqtt
                    this.mqtt_port = body.port
                    this.mqtt_topic = body.topic
                    this.name = body.name
                    this.mode = body.mode
                    this.enable = body.enable
                    this.parsing = body.parsing
                    this.scanDuration = body.scanDuration
                }
            } else {
                return@query HttpStatusCode.Forbidden.description("SC01")
            }
            return@query HttpStatusCode.OK
        }
    }

    private fun searchProjectID(project_id: Int): Boolean {
        return Project.findById(project_id) == null
    }

    suspend fun getScannerAllByProjectID(project_id: Int) = query {
        Scanner.find { ScannerScheme.project_id.eq(project_id) }.map {
            toScanner(project_id, it)
        }
    }

    suspend fun getScannerAll(): List<ScannerRes_Domain> = query {
        ScannerScheme.selectAll().map {
            toScanner(it)
        }
    }

    suspend fun getGatewayAllByProjectID(project_id: Int) = query {
        Gateway.find { GatewayScheme.project_id.eq(project_id) }.map {
            toGateway(project_id, it)
        }
    }

//    BS5555

    private fun toGateway(project_id: Int, row: Gateway): Gateway_Domain {
        return Gateway_Domain(
            id = row.id.value,
            projectID = project_id,
            ip = row.ip,
            mqtt = row.mqtt_ip,
            port = row.mqtt_port,
            mode = row.gateway_mode,
            virtualIP = row.virtual_ip,
            state = row.state
        )
    }

    private fun toScanner(project_id: Int, row: Scanner): ScannerRes_Domain {
        val list: ArrayList<Sensor_domain> = ArrayList<Sensor_domain>();
        Sensor.find { SensorScheme.project_id eq (project_id) and (SensorScheme.scanner_id eq row.id.value) }.forEach {
            list.add(toSensor(it))
        }
        return ScannerRes_Domain(
            id = row.id.value,
            ip = row.ip,
            mqtt = row.mqtt_ip,
            port = row.mqtt_port,
            topic = row.mqtt_topic,
            name = row.name,
            mode = row.mode,
            enable = row.enable,
            parsing = row.parsing,
            scanDuration = row.scanDuration,
            sensors = list,
            projectID = row.project_id
        )
    }

    private fun toScanner(row: ResultRow): ScannerRes_Domain {
        val list: ArrayList<Sensor_domain> = ArrayList<Sensor_domain>();
        Sensor.find { SensorScheme.scanner_id eq row[ScannerScheme.id].value }.forEach {
            list.add(toSensor(it))
        }
        return ScannerRes_Domain(
            id = row[ScannerScheme.id].value,
            ip = row[ScannerScheme.ip],
            mqtt = row[ScannerScheme.mqtt_ip],
            port = row[ScannerScheme.mqtt_port],
            topic = row[ScannerScheme.mqtt_topic],
            name = row[ScannerScheme.name],
            mode = row[ScannerScheme.mode],
            enable = row[ScannerScheme.enable],
            parsing = row[ScannerScheme.parsing],
            scanDuration = row[ScannerScheme.scanDuration],
            sensors = list,
            projectID = row[ScannerScheme.project_id]
        )
    }

    private fun toSensor(row: Sensor): Sensor_domain {
        return Sensor_domain(
            id = row.id.value,
            mac = row.mac,
            provider = row.provider,
            model = row.model,
            variable = row.variable,
            value = row.value,
            min = row.min,
            max = row.max,
            curValue = row.curValue,
            projectID = row.project_id,
            scannerID = row.scanner_id,
        )
    }
}