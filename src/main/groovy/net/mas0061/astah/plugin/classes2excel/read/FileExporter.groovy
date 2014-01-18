package net.mas0061.astah.plugin.classes2excel.read

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/**
 * Created by mas on 2014/01/19.
 */
class FileExporter {
    def rowNum = 0

    def exportClassAttributeListCSV(List<ElementWithAnnotation> elements, String fileName) {
        def fileWriter = new File(fileName).newWriter("MS932")
        def firstLine = "クラス名,属性名,型・継承元,アノテーション"

        fileWriter.writeLine(firstLine)
        elements.each {
            fileWriter.writeLine(it.toClassCommaString())
            it.attributes.each {
                fileWriter.writeLine(it.toAttributeCommaString())
            }
        }

        fileWriter.close()
    }

    def exportClassAttributeListExcel(List<ElementWithAnnotation> elements, String fileName) {
        def book = new XSSFWorkbook()
        // TODO Windowsで文字化けしたら有効にしてみる → 大丈夫だったので不要
//        book.createFont().setFontName("ＭＳ Ｐゴシック")
        def sheet = book.createSheet()
        rowNum = 0

        writeRow(sheet.createRow(rowNum), ["クラス名", "属性名", "型・継承元", "アノテーション"])

        elements.each {
            writeRow(sheet.createRow(rowNum), it.toClassCommaString().split(",").toList())
            it.attributes.each {
                writeRow(sheet.createRow(rowNum), it.toAttributeCommaString().split(",").toList())
            }
        }

        new File(fileName).withOutputStream { book.write(it) }
    }

    def writeRow(XSSFRow row, List<String> line) {
        def cellNum = 0

        line.each {
            row.createCell(cellNum).setCellValue(it)
            cellNum++
        }
        rowNum++
    }

}
