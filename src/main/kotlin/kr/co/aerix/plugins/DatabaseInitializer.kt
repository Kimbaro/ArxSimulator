package kr.co.aerix.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.co.aerix.entity.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseInitializer {
    var db: Database? = null;
    fun init() {
        db = Database.connect(HikariDataSource(hikariConfig()))
        transaction(db) {
            create(ProjectScheme, ScannerScheme, SensorScheme, GatewayScheme)
            ProjectScheme.insert {
                it[name] = "에어릭스2"
                it[mode] = false
            }
            ProjectScheme.insert {
                it[name] = "에어릭스"
                it[mode] = false
            }.apply {
                GatewayScheme.insert {
                    it[ip] = "192.168.0.1"
                    it[mqtt_ip] = "192.168.1.1"
                    it[mqtt_port] = 1883
                    it[gateway_mode] = true
                    it[virtual_ip] = "192.168.0.2"
                    it[state] = true
                    it[project_id] = 1
                }
                ScannerScheme.insert {
                    it[ip] = "192.168.0.1"
                    it[mqtt_ip] = "192.168.1.1"
                    it[mqtt_port] = "1883"
                    it[mqtt_topic] = "tharx/test"
                    it[name] = "에어릭스"
                    it[mode] = false
                    it[enable] = false
                    it[parsing] = false
                    it[scanDuration] = 5
                    it[project_id] = 1
                }.apply {
                    SensorScheme.insert {
                        it[mac] = "AA:BB:CC:DD:EE:F1"
                        it[provider] = "에어릭스"
                        it[model] = "T435"
                        it[variable] = false
                        it[value] = 2
                        it[min] = 0
                        it[max] = 10
                        it[curValue] = 0
                        it[project_id] = 1
                        it[scanner_id] = 1
                    }
                    SensorScheme.insert {
                        it[mac] = "AA:BB:CC:DD:EE:F2"
                        it[provider] = "에어릭스"
                        it[model] = "T435"
                        it[variable] = false
                        it[value] = 2
                        it[min] = 0
                        it[max] = 10
                        it[curValue] = 0
                        it[project_id] = 1
                        it[scanner_id] = 1
                    }
                }
                ScannerScheme.insert {
                    it[ip] = "192.168.0.2"
                    it[mqtt_ip] = "192.168.1.1"
                    it[mqtt_port] = "1883"
                    it[mqtt_topic] = "tharx/test"
                    it[name] = "에어릭스"
                    it[mode] = false
                    it[enable] = false
                    it[parsing] = false
                    it[scanDuration] = 5
                    it[project_id] = 1
                }.apply {
                    SensorScheme.insert {
                        it[mac] = "AA:BB:CC:DD:EE:F3"
                        it[provider] = "에어릭스"
                        it[model] = "T435"
                        it[variable] = false
                        it[value] = 2
                        it[min] = 0
                        it[max] = 10
                        it[curValue] = 0
                        it[project_id] = 1
                        it[scanner_id] = 2
                    }
                    SensorScheme.insert {
                        it[mac] = "AA:BB:CC:DD:EE:F4"
                        it[provider] = "에어릭스"
                        it[model] = "T435"
                        it[variable] = false
                        it[value] = 2
                        it[min] = 0
                        it[max] = 10
                        it[curValue] = 0
                        it[project_id] = 1
                        it[scanner_id] = 2
                    }
                }
            }
        }
    }

    private fun hikariConfig() = HikariConfig().apply {
        driverClassName = "org.h2.Driver"
        jdbcUrl = "jdbc:h2:mem:test"
        maximumPoolSize = 3
        isAutoCommit = false
        username = "aerix"
        password = "!aerix123"
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction(DatabaseInitializer.db) {
            block()
        }
    }
}