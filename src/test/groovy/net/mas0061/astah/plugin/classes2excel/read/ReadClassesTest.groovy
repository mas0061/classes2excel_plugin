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
        classesName.size() == 50
    }

    def "クラスと属性とアノテーションを取得するテスト"() {
        setup:
        def readClasses = new ReadClasses(testFile)

        when:
        def classInfo = readClasses.getClassesAttributes()
        classInfo.each {
            println "【Class】 " + it.name + " : " + it.type + ", " + it.annotation
            it.attributes.each {
                println "  `- " + it.name + " : " + it.type + ", " + it.annotation
            }
        }

        then:
        classInfo.size() == 42
    }

    def "クラスと属性とアノテーションがCSV出力されている"() {
        setup:
        def expectFileName = "out.csv"
        deleteExistsFile(expectFileName)
        def readClasses = new ReadClasses(testFile)

        when:
        def classes = readClasses.getClassesAttributes()
        new FileExporter().exportClassAttributeListCSV(classes, expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))
    }

    def "クラスと属性とアノテーションがExcel2007形式で出力されている"() {
        setup:
        def expectFileName = "out.xlsx"
        deleteExistsFile(expectFileName)
        def readClasses = new ReadClasses(testFile)

        when:
        def classes = readClasses.getClassesAttributes()
        new FileExporter().exportClassAttributeListExcel(classes, expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))
    }

    def deleteExistsFile(String fileName) {
        Path path = Paths.get(fileName)
        if (Files.exists(path)) {
            Files.delete(path)
        }
    }
}
