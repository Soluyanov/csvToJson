package com.test.core.conversion
import scala.collection.JavaConversions._
import java.io.File
import org.apache.poi.ss.usermodel.WorkbookFactory

object NameChecker {

  /**
    * Название фотографии в файле Excel может отличаться от названия самой фотографии. Метод составляет список названий
    * фотографий из файла Excel, для которых не было найдено соответствий среди фото. Необходимо вручную изменить название фото.
    * @param inputFilePath путь к файлу Excel
    *
    * */
  def checkPhotosNames(inputFilePath: String, folderWithPhotos: String): Unit = {
    val workbook =
      WorkbookFactory.create(new File(inputFilePath))
    for {
      sheet <- workbook
      row <- sheet
    } {
      if (row.getPhysicalNumberOfCells == 1 & new File(
            folderWithPhotos + row.getCell(0).toString).exists())
        println(row.getCell(0).toString)
    }
    workbook.close()
  }
}
