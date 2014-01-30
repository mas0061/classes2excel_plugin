package net.mas0061.astah.plugin.classes2excel.read

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/**
 * Created by mas on 2014/01/19.
 */
class FileExporter {
    def rowNum = 0

    public def exportClassAttributeListCSV(List<ElementWithAnnotation> elements, String fileName) {
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

    public def exportClassAttributeListExcel(List<ElementWithAnnotation> elements, String fileName) {
        def dataList = []
        dataList.add(["クラス名", "属性名", "型・継承元", "アノテーション", "備考"])

        elements.each {
            dataList.add(it.toClassCommaString().split(",").toList())
            it.attributes.each {
                dataList.add(it.toAttributeCommaString().split(",").toList())
            }
        }

        def book = writeRowList(new XSSFWorkbook(), "クラス・属性一覧", dataList)
        writeFile(book, new File(fileName))
    }

    public def exportAllListExcel(List<ElementWithAnnotation> classElements, List<ElementWithAnnotation> attrElements, String fileName) {
        exportAllListExcel(classElements, attrElements, new File(fileName))
    }

    public def exportAllListExcel(List<ElementWithAnnotation> classElements, List<ElementWithAnnotation> attrElements, File file) {
        def book = writeRowList(new XSSFWorkbook(), "クラス一覧", createClassList(classElements))
        book = writeRowList(book, "クラス詳細", createDefinitionList(attrElements))
        writeFile(book, file)
    }

    public def exportClassChildListExcel(List<ElementWithAnnotation> elements, String fileName) {
        exportClassChildListExcel(elements, new File(fileName))
    }

    public def exportClassChildListExcel(List<ElementWithAnnotation> elements, File file) {
        def writeDataList = createClassList(elements)
        def book = writeRowList(new XSSFWorkbook(), "クラス一覧", writeDataList)
        writeFile(book, file)
    }

    public def exportClassDefinitionListExcel(List<ElementWithAnnotation> elements, String fileName) {
        exportClassDefinitionListExcel(elements, new File(fileName))
    }

    public def exportClassDefinitionListExcel(List<ElementWithAnnotation> elements, File file) {
        def writeDataList = createDefinitionList(elements)
        def book = writeRowList(new XSSFWorkbook(), "クラス詳細", writeDataList)
        writeFile(book, file)
    }

    def createClassList(List<ElementWithAnnotation> elements) {
        def dataList = []

        elements.each {
            dataList.add(["クラス名", "継承元", "属性クラス名", "備考"])
            dataList.add([it.name, it.parent, "", it.etc])
            it.attributes.each {
                dataList.add(["", "", it.name, it.etc])
            }
            // 空行出力
            dataList.add([])
        }

        dataList
    }

    def createDefinitionList(List<ElementWithAnnotation> elements) {
        def dataList = []

        elements.each {
            dataList.add([it.name])
            dataList.add(["属性名", "属性", "注釈", "備考"])
            it.attributes.each {
                dataList.add([it.name, it.type, it.annotation, it.etc])
            }
            // 空行出力
            dataList.add([])
        }

        dataList
    }

    def writeFile(XSSFWorkbook book, File file) {
        file.withOutputStream { book.write(it) }
    }

    def writeRowList(XSSFWorkbook book, String sheetName, List<List<String>> lines) {
        def sheet = book.createSheet(sheetName)
        def rowNum = 0
        lines.each {
            writeRow(sheet.createRow(rowNum), it)
            rowNum++
        }
        book
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
