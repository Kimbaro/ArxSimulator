package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kr.co.aerix.model.Gateway_Domain
import kr.co.aerix.model.Project_Req
import kr.co.aerix.model.ScannerReq_Domain
import kr.co.aerix.model.ScannerRes_Domain
import kr.co.aerix.service.ProjectService
import kr.co.aerix.service.DeviceService
import kr.co.aerix.service.GatewayService
import org.h2.jdbc.JdbcBatchUpdateException
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Routing.device(service: DeviceService, service_project: ProjectService, service_gateway: GatewayService) {
    route("/device") {
        get("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");
                val mode =
                    service_project.getFindById(id) ?: throw NotFoundException();
                val scanners = service.getScannerAllByProjectID(id)
                val gateways = service.getGatewayAllByProjectID(id)
                val result: Map<String, Any> = mapOf<String, Any>(
                    "id" to id,
                    "mode" to mode.mode,
                    "name" to mode.name,
                    "scanners" to scanners,
                    "gateways" to gateways
                )// <- 결과값 반환 용도로 타입은 내부 호출함수에 명시되어 있음
                call.respond(
                    result
                ).apply {
                    HttpStatusCode.OK
                }
            } catch (e: BadRequestException) {
                call.respond(HttpStatusCode.Forbidden.description("E02"))
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.Forbidden.description("E01"))
            }
        }

        post("/gateway") {
            val body = call.receive<Gateway_Domain>()
            call.respond(service_gateway.new(body))
        }

        patch("/gateway") {
            val body = call.receive<Gateway_Domain>()
            call.respond(service_gateway.update(body))
        }

        delete("/gateway/{id}") {
            try {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException("Parameter id is null");
                call.respond(service_gateway.delete(id))
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.Forbidden.description("E02"))
            }
        }

        post("/scanner") {
            val body = call.receive<ScannerReq_Domain>()
            call.respond(service.new(body));
        }
        get("/scanner") {
            call.respond(service.getScannerAll())
        }
        patch("/scanner") {
            val body = call.receive<ScannerReq_Domain>()
            call.respond(service.update(body))
        }
        delete("/scanner/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");
            coroutineScope {
                val task = launch(Dispatchers.IO) {
                    service.deleteUK(id)
                }
                task.join()
                launch(Dispatchers.IO) {
                    service.deletePK(id)
                }
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}