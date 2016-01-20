package com.danielasfregola.quiz.management.resources

import com.danielasfregola.quiz.management.entities.{QuestionUpdate, Question}
import com.danielasfregola.quiz.management.routing.MyHttpService
import com.danielasfregola.quiz.management.services.QuestionService
import spray.routing._
import spray.http.MediaTypes

trait QuestionResource extends MyHttpService {

  val questionService: QuestionService
/**
 * Our own directive definition.
 * This directive, simply takes a route , filters in only GET requests, sets the response  mediatype as application/json
 * then return the route. The result is we have a directive that can be used to filter GET type requests and set the
 * response type as application/json in a concise way . Simply an example on how to abstract out common features
 * in to a directive
 */
  def getAsJSON(route: Route) : Route = {
    get{
      respondWithMediaType(MediaTypes.`application/json`){
        route
      }
    }

  }
/**
* A packaged route handler. This hadler can be used whereever there is a need to handle path patterns.
* If the filter directives match, The handler will consume the match and terminate the request cascade.s
*/
  lazy val description = pathSuffix("describe"){
    get{
      respondWithMediaType(MediaTypes.`application/json`){
        complete("{ Coming Soon ... }")
      }
    }
  }



  def questionRoutes: Route = pathPrefix("questions") {
    pathEnd {
      post {
        entity(as[Question]) { question =>
          completeWithLocationHeader(
            resourceId = questionService.createQuestion(question),
            ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        } ~
        getAsJSON{
          parameters('offset ? 0, 'limit ? 5 ) { (offset, limit) =>
            complete(questionService.getAllQuestions(offset,limit))
          }
        }
    } ~
    description ~
    path(Segment) { id =>
      getAsJSON {
        complete(questionService.getQuestion(id))
      } ~
      put {
        entity(as[QuestionUpdate]) { update =>
          complete(questionService.updateQuestion(id, update))
        }
      } ~
      delete {
        complete(204, questionService.deleteQuestion(id))
      }
    }
  }
}
