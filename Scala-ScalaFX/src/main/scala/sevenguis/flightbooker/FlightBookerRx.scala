package sevenguis.flightbooker

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ComboBox, TextField}
import scalafx.scene.layout.VBox
import scalafx.geometry.Insets
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import scalafx.Includes._
import javafx.beans.value.ObservableValue

import rx._

object FlightBookerRx extends JFXApp {
  val flightType = new ComboBox[String](Seq("one-way flight", "return flight"))
  val startDate = new TextField
  val returnDate = new TextField
  val book = new Button("Book")

  // The indented code is mechanical boilerplate code to create for each property 'p' a
  // corresponding "shadow" reactive variable 'r' that is bidirectionally connected with
  // 'p' ('r <=> p') if 'p' is an input node in the dataflow graph and otherwise, i.e. 'p'
  // is an inner node, the connection is unidirectional ('r => p').

  // If a toolkit was designed with such reactive variables most of the following boilerplate
  // code would be unnecessary, but in any case it could also be generated by a compiler.

  // Although this seems like a lot boilerplate, the boilerplate needed in pure JavaFX or ScalaFX
  // to achieve the same declarative effect would be much more and one would need to use new
  // operators/constructs for the binding declarations (see e.g. the use of 'when' in ScalaFX
  // and the use of the class 'When' in JavaFX instead of reusing the native and familiar 'if').
  // That's why I chose to use ScalaRx as this seems to be the first reactive library that makes
  // reactive declarations look really easy and native.

  val flightType_value = Var("one-way flight")
          val o0 = Obs(flightType_value) { flightType.value = flightType_value() }
          flightType.value.addListener((observable: ObservableValue[_ <: Object], oldValue: Object, newValue: Object) =>
            flightType_value() = newValue.toString)

  val returnDate_disable = Rx{ flightType_value() == "one-way flight" }
          val o1 = Obs(returnDate_disable) { returnDate.disable = returnDate_disable() }

  val startDate_text = Var(dateToString(LocalDate.now))
          val o2 = Obs(startDate_text) { startDate.text = startDate_text() }
          startDate.text.addListener((v: ObservableValue[_ <: String], o: String, n: String) =>
            startDate_text() = n)

  val returnDate_text = Var(dateToString(LocalDate.now))
          val o3 = Obs(returnDate_text) { returnDate.text = returnDate_text() }
          returnDate.text.addListener((v: ObservableValue[_ <: String], o: String, n: String) =>
            returnDate_text() = n)

  val startDate_style = Rx{ if (isDateString(startDate_text())) "" else "-fx-background-color: lightcoral" }
          val o4 = Obs(startDate_style) { startDate.style = startDate_style() }

  val returnDate_style = Rx{ if (isDateString(returnDate_text())) "" else "-fx-background-color: lightcoral" }
          val o5 = Obs(returnDate_style) { returnDate.style = returnDate_style() }

  val book_disable = Rx{
    flightType_value() match {
      case "one-way flight" => !isDateString(startDate_text())
      case "return flight" =>
        !isDateString(startDate_text()) || !isDateString(returnDate_text()) ||
        stringToDate(startDate_text()).compareTo(stringToDate(returnDate_text())) > 0
    }
  }
          val o6 = Obs(book_disable) { book.disable = book_disable() }

  // If we ignore the boilerplate and imagine JavaFX/ScalaFX was built with such a reactive approach
  // in mind -- which is completely feasible in principal as the code above shows -- then this
  // would be a real win in code clarity as the constraints are specified fully declaratively
  // and with all the familiar language constructs and operators (inside the Rx blocks).

  stage = new PrimaryStage {
    title = "FlightBooker"
    scene = new Scene {
      content = new VBox(10) {
        padding = Insets(10)
        content = Seq(flightType, startDate, returnDate, book)
      }
    }
  }

  def dateToString(date: LocalDate) = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
  def stringToDate(string: String) = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(string))
  def isDateString(string: String) =
    try {
      DateTimeFormatter.ISO_LOCAL_DATE.parse(string)
      true
    } catch {
      case e: Exception => false
    }
}