package com.logginghub.container.loader

import com.logginghub.container.parser.CMLParser
import com.logginghub.container.parser.NodeType
import groovy.json.JsonBuilder
import spock.lang.Specification

/**
 * Created by james on 02/04/15.
 */
class CMLParserTest extends Specification {

    def "test javascript style"() {
        def string = "xml: { name: 'value' }"
        println string

        CMLParser parser = new CMLParser();

        when:
        def parsed = parser.parse(string);

        then:
        println new JsonBuilder(parsed).toPrettyString()

        parsed.children.size() == 9
        parsed.children[0].type == NodeType.OpenTag
        parsed.children[1].type == NodeType.Text
        parsed.children[2].type == NodeType.WhiteSpace
        parsed.children[3].type == NodeType.Text
        parsed.children[4].type == NodeType.AttributeEquals
        parsed.children[5].type == NodeType.AttributeSingleQuotes
        parsed.children[6].type == NodeType.Text
        parsed.children[7].type == NodeType.AttributeSingleQuotes
        parsed.children[8].type == NodeType.EndTag
    }

    def "test xml tag"() {

        def string = "<xml>hello</xml>"
        println string

        CMLParser parser = new CMLParser();

        when:
        def parsed = parser.parse(string);

        then:
        println new JsonBuilder(parsed).toPrettyString()

        parsed.children.size() == 7
        parsed.children[0].type == NodeType.OpenTag
        parsed.children[1].type == NodeType.Text
        parsed.children[2].type == NodeType.CloseTag
        parsed.children[3].type == NodeType.Text
        parsed.children[4].type == NodeType.EndTag
        parsed.children[5].type == NodeType.Text
        parsed.children[6].type == NodeType.CloseTag
    }

    def "test xml attributes single quotes"() {

        def string = "<xml name='value'/>"
        println string

        CMLParser parser = new CMLParser();

        when:
        def parsed = parser.parse(string);

        then:
        println new JsonBuilder(parsed).toPrettyString()

        parsed.children.size() == 9
        parsed.children[0].type == NodeType.OpenTag
        parsed.children[1].type == NodeType.Text
        parsed.children[2].type == NodeType.WhiteSpace
        parsed.children[3].type == NodeType.Text
        parsed.children[4].type == NodeType.AttributeEquals
        parsed.children[5].type == NodeType.AttributeSingleQuotes
        parsed.children[6].type == NodeType.Text
        parsed.children[7].type == NodeType.AttributeSingleQuotes
        parsed.children[8].type == NodeType.EndTag


    }


}

