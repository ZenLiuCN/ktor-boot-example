/*
 * Copyright (c) 2018.
 * written by Zen.Liu(http://github.com/ZenLiuCN/), supported by AS IS.
 */

/**
 * @Author: Zen.Liu
 * @Date: 2018-12-22 10:57
 * @Project: ktor-boot
 */
package cn.zenliu.ktor.boot.example

import cn.zenliu.ktor.boot.annotations.context.*
import cn.zenliu.ktor.boot.annotations.context.ApplicationConfiguration
import cn.zenliu.ktor.boot.annotations.request.*
import cn.zenliu.ktor.boot.annotations.routing.*
import cn.zenliu.ktor.boot.context.*
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.http.content.*
import io.ktor.routing.Routing
import io.ktor.util.*
import org.slf4j.LoggerFactory

//main function to start ktor-boot
@KtorExperimentalAPI
fun main(args: Array<String>) {
    Start<App>(args)
}
// App class use to define DI should scan which pacakge
class App(
    val conf: Conf // DI element
){
    val log=LoggerFactory.getLogger(App::class.java)

    init {
        log.trace("properties test of $conf")
    }
    //annotation for function to do application pre configuration
    @ApplicationConfiguration
    fun configuartion(app:Application){
        app.install(CallLogging)
        //...do any  like install or other configurate
    }
}
//annotation to mark this class is serve request
@Controller
class Ctrl{
    // define new AttributeKey
    val attr=AttributeKey<String>("test")
    //Annotation intercept function for all route
    @Interceptor
    fun intercepthor(ctx:InterceptorContext){
        log.info("interceptor called")
        ctx.context.attributes.put(attr,"a")
    }
    //Annotation to define a raw route configurate function
    @RawRoute
    fun raw(ctx:Routing){
        ctx{
          static ("/"){
              resources("static")
              defaultResource("index.html")
          }
        }
    }
    //anntotation to map a function handler [GET]/info
    @RequestMapping("/info")
    fun info()="1"
    //anntotation to map a function handler [GET]/info1
    @RequestMapping("/info1")
    fun info2(
        attributes: Attributes // need DI Attributes as parameter
    )=attributes[attr]
    //annotation with path variable
    @RequestMapping("/info2/a/{id}",method = RequestMapping.METHOD.POST)
    fun info2a(
        @PathVariable id:String, //mark PathVariable **id** should be String
               @RequestBody body:List<String>?, //mark  RequestBody should be null or list
        @QueryParameter a:Int?)//mark optional query parameter a should be int
            =id+body+a.toString()

    @RequestMapping("/info2/",method = RequestMapping.METHOD.POST)
    fun info2(
        @RequestBody body:RequestBodyType?, //mark  RequestBody should be null or RequestBodyType
        @QueryParameter a:Int?)//mark optional query parameter a should be int
            ="$body $a"
    companion object {
        private val log=LoggerFactory.getLogger(Ctrl::class.java)
    }
}
@Properties("app.configuration")
data class Conf(val conf:Boolean=true)

data class RequestBodyType(val name:String)
