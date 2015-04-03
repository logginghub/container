package com.logginghub.container.parser

import spock.lang.Specification

/**
 * Created by james on 02/04/15.
 */
class RegexParserOptionTest extends Specification {

    def "test" (){

        given:
        RegexParserOption option = new RegexParserOption(NodeType.OpenTag, "<" , "", null, false);
        when:
        Pointer pointer = new Pointer("<hello>");

        then:
        option.matches(pointer) == true

        when:
        def node = option.consume(pointer)

        then:
        node.getType() == NodeType.OpenTag
        node.getText() == "<"

    }

    def "test text matcher" (){

        given:
        RegexParserOption option = new RegexParserOption(NodeType.Text, "\\A\\w+" , "", null, false);

        when:
        Pointer pointer1 = new Pointer("hello>hello");
        Pointer pointer2 = new Pointer(">hello>hello");

        then:
        option.matches(pointer1) == true
        option.matches(pointer2) == false

        when:
        def node = option.consume(pointer1)

        then:
        node.getType() == NodeType.Text
        node.getText() == "hello"

    }

}
