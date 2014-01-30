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
        getProject()
    }

    ReadClasses(FileInputStream fileStream) {
        prjAccsr = AstahAPI.getAstahAPI().getProjectAccessor()
        prjAccsr.open( fileStream)
        project = prjAccsr.getProject()
    }

    def close() {
        prjAccsr.close()
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
            def annotation = getAnnotation(it)
            new ElementWithAnnotation(
                name: it.getName(),
                annotation: formatAnnotation(annotation),
                parent: it.getGeneralizations().collect {it.getSuperType().getName()}.join(":"),
                attributes: getAttributesAnnotations(it),
                etc: getEtc(annotation)
            )
        }
    }

    List<ElementWithAnnotation> getClassStructure() {
        if (!isJavaProject()) throw new RuntimeException("This project is not Java project.")

        List<IClass> classes = []
        getClasses(project, classes)
        def attrClasses = classes.findAll { isNotEnumOrInterface(it) }.collect {
            def annotation = getAnnotation(it)
            def attributes = getChildAttributes(it)
            if (attributes.size() > 0) {
                new ElementWithAnnotation(
                        name: it.getName(),
                        annotation: formatAnnotation(annotation),
                        parent: it.getGeneralizations().collect {it.getSuperType().getName()}.join(":"),
                        attributes: attributes,
                        etc: getEtc(annotation)
                )
            }
        }

        return attrClasses.findAll {it != null }
    }

    private List<ElementWithAnnotation> getAttributesAnnotations(IClass iClass) {
        if (iClass == null) return []

        def attributes = iClass.getAttributes()
        attributes.findAll{it.getName() != "serialVersionUID"}
            .collect {
                def annotation = getAnnotation(it)
                new ElementWithAnnotation(
                    name: it.getName(),
                    annotation: formatAnnotation(annotation),
                    type: it.getType().getName(),
                    etc: getEtc(annotation)
                )
            }
    }

    private List<ElementWithAnnotation> getChildAttributes(IClass iClass) {
        if (iClass == null) return []

        def attributes = iClass.getAttributes()
        attributes.findAll{it.getName() != "serialVersionUID" && getAnnotation(it).contains("Child")}
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

    private String formatAnnotation(String annotation) {
        annotation.replaceAll(/(@(.*?)\(".*?"\))/, /$2/).replaceAll(/@/, "")
   }

    private static String getEtc(String element) {
        def ret = "";
        element.split(",").each {
            def extract = it.find(/@.*\("(.*)"\)/) {str, m -> m}
            if (extract != null && extract != "null") ret += extract + ","
        }

        ret.replaceFirst(/,$/, "")
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

}