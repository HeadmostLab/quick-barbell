package com.headmostlab.quickbarbell.testutils;

public class Sequence {
    private int id;
    int next() {
        return id++;
    }
    int current() {
        return id;
    }
}
