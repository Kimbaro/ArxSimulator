package kr.co.aerix.service

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.*
import kr.co.aerix.plugins.DatabaseInitializer
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.io.IOException

/**
 * Mqtt 모듈에 시뮬레이션의 프로젝트의 동작상태를 전달합니다.
 * mode = true 인 경우 Mqtt 모듈은 해당 프로젝트의 스캐너/센서정보를 패키지로 만들어 Broker에게 발행 인터벌을 수행합니다
 * mode = false 인 경우 Mqtt 모듈은 해당 프로젝트의 발행 인터벌을 중지합니다
 * */
class MqttService {
    suspend fun getAllSchemeData(): List<Project_Mqtt_Res_domain> = DatabaseInitializer.query {
        ProjectScheme.selectAll().map {
            toProjectByMqttModule(it)
        }
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

    suspend fun httpRequest(body: Project_Patch_Req, datas: MutableList<Project_Mqtt_Res_domain>) {
        //http://192.168.0.74:9002/mqtt?id={id}&mode=true or false
        //http://localhost:9001/test?id=1&mode=true
        val client = HttpClient(CIO)
        var response: HttpResponse? = null;
        try {
            val jsonData = Gson().toJsonTree(datas, object : TypeToken<MutableList<Project_Mqtt_Res_domain>>() {}.type)
            response = client.post("http://192.168.0.74:8080/mqtt") {
                this.body = TextContent(
                    text = jsonData.asJsonArray.toString(),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.OK
                )
            }
        } catch (e: IOException) {
            if (response != null) {
                println("${response!!.status} MQTT 서버와의 연결이 실패하였음.")
            }
        } finally {
            client.close()
        }
    }

}