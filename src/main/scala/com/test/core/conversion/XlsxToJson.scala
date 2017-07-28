package com.test.core.conversion

import java.io.{File, PrintWriter}
import org.apache.poi.ss.usermodel.{Row, WorkbookFactory}
import org.json4s.JsonAST
import org.json4s.jackson.JsonMethods.{pretty, render}
import org.json4s.JsonDSL._
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._


object XlsxToJson {
  private case class Rectangle(x1: Int, x2: Int, y1: Int, y2: Int)
  private case class Photo(path: String, rectangles: ArrayBuffer[Rectangle])

  /**
    *
    * @param fileName
    * @param rectangles
    * @param out
    */
  private def writeImageRectangles(fileName: String,
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
  private def writeLastImageRectangles(fileName: String,
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
  private def createJObject(photo: Photo): JsonAST.JObject = {
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
  private def createRectangle(row: Row): Rectangle = {
    Rectangle(
      math.min(row.getCell(7).getNumericCellValue.toInt,
                     row.getCell(9).getNumericCellValue.toInt): Int,
      math.max(row.getCell(7).getNumericCellValue.toInt,
                     row.getCell(9).getNumericCellValue.toInt): Int,
      math.min(row.getCell(8).getNumericCellValue.toInt,
                     row.getCell(10).getNumericCellValue.toInt): Int,
      math.max(row.getCell(8).getNumericCellValue.toInt,
                     row.getCell(10).getNumericCellValue.toInt): Int
    )
  }

  /**

  /**
    *
    * @param file
    * @param out
    */
  private def readExcelFile (file: File, out: PrintWriter): Unit = {
  val workbook =
    WorkbookFactory.create(new File(file.toString))
    val rectangles = new ArrayBuffer[Rectangle]()
  for {sheet <- workbook
  row <- sheet} {
   // val rowIterator = sheetIterator.next.rowIterator()
    var fileName = if (row.getPhysicalNumberOfCells == 1) row.getCell(0).toString
    if (row.getPhysicalNumberOfCells == 9) rectangles += createRectangle(row)


    if ((row.getPhysicalNumberOfCells == 1 & (sheetIterator.hasNext || rowIterator.hasNext)) {
      writeImageRectangles(fileName, rectangles, out)
      fileName = row.getCell(0).toString
      rectangles.clear()
    }





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
}


    */
  /**
    * Конвертирует файлы excel в файл JSON. Нужно добавить возможность конвертации нескольких файлов в один.
    * @param dirName - дирректория с файлами excel
    * @param outputFilePath
    */
  def convertXlsxToJson(dirName: String, outputFilePath: String): Unit = {
    var fileName = "nothing"
    val excelFiles =
      new File(dirName).listFiles.filter(_.getName.endsWith(".xlsx"))
    val out = new PrintWriter(outputFilePath)
    out.println("[")
    excelFiles.foreach { file => //нужно сделать отдельную функцию
      val sheetIterator =
        WorkbookFactory.create(new File(file.toString)).sheetIterator()
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
    }
    out.println("]")
    out.close()
  }

}
