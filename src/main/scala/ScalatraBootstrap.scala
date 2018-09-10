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
