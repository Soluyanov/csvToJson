package com.test.core.conversion

import java.io.File
import scala.collection.JavaConversions._
import org.apache.poi.ss.usermodel.WorkbookFactory

object NameChanger {

  /**
    * Названия фотографий в разных экспериментах могут повторяться, поэтому добавляем к названию каждой фотографии
    * дату эксперимента. Названия фото меняются как в файле excel, так и в самих фото, поэтому рекомендуется сначала
    * проверить соответствия имён с помощью NameChecker
    *
    * @param prefix - дата эксперимента (или любой другой префикс)
    * @param folderWithPhotos - путь к папке с фотографиями эксперимента
    * @param inputFilePath - путь к файлу excel
    */
  def renamePhotos(prefix: String,
                   folderWithPhotos: String,
                   inputFilePath: String): Unit = {
    val workbook =
      WorkbookFactory.create(new File(inputFilePath))
    for {
      sheet <- workbook
      row <- sheet
      if row.getPhysicalNumberOfCells == 1
    } {
      val file = new File(folderWithPhotos + row.getCell(0).toString)
      if (file.exists()) {
        file.renameTo(
          new java.io.File(folderWithPhotos + prefix + row.getCell(0).toString))
        val oldNameInCell = row.getCell(0).toString
        row.getCell(0).setCellValue(prefix + oldNameInCell)
      }
    }
    workbook.close()
  }
}
