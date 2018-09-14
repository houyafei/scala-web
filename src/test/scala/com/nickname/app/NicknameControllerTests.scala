package com.nickname.app

import com.mongodb.casbah.MongoClient
import org.json4s.jackson.Serialization.write
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest._

@RunWith(classOf[JUnitRunner])
class NicknameControllerTests extends ScalatraSpec with TestJson {

  //default: host-> 127.0.0.1, port->27017
  val mongoClient = MongoClient()
  //database:scalaWeb
  //collection:person
  val mongoColl = mongoClient("scalaWeb")("person")

  addServlet(new NicknameController(mongoColl), "/*")


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
      put("update/1", jObject2) {
        status should equal(200)
      }
    }

    it("delete /delete/:id  delete some body profile") {
      delete("/delete/1") {
        status should equal(200)
      }
    }

  }

}
