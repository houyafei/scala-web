package com.nickname.app

import com.github.fakemongo.Fongo
import com.mongodb.casbah.MongoDB
import com.mongodb.casbah.commons.MongoDBObject
import com.nickname.app.dto.Person
import org.json4s.jackson.Serialization.read
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest._

@RunWith(classOf[JUnitRunner])
class NicknameControllerTests extends ScalatraSpec with TestJson {

  //default: host-> 127.0.0.1, port->27017
  //  val mongoClient = MongoClient()
  //database:scalaWeb
  //collection:person
  //  val mongoColl = mongoClient("scalaWeb")("person")
  //mock one memory db, this
  val Db = new MongoDB(new Fongo("mongo server 1").getDB("hr_in_memory"))
  val mongoColl = Db("person")
  //  val mongoColl = MockitoSugar.mock[MongoCollection]
  //  MockitoSugar.when(mongoColl.update(MongoDBObject("id"->1),MongoDBObject("id"->2))).getMock

  addServlet(new NicknameController(mongoColl), "/*")


  private def createData(): Int = {
    var id = 0;
    post("/insert", jObject2) {
      status should equal(201)
      id = response.getHeader("id").toInt
      println("id = " + id)
    }
    id
  }

  describe("NicknameControllerTests") {

    it("GET /person/list  obtain all persons profile") {
      get("/person/list") {
        status should equal(200)
      }
    }

    it("POST /insert  insert one person profile") {
      post("/insert", jObject) {
        status should equal(201)
        val id = response.getHeader("id").toInt
        //
        val result = read[Person](mongoColl.findOne(MongoDBObject("id" -> id)).get.toString)
        result.id should be(id)
      }
    }

    it("PUT /update/:id  update one person profile") {
      val id = createData()
      put(s"update/$id", jObject1) {
        status should equal(200)
        mongoColl.find(MongoDBObject("id" -> id)).size should be(0)
        response.getHeader("result").toInt should be(1)
      }
    }

    it("delete /delete/:id  delete some body profile") {
      val id = createData()
      delete(s"/delete/$id") {
        status should equal(200)
        mongoColl.find(MongoDBObject("id" -> id)).size should be(0)
        response.getHeader("result").toInt should be(1)
      }
    }


  }

}
