package com.logginghub.container.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by james on 23/02/15.
 */
public class CMLParser {


    private List<Option> options = new ArrayList<>();

    public CMLParser() {

        List<Option> tagOptions = new ArrayList<>();
        tagOptions.add(new RegexParserOption(NodeType.Text, "\\w+", "", null, false));
        tagOptions.add(new RegexParserOption(NodeType.WhiteSpace, "\\s+", "", null, false));
        tagOptions.add(new RegexParserOption(NodeType.AttributeEquals, "=", "", null, false));
        tagOptions.add(new RegexParserOption(NodeType.AttributeSingleQuotes, "\\'", "", null, false));

        tagOptions.add(new RegexParserOption(NodeType.EndTag, "/>", "", null, true));
        tagOptions.add(new RegexParserOption(NodeType.CloseTag, ">", "", null, true));

        options.add(new RegexParserOption(NodeType.EndTag, "</", "", tagOptions, false));
        options.add(new RegexParserOption(NodeType.OpenTag, "<", "", tagOptions, false));
        options.add(new RegexParserOption(NodeType.Text, "\\w+", "", null, false));
    }

    public Node parse(String string) {
        Node node = new Node(NodeType.Root, "");

        Stack<Option> optionStack = new Stack<Option>();

        Option defaultOption = new Option() {
            @Override
            public boolean matches(Pointer pointer) {
                return false;
            }

            @Override
            public Node consume(Pointer pointer) {
                return null;
            }

            @Override
            public List<Option> getOptions() {
                return options;
            }

            @Override
            public boolean isPopOptions() {
                return false;
            }
        };

        optionStack.add(defaultOption);

        Pointer pointer = new Pointer(string);

        while (pointer.hasMore()) {
            System.out.println(pointer);
            Node child = null;
            List<Option> optionList = optionStack.peek().getOptions();
            for (Option option : optionList) {
                if (option.matches(pointer)) {
                    child = option.consume(pointer);
                    System.out.println("Node :" + child);

                    List<Option> subOptions = option.getOptions();
                    if (subOptions != null) {
                        optionStack.push(option);
                    }

                    if(option.isPopOptions()) {
                        optionStack.pop();
                    }

                    // TODO : option should be able to control recursion, and also vary the options list for sub-element parsing
                    //   recursiveParse(child);

                    break;
                }
            }

            if (child == null) {
                throw new RuntimeException(String.format("No option matched %s", pointer.toString()));
            } else {
                node.addChild(child);


            }
        }

        node.mergeNormalChildren();
        node.delete("\\n");

        return node;
    }

    private void recursiveParse(Node node) {

        Pointer pointer = new Pointer(node.text);

        while (pointer.hasMore()) {
            Node child = null;
            for (Option option : options) {
                if (option.matches(pointer)) {
                    child = option.consume(pointer);
                    // TODO : option should be able to control recursion
                    recursiveParse(child);
                    break;
                }
            }

            if (child == null) {
                throw new RuntimeException(String.format("No option matched %s", pointer.toString()));
            } else {
                node.addChild(child);
            }
        }

    }

    //    public static class PlainTextRender {
    //
    //        public String render(Node root) {
    //
    //            StringUtils.StringUtilsBuilder builder = new StringUtils.StringUtilsBuilder();
    //
    //            List<Node> children = root.children;
    //            for (Node child : children) {
    //
    //                switch (child.type) {
    //                    case CurlyBraces: {
    //                        builder.append(StringUtils.between(child.text, "{{", "}}"));
    //                        break;
    //                    }
    //                    case DoubleEquals: {
    //                        builder.appendLine(StringUtils.between(child.text, "==", "=="));
    //                        break;
    //                    }
    //                    case DoubleQuotes: {
    //                        builder.append(StringUtils.between(child.text, "''", "''"));
    //                        break;
    //                    }
    //                    case SquareBraces: {
    //                        builder.append(StringUtils.between(child.text, "[[", "]]"));
    //                        break;
    //                    }
    //                    case TripleQuotes: {
    //                        builder.append(StringUtils.between(child.text, "'''", "'''"));
    //                        break;
    //                    }
    //                    case Text:
    //                        builder.append(child.text);
    //                        break;
    //                    case TripleEquals:
    //                        builder.appendLine(StringUtils.between(child.text, "===", "==="));
    //                        break;
    //
    //                    case Reference:
    //
    //                }
    //
    //            }
    //
    //            return builder.toString();
    //        }
    //
    //    }

}


