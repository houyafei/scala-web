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
    println("/insert")
    try {
      val newObj = parseRequest2Person
      println(newObj)
      mongoColl += newObj
//      println(mongoColl.findOne(newObj).get)
      response.setIntHeader("id",read[Person](mongoColl.findOne(newObj).get.toString).id)
    } catch {
      case e: Exception => response.sendError(400, "data format is error")
    }
//    response.setIntHeader("id",read[Person](mongoColl.findOne(parseRequest2Person).toString).id)
    response.setStatus(201)
  }

  private def parseRequest2Person = {
    println(request.body)
    val obj = read[Person](request.body)
    val newObj = MongoDBObject("id" -> obj.id,
      "name" -> obj.name,
      "nickName" -> obj.nickName,
      "description" -> obj.description)
    newObj
  }

  put("/update/:id") {
    println("/update/:id")
    val query = MongoDBObject("id" -> params("id").toInt)
    val update = parseRequest2Person
    val result = mongoColl.update(query, update)
    val resultJson = "result" -> result.getN
    response.setIntHeader("result",result.getN)
    println(resultJson)
    JsonMethods.compact(JsonMethods.render(resultJson))
  }

  delete("/delete/:id") {
    println("/delete/:id")
    //  ---------ok------1-------
    //    val id= params("id").toInt
    //    println(id)
    //    val query = new BasicDBObject("id", new BasicDBObject("$eq", id))
    //    mongoColl.find(query).foreach(print)
    //    mongoColl.remove(query)
    //------------ok-----2-------
    //    val query = MongoDBObject("id" -> params("id").toInt)
    //    print(query)
    //    mongoColl.find(query).foreach(print)
    //    mongoColl.remove(query)


    val query = MongoDBObject("id" -> params("id").toInt)
    val result = mongoColl.remove(query)
    val resultJson = "result" -> result.getN
    response.setIntHeader("result",result.getN)
    println(resultJson)
    JsonMethods.compact(JsonMethods.render(resultJson))
  }
}
