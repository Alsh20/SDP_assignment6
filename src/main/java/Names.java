package edu.example.visitor;

final class Names {

    private Names() {
    }

    static String requireValid(String name) {
        if (name == null || name.isBlank() || name.contains("/") || name.equals(".") || name.equals("..")) {
            throw new IllegalArgumentException("Name must be non-blank and a single path segment");
        }
        return name;
    }
}
