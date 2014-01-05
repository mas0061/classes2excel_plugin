package net.mas0061.astah.plugin.classes2excel.read

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by mas on 2013/12/27.
 */
class ReadClassesTest extends Specification {
    def "クラスの一覧とアノテーションを取得するテスト"() {
        setup:
        def readClasses = new ReadClasses()
        readClasses.forTest()

        when:
        def classesName = readClasses.getClassAnnotations()

        then:
        classesName.size() == 50
    }

    def "クラスと属性とアノテーションを取得するテスト"() {
        setup:
        def readClasses = new ReadClasses()
        readClasses.forTest()

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
        def readClasses = new ReadClasses()
        readClasses.forTest()

        when:
        def expectFileName = "out.csv"
        readClasses.exportClassAttributeListCSV(expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))
    }

    def "クラスと属性とアノテーションがExcel2007形式で出力されている"() {
        setup:
        def readClasses = new ReadClasses()
        readClasses.forTest()

        when:
        def expectFileName = "out.xlsx"
        readClasses.exportClassAttributeListExcel(expectFileName)

        then:
        Files.exists(Paths.get(expectFileName))
    }
}
