package com.logginghub.container.parser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by james on 02/04/15.
 */
public class RegexParserOption implements Option {
    private final String endPattern;
    private NodeType nodeType;
    private String pattern;
    private List<Option> options;
    private boolean popOptions;
    private String substring;

    public RegexParserOption(NodeType nodeType, String pattern, String endPattern, List<Option> options, boolean popOptions) {
        this.nodeType = nodeType;
        this.pattern = pattern;
        this.endPattern = endPattern;
        this.options = options;
        this.popOptions = popOptions;
    }

    public boolean isPopOptions() {
        return popOptions;
    }

    @Override
    public List<Option> getOptions() {
        return options;
    }

    @Override
    public boolean matches(Pointer pointer) {

        Pattern p = Pattern.compile("\\A" + pattern);
        String head = pointer.getHead();
        Matcher matcher = p.matcher(head);

        boolean matches;
        if(matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            substring = head.substring(start, end);

            matches = true;
        }else{
            matches = false;
        }

        return matches;
    }

    @Override
    public Node consume(Pointer pointer) {
        Node node = new Node(nodeType, substring);
        pointer.skip(substring.length());
        return node;
    }
}
