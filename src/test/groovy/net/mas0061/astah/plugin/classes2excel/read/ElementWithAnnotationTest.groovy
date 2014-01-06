package net.mas0061.astah.plugin.classes2excel.read

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by mas on 2014/01/03.
 */
class ElementWithAnnotationTest extends Specification {

    @Unroll
    def "convertComma2Slashで #testName テスト"() {
        when:
        def actual = ElementWithAnnotation.convertComma2Slash(元データ)

        then:
        actual == 期待値

        where:
        元データ | 期待値 | testName
        "aaa,bbb,ccc,あああ,いいい" | "aaa/bbb/ccc/あああ/いいい" | "通常のカンマ区切り"
        "aaa,bbb,,,ccc,あああ,いいい" | "aaa/bbb///ccc/あああ/いいい" | "カンマが連続している部分がある"
        "aaa,bbb,ccc,あああ,いいい," | "aaa/bbb/ccc/あああ/いいい/" | "文字列の最後にカンマがある"
        ",aaa,bbb,ccc,あああ,いいい" | "/aaa/bbb/ccc/あああ/いいい" | "文字列の最初にカンマがある"
    }
}
