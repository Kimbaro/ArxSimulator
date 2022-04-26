package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.Gateway_Domain
import kr.co.aerix.service.MqttService
import kr.co.aerix.service.ProjectService

fun Routing.mqtt(service_project: ProjectService) {
    //등록된 모든 프로젝트-스캐너-센서정보를 전달함
    route("/mqtt") {
        get("/packet") {
            call.respond(service_project.getAllSchemeData());
        }

        //Mqtt module로 전송하기전 내부 테스트용
        post {
            val body = call.receive<Any>()
            println("############## ${body}");
            call.respond(body);
        }
    }
}