package net.mas0061.astah.plugin.classes2excel.read

import com.change_vision.jude.api.inf.AstahAPI
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException
import com.change_vision.jude.api.inf.model.IClass
import com.change_vision.jude.api.inf.model.IElement
import com.change_vision.jude.api.inf.model.IModel
import com.change_vision.jude.api.inf.model.INamedElement
import com.change_vision.jude.api.inf.model.IPackage
import com.change_vision.jude.api.inf.project.ProjectAccessor
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ReadClasses {
    ProjectAccessor prjAccsr
    IModel project

    ReadClasses() {
//        getProject()
    }

    List<String> getClassAnnotations() {
        if (!isJavaProject()) throw new RuntimeException("This project is not Java project.")

        List<IClass> classes = []
        getClasses(project, classes)
        classes.collect {
            it.getName() + " : " + getAnnotation(it)
        }
    }

    List<ElementWithAnnotation> getClassesAttributes() {
        if (!isJavaProject()) throw new RuntimeException("This project is not Java project.")

        List<IClass> classes = []
        getClasses(project, classes)
        classes.findAll { isNotEnumOrInterface(it) }.collect {
            new ElementWithAnnotation(
                name: it.getName(),
                annotation: getAnnotation(it),
                type: it.getGeneralizations().collect {it.getSuperType().getName()}.join(":"),
                attributes: getAttributesAnnotations(it)
            )
        }
    }

    def exportClassAttributeListCSV(String fileName) {
        def elements = getClassesAttributes()
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


    def rowNum = 0

    def exportClassAttributeListExcel(String fileName) {
        def book = new XSSFWorkbook()
        // TODO Windowsで文字化けしたら有効にしてみる
//        book.createFont().setFontName("ＭＳ Ｐゴシック")
        def sheet = book.createSheet()
        rowNum = 0

        writeRow(sheet.createRow(rowNum), ["クラス名", "属性名", "型・継承元", "アノテーション"])

        def elements = getClassesAttributes()
        elements.each {
            writeRow(sheet.createRow(rowNum), it.toClassCommaString().tokenize(","))
            it.attributes.each {
                writeRow(sheet.createRow(rowNum), it.toAttributeCommaString().tokenize(","))
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

    private List<ElementWithAnnotation> getAttributesAnnotations(IClass iClass) {
        if (iClass == null) return []

        def attributes = iClass.getAttributes()
        attributes.findAll{it.getName() != "serialVersionUID"}
            .collect {
                new ElementWithAnnotation(
                    name: it.getName(),
                    annotation: getAnnotation(it),
                    type: it.getType().getName()
                )
            }
    }

    private String getAnnotation(IElement element) {
        if (!isJavaClassOrAttribute(element)) return ""
        def tags = element.getTaggedValues()
        def key = tags.find { it.getKey() != null && "jude.java.annotations".equals(it.getKey()) }
        key == null ? "" : key.getValue()
    }

    List<String> getClassesName() {
        List<IClass> classes = []
        getClasses(project, classes)
        classes.collect {it.getName()}
    }

    private def getProject() {
        prjAccsr = AstahAPI.getAstahAPI().getProjectAccessor()
        project = prjAccsr.getProject()
    }

    private def getClasses(INamedElement element, List<IClass> classes) throws ClassNotFoundException, ProjectNotFoundException {
        if (element instanceof IPackage) {
            (element as IPackage).getOwnedElements().each {
                if (!it.getFullName(".").find(/^java/)) getClasses(it as INamedElement, classes)
            }
        } else if (element instanceof IClass) {
            classes.add(element as IClass)
            (element as IClass).getNestedClasses().each {
                getClasses(it, classes)
            }
        }
    }

    boolean isJavaProject() {
        if (project == null) getProject()

        project.getTaggedValues().any {
            (it.getKey() == "jude.profile.java") && it.getValue() == "true"
        }
    }

    private boolean isJavaClassOrAttribute(IElement element) {
        getStereotypes(element).any { it == "Java Class" || it == "Java Attribute" }
    }

    private boolean isNotEnum(IElement element) {
        getStereotypes(element).every { it != "enum" }
    }

    private boolean isNotEnumOrInterface(IElement element) {
        getStereotypes(element).every { it != "enum" && it != "interface" }
    }

    private def getStereotypes(IElement element) {
        if (element == null) return []
        element.getStereotypes()
    }

    def forTest() {
        prjAccsr = AstahAPI.getAstahAPI().getProjectAccessor()
        prjAccsr.open("Facts.asta")
        project = prjAccsr.getProject()
    }
}