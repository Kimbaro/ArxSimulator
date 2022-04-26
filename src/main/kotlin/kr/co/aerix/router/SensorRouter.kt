package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.Project_Req
import kr.co.aerix.model.ScannerReq_Domain
import kr.co.aerix.model.SensorReq_Domain
import kr.co.aerix.service.SensorService

fun Routing.sensor(service: SensorService) {
    route("/sensor") {
        get {
            call.respond(service.getAll()).apply {
                HttpStatusCode.OK
            }
        }

        post {
            val body = call.receive<SensorReq_Domain>()
            call.respond(service.new(body));
        }

        patch {
            val body = call.receive<SensorReq_Domain>()
            call.respond(service.update(body))
        }

        delete("/{id}") {
            try{
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");
                call.respond(service.deletePK(id))

            }catch (e:BadRequestException){
                call.respond(HttpStatusCode.Forbidden.description("E02"))
            }
        }
    }
}