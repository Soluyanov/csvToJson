package com.test.core.conversion

import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import java.io.PrintWriter
import scala.collection.mutable.ArrayBuffer
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

object CsvToJson {

  def main(args: Array[String]) {

    case class Rectangle(x1: Int, x2: Int, y1: Int, y2: Int)
    case class Photo(path: String, rectangles: ArrayBuffer[Rectangle])
    val rectangles = new ArrayBuffer[Rectangle]()
    import scala.collection.JavaConversions._
    val workbook =
      WorkbookFactory.create(new File("/home/alexander/Desktop/unionex.xlsx"))
    var fileName = "nothing"
    val out = new PrintWriter("/home/alexander/Desktop/output.json")
    out.println("[")

    for {
      sheet <- workbook
      row <- sheet
    } {
      if (row.getPhysicalNumberOfCells == 1 & rectangles.isEmpty)
        fileName = row.getCell(0).toString

      if (row.getPhysicalNumberOfCells == 1 & rectangles.nonEmpty) {

        val photo = Photo(fileName, rectangles)

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
        fileName = row.getCell(0).toString
        rectangles.clear()

      }

      if (row.getPhysicalNumberOfCells == 9) {

        rectangles += Rectangle(
          row.getCell(7).getNumericCellValue.toInt: Int,
          row.getCell(9).getNumericCellValue.toInt: Int,
          row.getCell(8).getNumericCellValue.toInt: Int,
          row.getCell(10).getNumericCellValue.toInt: Int
        )

      }
    }

  }

}
