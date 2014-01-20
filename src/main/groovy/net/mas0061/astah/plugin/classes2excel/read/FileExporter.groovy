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
        def firstLine = "クラス名,属性名,型・継承元,アノテーション,備考"

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

        writeRow(sheet.createRow(rowNum), ["クラス名", "属性名", "型・継承元", "アノテーション", "備考"])

        elements.each {
            writeRow(sheet.createRow(rowNum), it.toClassCommaString().split(",").toList())
            it.attributes.each {
                writeRow(sheet.createRow(rowNum), it.toAttributeCommaString().split(",").toList())
            }
        }

        new File(fileName).withOutputStream { book.write(it) }
    }

    def exportClassChildListExcel(List<ElementWithAnnotation> elements, String fileName) {
        def book = new XSSFWorkbook()
        // TODO Windowsで文字化けしたら有効にしてみる → 大丈夫だったので不要
//        book.createFont().setFontName("ＭＳ Ｐゴシック")
        def sheet = book.createSheet("Fact構造一覧")
        rowNum = 0

        writeRow(sheet.createRow(rowNum), ["クラス名", "子クラス名", "備考"])

        elements.each {
            writeRow(sheet.createRow(rowNum), [it.name, "", it.etc])
            it.attributes.each {
                writeRow(sheet.createRow(rowNum), ["", it.name, it.etc])
            }
            // 空行出力
            writeRow(sheet.createRow(rowNum), [])
        }

        new File(fileName).withOutputStream { book.write(it) }
    }

    def exportClassDefinitionListExcel(List<ElementWithAnnotation> elements, String fileName) {
        def book = new XSSFWorkbook()
        // TODO Windowsで文字化けしたら有効にしてみる → 大丈夫だったので不要
//        book.createFont().setFontName("ＭＳ Ｐゴシック")
        def sheet = book.createSheet("Fact詳細")
        rowNum = 0
        def titleHeader = ["項目名", "属性", "I/O", "備考"]

        elements.each {
            writeRow(sheet.createRow(rowNum), [it.name])
            writeRow(sheet.createRow(rowNum), titleHeader)
            it.attributes.each {
                writeRow(sheet.createRow(rowNum), [it.name, it.type, it.annotation, it.etc])
            }
            // 空行出力
            writeRow(sheet.createRow(rowNum), [])
        }

        new File(fileName).withOutputStream { book.write(it) }
    }

    def writeRow(XSSFRow row, List<String> line) {
        def cellNum = 0

        line.each {
            if (it != null) {
                row.createCell(cellNum).setCellValue(it)
            } else {
                row.createCell(cellNum).setCellValue("")
            }
            cellNum++
        }
        rowNum++
    }

}
