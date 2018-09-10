# nickname-manager #

## Build & Run ##

```sh
$ cd nickname-manager
$ sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.


搭建scala http server文档
环境：
Scala SDK ：2.12.6
Sbt       ：
Scalatra  ： 2.6.3

## 1.	使用Scalatra 的框架实现
$ sbt new scalatra/scalatra.g8

## 2.	使用IDEA打开项目
删掉不需要的文件夹

## 3.	写测试类
### 1）	准备
Scalatra的测试风格有8种，这里选择其中一种。测试之前先编译运行一次，否则汇报 empty test suit。
### 2）	测试数据
因为用到了json数据因此要添加json 相关的依赖

```
    //  json 注意末尾的逗号
    "org.scalatra" %% "scalatra-json" % ScalatraVersion,
    "org.json4s" %% "json4s-jackson" % "3.2.11"
```


测试数据
```scala
package com.nickname.app

import com.nickname.app.dto.Person
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write

trait TestJson {

  implicit val format = DefaultFormats

  val person = new Person(10,
    "hyf",
    " spiderman",
    "he can do everything he wants",
    Set("fly", "kill", "climb"))

  val jObject = write(person)
}
```


### 3）	测试类

```scala
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest._

@RunWith(classOf[JUnitRunner])
class NicknameControllerTests extends ScalatraSpec with TestJson {

  addServlet(classOf[NicknameController], "/*")


  describe("NicknameControllerTests") {

    it("GET /person/list  obtain all persons profile") {
      get("/person/list") {
        status should equal(200)
      }
    }

    it("POST /insert  insert one person profile") {
      post("/insert", jObject) {
        status should equal(200)
      }
    }

    it("PUT /update/:id  update one person profile") {
      put("update/10", ("description", "he loves his home")) {
        status should equal(200)
      }
    }

    it("delete /delete/:id  delete some body profile") {
      delete("/delete/10") {
        status should equal(200)
      }
    }

  }

}
```

### 4）	运行
选中该类名，鼠标右键：run

### 5）	结果分析
这里只是能看到请求正常发出。数据逻辑处理是否正确还需要进一步处理。

## 4.	链接数据库（mongo）
### 1）需要安装mongoDB（见上一份文档）
### 2）在build.sbt中添加依赖

```scala
  //mongo
  "org.mongodb" %% "casbah" % "3.1.1",
  "org.json4s" %% "json4s-mongo" % "3.5.4"
```

### 3）设置数据库链接

```scala
import com.mongodb.casbah.MongoClient
import com.nickname.app._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    //default: host-> 127.0.0.1, port->27017
    //database:scalaWeb
    //collection:person
    val mongoClient = MongoClient()
    val mongoColl = mongoClient("scalaWeb")("person")
    context.mount(new NicknameController(mongoColl), "/*")
  }
}
```


5.	具体逻辑实现

```scala

package com.nickname.app

import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.{Imports, MongoDBObject}
import com.nickname.app.dto.Person
import com.nickname.app.helper.JsonFormat
import org.scalatra._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization.read
import org.json4s.jackson.Serialization.write


class NicknameController(mongoColl: MongoCollection) extends ScalatraServlet with JsonFormat {

  get("/person/list") {
    print("/person/list")
    mongoColl.find()
    val temp = for (x <- mongoColl) yield read[Person](x.toString)
    print(write(temp))
    write(temp)
  }

  post("/insert") {
    print("/insert")
    try {
      val newObj: Imports.DBObject = parseRequest2Person
      print(newObj)
      mongoColl += newObj
    } catch {
      case e: Exception => response.sendError(202, "data format is error")
    }
    response.setStatus(201)
  }

  private def parseRequest2Person = {
    val obj = read[Person](request.body)
    val newObj = MongoDBObject("id" -> obj.id, "name" -> obj.name,
      "nickName" -> obj.nickName, "description" -> obj.description,
      "skills" -> obj.skills)
    newObj
  }

  put("/update/:id") {
    print("/update/:id")
    val query = MongoDBObject("id" -> params("id").toInt)
    val update = parseRequest2Person
    val result = mongoColl.update(query, update)
    val resultJson = "result" -> result.getN
    print(resultJson)
    JsonMethods.compact(JsonMethods.render(resultJson))
  }

  delete("/delete/:id") {
    println("/delete/:id")
    val query = MongoDBObject("id" -> params("id").toInt)
    val result = mongoColl.remove(query)
    val resultJson = "result" -> result.getN
    print(resultJson)
    JsonMethods.compact(JsonMethods.render(resultJson))
  }
}

```

>注意：param(“id”)获取的值为String类型，在构造查询语句时，要注意转为Int型，否则将无法查询到符合条件的数据。所有的条件都是一个MongoDBObject。
对应的有find(),update(),insert(),remove().
参考：https://www.cnblogs.com/PerkinsZhu/p/6917104.html
mongoDB文档：https://docs.mongodb.com/manual/tutorial/query-documents/
mongoDB-casbah: http://mongodb.github.io/casbah/3.1/reference/connecting/

## 6.	打包发布为独立的jar文件
### 1）	添加依赖，后面添加“container”，表示把它作为容器
```scala
"org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container;compile"

```
###2）	添加插件，用该命令实现打包（同步下sbt的依赖和插件）
```
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.7")

```
### 3）	添加启动类

```scala
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyLauncher {
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
context.setResourceBase("src/main/webapp")
//启动类
    context.setInitParameter(ScalatraListener.LifeCycleKey, "ScalatraBootstrap")
    context.addEventListener(new ScalatraListener)

    server.setHandler(context)

    server.start
    server.join
  }
}
```


### 4）	打包命令
```sbtshell
$ assembly
```
 
### 5）	运行
```
$ java -jar nickname-manager-assembly-0.1.0-SNAPSHOT.jar

```

