package com.logginghub.container.parser;

/**
 * Created by james on 02/04/15.
 */
public class Pointer {
    private String string;
    private int position;

    public Pointer(String string) {
        this.string = string;
        this.position = 0;
    }

    @Override
    public String toString() {
        return String.format("String pointing at '%s', position '%d' of '%s'", current(), position,
                             string.substring(position, position + Math.min(50, string.length() - position)));
    }

    public boolean hasMore() {
        return position < string.length();
    }

    public char current() {
        return string.charAt(position);
    }

    public char peekPrevious() {
        return string.charAt(position - 1);
    }

    public char peekNext() {
        return string.charAt(position + 1);
    }

    public void next() {
        position++;
    }

    public String eatUntil(String in, String out) {

        int start = position;

        // Skip the openning tag
        position += in.length();

        // That tag counts as our first level of nesting
        int nesting = 1;

        while (nesting > 0) {

            if (positionMatches(in)) {
                nesting++;
                position += in.length();
            } else if (positionMatches(out)) {
                nesting--;
                position += out.length();
            } else {
                position++;
            }

        }

        String eaten = string.substring(start, position);
        return eaten;

    }

    private boolean positionMatches(String out) {
        if (position + out.length() > string.length()) {
            return false;
        } else {
            String sub = string.substring(position, position + out.length());
            return sub.equals(out);
        }
    }

    public String eatUntil(String s) {
        int start = position;

        position += s.length();

        int index = string.indexOf(s, position);

        String eaten = null;

        if (index != -1) {
            eaten = string.substring(start, index + s.length());
            position = index + s.length();
        }

        return eaten;

    }

    public String getHead() {
        return string.substring(position);
    }

    public void skip(int length) {
        position += length;
    }
}
