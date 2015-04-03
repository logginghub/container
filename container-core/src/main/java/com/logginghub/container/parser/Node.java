package com.logginghub.container.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by james on 02/04/15.
 */
public class Node {
    public NodeType type;
    public String text;
    public List<Node> children = new ArrayList<>();

    public Node(NodeType type, String text) {
        this.type = type;
        this.text = text;
    }

    public List<Node> getChildren() {
        return children;
    }

    public NodeType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    @Override
    public String toString() {
        return String.format("Node type %s : %s", type, text);
    }

    public void mergeNormalChildren() {

        Iterator<Node> iterator = children.iterator();
        Node previous = null;
        while (iterator.hasNext()) {

            Node node = iterator.next();

            if (previous != null && previous.type == node.type && node.type == NodeType.Text) {
                previous.text += node.text;
                iterator.remove();
            } else {
                previous = node;
            }
        }

    }

    public void delete(String s) {
        Iterator<Node> iterator = children.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.text != null && node.text.equals(s)) {
                iterator.remove();
            }
        }
    }
}
