/*
 * Copyright (c) 2018.
 * written by Zen.Liu(http://github.com/ZenLiuCN/), supported by AS IS.
 */

package cn.zenliu.ktor.boot.example.jooq

import cn.zenliu.ktor.boot.annotations.context.Controller
import cn.zenliu.ktor.boot.annotations.routing.RequestMapping
import cn.zenliu.ktor.boot.context.*
import com.codahale.metrics.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.metrics.Metrics
import io.ktor.util.KtorExperimentalAPI
import java.time.Duration
import java.util.concurrent.TimeUnit

@KtorExperimentalAPI
fun main(args: Array<String>) {
    Start<Application>(args)
}

@cn.zenliu.ktor.boot.annotations.context.ApplicationConfiguration
class Application : ApplicationConfiguration {
    override fun applicationConfiguration(app: io.ktor.application.Application) {
        app.apply {
            install(CORS) {
                method(HttpMethod.Options)
                method(HttpMethod.Get)
                header(HttpHeaders.XForwardedProto)
                anyHost()
                allowCredentials = true
                maxAge = Duration.ofDays(1)
            }
            install(AutoHeadResponse)
            install(CallLogging)
            install(Compression) {
                gzip {
                    priority = 1.0
                    minimumSize(1024)
                }
                deflate {
                    priority = 10.0
                    minimumSize(1024) // condition
                }
            }
            install(Metrics) {
                JmxReporter.forRegistry(registry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build()
                    .start()
              /*  Slf4jReporter.forRegistry(registry)
                    .outputTo(log)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build()
                    .start(10, TimeUnit.SECONDS)*/
            }
        }
    }

}

@Controller
@RequestMapping("/h2")
class Controller {
    @RequestMapping("create")
    fun l()= listOf("1","2","3")
}
