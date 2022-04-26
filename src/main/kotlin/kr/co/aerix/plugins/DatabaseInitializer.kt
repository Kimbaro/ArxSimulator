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
    fun init(insert: String, create: String, hikariConfig: HikariConfig) {
        db = Database.connect(HikariDataSource(hikariConfig))
        transaction(db) {
            /**매 빌드마다 수행합니다.*/
            //스키마생성
            if (create.toBoolean()) create(ProjectScheme, ScannerScheme, SensorScheme, GatewayScheme)
            //데이터적재
            if (insert.toBoolean()) {
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
    }

    fun hikariConfig(
        DRIVER_CLASS_NAME: String,
        JDBC_URL: String,
        MAXIMUM_POOL_SIZE: String,
        IS_AUTO_COMMIT: String,
        USERNAME: String,
        PASSWORD: String,
        TRANSACTION_ISOLATION: String
    ) = HikariConfig().apply {
        driverClassName = DRIVER_CLASS_NAME
        jdbcUrl = JDBC_URL
        maximumPoolSize = MAXIMUM_POOL_SIZE.toInt()
        isAutoCommit = IS_AUTO_COMMIT.toBoolean()
        username = USERNAME
        password = PASSWORD
        transactionIsolation = TRANSACTION_ISOLATION
        validate()
    }

    suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction(DatabaseInitializer.db) {
            block()
        }
    }
}