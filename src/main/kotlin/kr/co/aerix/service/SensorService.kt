package kr.co.aerix.service

import io.ktor.http.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.ScannerReq_Domain
import kr.co.aerix.model.SensorReq_Domain
import kr.co.aerix.model.Sensor_domain
import kr.co.aerix.plugins.DatabaseInitializer.query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll

class SensorService {

    suspend fun deletePK(id: Int) = query {
        SensorScheme.deleteWhere { SensorScheme.id eq (id) }
    }


    suspend fun update(body: SensorReq_Domain) = query {
        if (searchProjectID(body.projectID)) {
            return@query HttpStatusCode.Forbidden.description("E01")
        } else {
            if (searchScannerID(body.scannerID)) {
                return@query HttpStatusCode.Forbidden.description("E01")
            } else {
                val patcher =
                    Sensor.findById(body.id) ?: return@query HttpStatusCode.Forbidden.description("E01");
                if (Sensor.find { SensorScheme.mac eq (body.mac) }.empty()) else {
                    if (!patcher.mac.equals(body.mac)) {
                        return@query HttpStatusCode.Forbidden.description("S01")
                    }
                }
                patcher.apply {
                    this.project_id = body.projectID
                    this.scanner_id = body.scannerID
                    this.mac = body.mac
                    this.provider = body.provider
                    this.model = body.model
                    this.variable = body.variable
                    this.value = body.value
                    this.min = body.min
                    this.max = body.max
                }
                return@query HttpStatusCode.OK
            }
        }
    }

    private fun searchProjectID(project_id: Int): Boolean {
        return Project.findById(project_id) == null
    }

    private fun searchScannerID(scanner_id: Int): Boolean {
        return Scanner.findById(scanner_id) == null
    }

    suspend fun new(body: SensorReq_Domain) = query {

        if (body.scannerID == null) {
            return@query HttpStatusCode.Forbidden.description("E02")
        }
        if (searchScannerID(body.scannerID)) {
            return@query HttpStatusCode.Forbidden.description("E01")
        } else {
            if(Sensor.find(SensorScheme.mac eq(body.mac)).empty()){
                Sensor.new {
                    this.project_id = body.projectID
                    this.scanner_id = body.scannerID
                    this.mac = body.mac
                    this.provider = body.provider
                    this.model = body.model
                    this.variable = body.variable
                    this.value = body.value
                    this.min = body.min
                    this.max = body.max
                }
            }else{
                return@query HttpStatusCode.Forbidden.description("S01")
            }
            return@query HttpStatusCode.OK
        }
    }

    suspend fun getAll(): List<Sensor_domain> = query {
        SensorScheme.selectAll().map {
            selectAlltoSensor(it)
        }
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

    private fun selectAlltoSensor(row: ResultRow): Sensor_domain {
        println(row.toString());
        return Sensor_domain(
            id = row[SensorScheme.id].value,
            mac = row[SensorScheme.mac],
            provider = row[SensorScheme.provider],
            model = row[SensorScheme.model],
            variable = row[SensorScheme.variable],
            value = row[SensorScheme.value],
            min = row[SensorScheme.min],
            max = row[SensorScheme.max],
            curValue = row[SensorScheme.curValue],
            projectID = row[SensorScheme.project_id],
            scannerID = row[SensorScheme.scanner_id],
        )
    }
}