package net.mas0061.astah.plugin.classes2excel.read

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by mas on 2013/12/27.
 */
class ReadClassesTest extends Specification {
    def testFile = new FileInputStream(System.getProperty("user.home") + "/Desktop/Facts.asta")

    def "クラスの一覧とアノテーションを取得するテスト"() {
        setup:
        def readClasses = new ReadClasses(testFile)

        when:
        def classesName = readClasses.getClassAnnotations()

        then:
        classesName.size() == 54

        cleanup:
        readClasses.close()
    }

    def "クラスと属性とアノテーションを取得するテスト"() {
        setup:
        def readClasses = new ReadClasses(testFile)

        when:
        def classInfo = readClasses.getClassesAttributes()
        classInfo.each {
            println "【Class】 " + it.name + " : " + it.type + ", " + it.annotation + ", " + it.etc
            it.attributes.each {
                println "  `- " + it.name + " : " + it.type + ", " + it.annotation + ", " + it.etc
            }
        }

        then:
        classInfo.size() == 46

        cleanup:
        readClasses.close()
    }

    def "クラスと属性とアノテーションがCSV出力されている"() {
        setup:
        def expectFileName = System.getProperty("user.home") + "/Desktop/out.csv"
        deleteExistsFile(expectFileName)
        def readClasses = new ReadClasses(testFile)

        when:
        def classes = readClasses.getClassesAttributes()
        new FileExporter().exportClassAttributeListCSV(classes, expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))

        cleanup:
        readClasses.close()
    }

    def "クラスと属性とアノテーションがExcel2007形式で出力されている"() {
        setup:
        def expectFileName = System.getProperty("user.home") + "/Desktop/out.xlsx"
        deleteExistsFile(expectFileName)
        def readClasses = new ReadClasses(testFile)

        when:
        def classes = readClasses.getClassesAttributes()
        new FileExporter().exportClassAttributeListExcel(classes, expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))

        cleanup:
        readClasses.close()
    }

    def "クラス一覧がExcelで出力されている"() {
        setup:
        def expectFileName = System.getProperty("user.home") + "/Desktop/out.xlsx"
        deleteExistsFile(expectFileName)
        def readClasses = new ReadClasses(testFile)

        when:
        def classInfo = readClasses.getClassStructure()
        new FileExporter().exportClassChildListExcel(classInfo, expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))

        cleanup:
        readClasses.close()
    }

    def "クラス詳細がExcelで出力されている"() {
        setup:
        def expectFileName = System.getProperty("user.home") + "/Desktop/out.xlsx"
        deleteExistsFile(expectFileName)
        def readClasses = new ReadClasses(testFile)

        when:
        def classInfo = readClasses.getClassesAttributes()
        new FileExporter().exportClassDefinitionListExcel(classInfo, expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))

        cleanup:
        readClasses.close()
    }

    def "クラス一覧と詳細が1つのExcelファイルに出力されている"() {
        setup:
        def expectFileName = System.getProperty("user.home") + "/Desktop/outAll.xlsx"
        deleteExistsFile(expectFileName)
        def readClasses = new ReadClasses(testFile)

        when:
        def classInfo = readClasses.getClassStructure()
        def attrInfo = readClasses.getClassesAttributes()
        new FileExporter().exportAllListExcel(classInfo, attrInfo, expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))

        cleanup:
        readClasses.close()
    }

    def deleteExistsFile(String fileName) {
        Path path = Paths.get(fileName)
        if (Files.exists(path)) {
            Files.delete(path)
        }
    }
}
