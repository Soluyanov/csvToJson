package com.test.core.conversion

object Converter {

  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println(
        "Incorrect arguments" + '\n' +
          "Provide tool and tool parameters")
      System.exit(1)
    }
    args(0) match {
      case "checkPhotosNames"  => NameChecker.checkPhotosNames(args(1), args(2))
      case "renamePhotos"      => NameChanger.renamePhotos(args(1), args(2), args(3))
      case "convertXlsxToJson" => XlsxToJson.convertXlsxToJson(args(1), args(2))
      case _ =>
        System.err.println("ERROR: Incorrect tool")
        System.exit(1)

    }
  }
}
