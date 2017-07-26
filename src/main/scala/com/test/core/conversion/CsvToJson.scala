package com.test.core.conversion

import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import java.io.PrintWriter
import scala.collection.mutable.ArrayBuffer
import org.apache.poi.ss.usermodel.{Row, Sheet, WorkbookFactory}
import java.io.File
import org.json4s.JsonAST

object CsvToJson {

  def main(args: Array[String]) {

    case class Rectangle(x1: Int, x2: Int, y1: Int, y2: Int)
    case class Photo(path: String, rectangles: ArrayBuffer[Rectangle])

    /**
      *
      * @param fileName
      * @param rectangles
      * @param out
      */
    def writeImageRectangles(fileName: String,
                             rectangles: ArrayBuffer[Rectangle],
                             out: PrintWriter): Unit = {
      val photo = Photo(fileName, rectangles)
      out.println(pretty(render(createJObject(photo))) + ",")
    }

    /**
      *
      * @param fileName
      * @param rectangles
      * @param out
      */
    def writeLastImageRectangles(fileName: String,
                                 rectangles: ArrayBuffer[Rectangle],
                                 out: PrintWriter): Unit = {
      val photo = Photo(fileName, rectangles)
      out.println(pretty(render(createJObject(photo))))
    }

    /**
      *
      * @param photo
      * @return
      */
    def createJObject(photo: Photo): JsonAST.JObject = {
      ("image_path" -> photo.path) ~
        ("rects" ->
          photo.rectangles.map { w =>
            ("x1" -> w.x1) ~
              ("x2" -> w.x2) ~
              ("y1" -> w.y1) ~
              ("y2" -> w.y2)
          })

    }

    /**
      *
      * @param row
      * @return
      */
    def createRectangle(row: Row): Rectangle = {
      Rectangle(
        row.getCell(7).getNumericCellValue.toInt: Int,
        row.getCell(9).getNumericCellValue.toInt: Int,
        row.getCell(8).getNumericCellValue.toInt: Int,
        row.getCell(10).getNumericCellValue.toInt: Int
      )
    }

    /**
      *
      * @param inputFilePath
      * @param outputFilePath
      */
    def convertXlsxToJson(inputFilePath: String, outputFilePath: String): Unit = {
      var fileName = "nothing"

      val out = new PrintWriter(outputFilePath)
      out.println("[")
      val sheetIterator =
        WorkbookFactory.create(new File(inputFilePath)).sheetIterator()
      while (sheetIterator.hasNext) {
        val rowIterator = sheetIterator.next.rowIterator()
        fileName = rowIterator.next().getCell(0).toString
        val rectangles = new ArrayBuffer[Rectangle]()
        while (rowIterator.hasNext) {
          val row = rowIterator.next()
          if (row.getPhysicalNumberOfCells == 9) {
            rectangles += createRectangle(row)
          }
          if ((row.getPhysicalNumberOfCells == 1 || !rowIterator.hasNext) & (sheetIterator.hasNext || rowIterator.hasNext)) {
            writeImageRectangles(fileName, rectangles, out)
            fileName = row.getCell(0).toString
            rectangles.clear()
          }
          if (!rowIterator.hasNext & !sheetIterator.hasNext) {
            writeLastImageRectangles(fileName, rectangles, out)
          }
        }
      }
      out.println("]")
      out.close()
    }

    convertXlsxToJson("/home/alexander/Desktop/test.xlsx",
                      "/home/alexander/Desktop/output.json")

  }
}
