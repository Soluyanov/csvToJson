package com.test.core.conversion

import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import java.io.PrintWriter
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

object CsvToJson {

  def main(args: Array[String]) {
    case class Rectangle(x1: Int, x2: Int, y1: Int, y2: Int)
    case class Photo(path: String, rectangles: ArrayBuffer[Rectangle])
    val rectangles1 = new ArrayBuffer[Rectangle]()
    val file = Source
      .fromFile("/home/alexander/Desktop/resized/unionex.csv")
      .getLines
    var fileName = "nothing"
    val out = new PrintWriter("/home/alexander/Desktop/output.json")
    out.println("[")
    for (line <- file) {

      val cells = line.split(";")
      if (cells.length == 1 & rectangles1.isEmpty) {
        fileName = cells(0)
      //  println("/home/alexander/Desktop/resized/" + fileName + ".jpg")
      }

      if (new java.io.File(
            "/home/alexander/Desktop/resized/" + fileName + ".jpg").exists) {

        if (cells.length == 1 & rectangles1.nonEmpty) {

          val photo = Photo(fileName, rectangles1)

          val obj =
            ("image_path" -> photo.path) ~
              ("rects" ->
                photo.rectangles.map { w =>
                  ("x1" -> w.x1) ~
                    ("x2" -> w.x2) ~
                    ("y1" -> w.y1) ~
                    ("y2" -> w.y2)
                })
          out.println(pretty(render(obj)) + ",")
          fileName = cells(0)
          rectangles1.clear()

        }

        if (cells.length > 1) {
          if (!cells(0).isEmpty) {
          //  println ("add rect")

            rectangles1 += Rectangle((cells(7).toInt*0.25).toInt: Int,
              (cells(9).toInt*0.25).toInt: Int,
              (cells(8).toInt*0.25).toInt: Int,
              (cells(10).toInt*0.25).toInt: Int)

          }
        }
      }
      else println (fileName)
    }

    out.println("]")
    out.close()
  }
}
