package net.mas0061.astah.plugin.classes2excel.read

/**
 * Created by mas on 2013/12/30.
 */
class ElementWithAnnotation {
    String name
    String parent
    String annotation
    String type
    String etc

    List<ElementWithAnnotation> attributes

    def toClassCommaString() {
        name + ",," + type + "," + convertComma2Slash(annotation) + "," + etc
    }

    def toAttributeCommaString() {
        "," + name + "," + type + "," + convertComma2Slash(annotation) + "," + etc
    }

    def static convertComma2Slash(String commaStr) {
        commaStr.replaceAll(/,/, "/")
    }
}
