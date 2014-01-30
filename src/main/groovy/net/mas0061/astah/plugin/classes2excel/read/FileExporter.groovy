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
        def dataList = []
        dataList.add(["クラス名", "属性名", "型・継承元", "アノテーション", "備考"])

        elements.each {
            dataList.add(it.toClassCommaString().split(",").toList())
            it.attributes.each {
                dataList.add(it.toAttributeCommaString().split(",").toList())
            }
        }

        writeRowList(fileName, "クラス・属性一覧", dataList)
    }

    def exportClassChildListExcel(List<ElementWithAnnotation> elements, String fileName) {
        def dataList = []

        elements.each {
            dataList.add(["クラス名", "子クラス名", "備考"])
            dataList.add([it.name, "", it.etc])
            it.attributes.each {
                dataList.add(["", it.name, it.etc])
            }
            // 空行出力
            dataList.add([])
        }

        writeRowList(fileName, "クラス一覧", dataList)
    }

    def exportClassDefinitionListExcel(List<ElementWithAnnotation> elements, String fileName) {
        def dataList = []

        elements.each {
            dataList.add([it.name])
            dataList.add(["項目名", "属性", "I/O", "備考"])
            it.attributes.each {
                dataList.add([it.name, it.type, it.annotation, it.etc])
            }
            // 空行出力
            dataList.add([])
        }

        writeRowList(fileName, "クラス詳細", dataList)
    }

    def writeRowList(String fileName, String sheetName, List<List<String>> lines) {
        def book = new XSSFWorkbook()
        def sheet = book.createSheet(sheetName)
        def rowNum = 0
        lines.each {
            writeRow(sheet.createRow(rowNum), it)
            rowNum++
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
