package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.Project_Req
import kr.co.aerix.model.Project_domain
import kr.co.aerix.service.ProjectService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kr.co.aerix.model.Project_Patch_Req
import kr.co.aerix.service.MqttService

fun Routing.project(service: ProjectService, serviceRequest: MqttService) {
    route("/test") {
        get {
            val id =
                call.request.queryParameters["id"]?.toInt();
            val mode = call.request.queryParameters["mode"].toBoolean();
            println("${id}      ${mode}");
            call.respond(HttpStatusCode.OK)
        }
    }

    route("/project") {
        get {
            call.respond(service.getAll()).apply {
                HttpStatusCode.OK
            };
        }

        post {
            val body = call.receive<Project_Req>()
            call.respond(service.new(body.name))
        }

        patch {
            val body = call.receive<Project_Req>()
            call.respond(service.update(body))
        }
        patch("/mode") {
            val body = call.receive<Project_Patch_Req>()
            var httpStatusCode: HttpStatusCode? = null;
            coroutineScope {
                var task = async(Dispatchers.IO) {
                    httpStatusCode = service.update_mode(body)
                    call.respond(HttpStatusCode.OK)
                    service.update_mode(body)
                }
                if (task.await().value == 200) {
                    launch(Dispatchers.IO) {
                        if (httpStatusCode == HttpStatusCode.OK) {
                            var datas = serviceRequest.getAllSchemeData().toMutableList()
                            if (!datas.isEmpty()) {
                                serviceRequest.httpRequest(body, datas)    // <- MQTT Module 에게 mode=true,false 정보를 전달
                            }
                        }
                    }
                }
            }
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");

            coroutineScope {
                launch(Dispatchers.IO) {
                    service.deleteUK(id)    // <- MQTT Module 에게 mode=true,false 정보를 전달
                }.join()
                launch(Dispatchers.IO) {
                    service.deletePK(id)    // <- MQTT Module 에게 mode=true,false 정보를 전달
                }
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}