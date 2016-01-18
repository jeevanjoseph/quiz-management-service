package com.danielasfregola.quiz.management.services

import com.danielasfregola.quiz.management.entities.{Question, QuestionUpdate}

import scala.concurrent.{ExecutionContext, Future}

class QuestionService(implicit val executionContext: ExecutionContext) {

  var questions = Vector(Question("1","First Question","Is this a default question ?"),
                                    Question("2","Second Question","Is the palnet earth round ?"),
                                    Question("3","Third Question","Who is John Galt ?"),
                                    Question("4","Fourth Question","Will the fat lady sing ?"),
                                    Question("5","Fifth Question","How far is the universe ?"),
                                    Question("6","Sixth Question","Is there a multiverse ?"),
                                    Question("7","Seventh Question","Whats inside a black hole ?"),
                                    Question("8","Eighth Question","Is the cat dead, alive or both  ?"),
                                    Question("9","Ninth Question","Can there be peace ?")
                                  )

  def createQuestion(question: Question): Future[Option[String]] = Future {
    questions.find(_.id == question.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        questions = questions :+ question
        Some(question.id)
    }
  }

  def getAllQuestions(offset:Int =0, limit:Int =5 ) : Future[Vector[Question]] = Future {
    questions.slice(offset,offset+limit)
  }

  def getQuestion(id: String): Future[Option[Question]] = Future {
    questions.find(_.id == id)
  }

  def updateQuestion(id: String, update: QuestionUpdate): Future[Option[Question]] = {

    def updateEntity(question: Question): Question = {
      val title = update.title.getOrElse(question.title)
      val text = update.text.getOrElse(question.text)
      Question(id, title, text)
    }

    getQuestion(id).flatMap { maybeQuestion =>
      maybeQuestion match {
        case None => Future { None } // No question found, nothing to update
        case Some(question) =>
          val updatedQuestion = updateEntity(question)
          deleteQuestion(id).flatMap { _ =>
            createQuestion(updatedQuestion).map(_ => Some(updatedQuestion))
          }
      }
    }
  }

  def deleteQuestion(id: String): Future[Unit] = Future {
    questions = questions.filterNot(_.id == id)
  }


}
