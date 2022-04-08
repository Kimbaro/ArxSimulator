package kr.co.aerix.service

import io.ktor.features.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kr.co.aerix.entity.*
import kr.co.aerix.model.*
import kr.co.aerix.plugins.DatabaseInitializer.query
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ProjectService {

    suspend fun deleteUK(id: Int): HttpStatusCode = query {
        SensorScheme.deleteWhere { SensorScheme.project_id eq (id) };
        ScannerScheme.deleteWhere { ScannerScheme.project_id eq (id) };
        GatewayScheme.deleteWhere { GatewayScheme.project_id eq (id) }
        return@query HttpStatusCode.OK
    }

    suspend fun deletePK(id: Int): HttpStatusCode = query {
        Project.findById(id)!!.delete()
        //ProjectScheme.deleteWhere { ProjectScheme.id eq (id) }
        return@query HttpStatusCode.OK
    }

    suspend fun updateSensor(sensors: MutableList<Sensor_Mqtt_Res_Domain>) = query {
        sensors.forEach {
            Sensor.findById(it.id).apply {
                if (it.variable) {
                    this!!.curValue = (it.min..it.max).random()
                } else {
                    this!!.curValue = this.value
                }
            }
        }
    }

    suspend fun update_mode(body: Project_Patch_Req): HttpStatusCode = query {
        val patcher =
            Project.findById(body.id) ?: return@query HttpStatusCode.Forbidden.description("E1");
        patcher.apply {
            this.mode = body.mode
        }
        return@query HttpStatusCode.OK
    }

    suspend fun update(body: Project_Req): HttpStatusCode = query {
        val patcher =
            Project.findById(body.id) ?: return@query HttpStatusCode.Forbidden.description("E01");
        patcher.apply {
            this.name = body.name
        }
        return@query HttpStatusCode.OK
    }

    suspend fun getAllByProjectAndSensorData(): List<Project_RealtimeUpdate_Res_domain> = query {
        ProjectScheme.select { ProjectScheme.mode eq (true) }.map {
            toProjectByRealtimeUpdateModule(it)
        }
    }

    suspend fun getAllSchemeData(): List<Project_Mqtt_Res_domain> = query {
        ProjectScheme.selectAll().map {
            toProjectByMqttModule(it)
        }
    }

    private fun toProjectByRealtimeUpdateModule(row: ResultRow): Project_RealtimeUpdate_Res_domain {
        val datas: MutableList<Sensor_Mqtt_Res_Domain> = mutableListOf()
        Sensor.find { SensorScheme.project_id eq (row[ProjectScheme.id].value) }
            .map {
                datas.add(toSensor(it))
            }
        return Project_RealtimeUpdate_Res_domain(
            id = row[ProjectScheme.id].value,
            mode = row[ProjectScheme.mode],
            sensors = datas,
        )
    }

    private fun toProjectByMqttModule(row: ResultRow): Project_Mqtt_Res_domain {
        val scanners: MutableList<Scanner_Mqtt_Res_Domain> = mutableListOf()
        val gateways: MutableList<Gateway_Domain> = mutableListOf()
        ScannerScheme.select { ScannerScheme.project_id eq (row[ProjectScheme.id].value) }.map {
            scanners.add(toScanner(it))
        }
        GatewayScheme.select { GatewayScheme.project_id eq (row[ProjectScheme.id].value) }.map {
            gateways.add(toGateway(it))
        }
        return Project_Mqtt_Res_domain(
            id = row[ProjectScheme.id].value,
            mode = row[ProjectScheme.mode],
            scanners = scanners,
            gateways = gateways
        )
    }

    private fun toGateway(row: ResultRow): Gateway_Domain {
        return Gateway_Domain(
            id = row[GatewayScheme.id].value,
            projectID = row[GatewayScheme.project_id],
            ip = row[GatewayScheme.ip],
            mqtt = row[GatewayScheme.mqtt_ip],
            port = row[GatewayScheme.mqtt_port],
            mode = row[GatewayScheme.gateway_mode],
            virtualIP = row[GatewayScheme.virtual_ip],
            state = row[GatewayScheme.state]
        )
    }

    private fun toScanner(row: ResultRow): Scanner_Mqtt_Res_Domain {
        val list: ArrayList<Sensor_Mqtt_Res_Domain> = ArrayList<Sensor_Mqtt_Res_Domain>();
        Sensor.find { SensorScheme.scanner_id eq row[ScannerScheme.id].value }.forEach {
            list.add(toSensor(it))
        }
        return Scanner_Mqtt_Res_Domain(
            id = row[ScannerScheme.id].value,
            ip = row[ScannerScheme.ip],
            mqtt_ip = row[ScannerScheme.mqtt_ip],
            mqtt_port = row[ScannerScheme.mqtt_port],
            mqtt_topic = row[ScannerScheme.mqtt_topic],
            mode = row[ScannerScheme.mode],
            enable = row[ScannerScheme.enable],
            parsing = row[ScannerScheme.parsing],
            scanDuration = row[ScannerScheme.scanDuration],
            sensors = list,
        )
    }

    private fun toSensor(row: Sensor): Sensor_Mqtt_Res_Domain {
        return Sensor_Mqtt_Res_Domain(
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


    suspend fun getAll(): List<Project_domain> = query {
        ProjectScheme.selectAll().map {
            toProject(it)
        }
    }

    suspend fun new(name: String): HttpStatusCode = query {
        val uk_check = Project.find { ProjectScheme.name eq (name) }.empty()
        if (uk_check) {
            Project.new {
                this.name = name
            }
            return@query HttpStatusCode.OK
        } else {
            return@query HttpStatusCode.Forbidden.description("P01")
        }
    }

    suspend fun getFindById(id: Int) = query {
        Project.findById(id)
    }

    private fun toProject(row: ResultRow): Project_domain {
        println(row.toString());
        return Project_domain(
            id = row[ProjectScheme.id].value,
            name = row[ProjectScheme.name]
        )
    }
}