package com.danielasfregola.quiz.management.resources

import com.danielasfregola.quiz.management.entities.{QuestionUpdate, Question}
import com.danielasfregola.quiz.management.routing.MyHttpService
import com.danielasfregola.quiz.management.services.QuestionService
import spray.routing._

trait QuestionResource extends MyHttpService {

  val questionService: QuestionService

  def questionRoutes: Route = pathPrefix("questions") {
    pathEnd {
      post {
        entity(as[Question]) { question =>
          completeWithLocationHeader(
            resourceId = questionService.createQuestion(question),
            ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        } ~
        get{
          parameters('offset ? 0, 'limit ? 5 ) { (offset, limit) =>
            complete(questionService.getAllQuestions(offset,limit))
          }
        }
    } ~
    path(Segment) { id =>
      get {
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
