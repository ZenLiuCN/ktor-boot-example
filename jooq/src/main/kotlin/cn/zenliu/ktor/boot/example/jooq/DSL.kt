/*
 * Copyright (c) 2018.
 * written by Zen.Liu(http://github.com/ZenLiuCN/), supported by AS IS.
 */

package cn.zenliu.ktor.boot.example.jooq

import cn.zenliu.ktor.boot.annotations.context.Properties
import cn.zenliu.ktor.boot.context.PropertiesManager


import com.zaxxer.hikari.*
import io.ktor.util.KtorExperimentalAPI
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import java.sql.DriverManager
import javax.sql.DataSource

object Dsl {
    private val log by lazy { LoggerFactory.getLogger(this::class.java) }
    val settings: Settings = Settings().apply {
        isExecuteLogging = false
    }
    @KtorExperimentalAPI
    val ds: HikariDataSource by lazy {
        HikariDataSource(HikariConfig().apply {
            val conf = PropertiesManager.getProperties(Hikari::class) as Hikari
            jdbcUrl = conf.url
            username = conf.username
            password = conf.password
            maximumPoolSize = conf.poolSize
            addDataSourceProperty(
                "cachePrepStmts",
                conf.cachePrepStmts
            )
            addDataSourceProperty(
                "prepStmtCacheSize",
                conf.prepStmtCacheSize
            )
            addDataSourceProperty(
                "prepStmtCacheSqlLimit",
                conf.prepStmtCacheSqlLimit
            )
        })
    }
    @KtorExperimentalAPI
    val ctx by lazy { createDSL(ds, SQLDialect.H2) }

    @JvmStatic
    fun createDSL(url: String, user: String, password: String, driver: String, dialect: SQLDialect) = kotlin.run {
        Class.forName(driver)
        DSL.using(DriverManager.getConnection(url, user, password), dialect, settings)
    }!!

    @JvmStatic
    fun createDSL(ds: DataSource, dialect: SQLDialect) = DSL.using(ds, dialect, settings)!!

}

@Properties(path = "hikari")
data class Hikari(
    val url: String,
    val username: String,
    val password: String,
    val poolSize: Int = 10,
    val cachePrepStmts: Boolean = true,
    val prepStmtCacheSize: Int = 250,
    val prepStmtCacheSqlLimit: Int = 2048

)
