package kr.co.aerix.entity

import kr.co.aerix.model.Project_Req
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime


object ProjectScheme : IntIdTable("project") {
    val name = text("name")
    val mode = bool("mode").index().default(false);
    val createdAt = datetime("created_at").index().default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

class Project(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Project>(ProjectScheme)
    var name by ProjectScheme.name
    var mode by ProjectScheme.mode
    var createdAt by ProjectScheme.createdAt
    var updatedAt by ProjectScheme.updatedAt
}