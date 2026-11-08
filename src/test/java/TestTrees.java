package edu.example.visitor;

final class TestTrees {

    private TestTrees() {
    }

    static DirectoryNode project() {
        DirectoryNode project = new DirectoryNode("project");
        project.add(src());
        project.add(new FileNode("README.md", 120));
        project.add(new FileNode("build.log", 0));
        project.add(docs());
        return project;
    }

    static DirectoryNode src() {
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode util = new DirectoryNode("util");
        src.add(new FileNode("Main.java", 300));
        src.add(util);
        util.add(new FileNode("Helper.java", 150));
        util.add(new FileNode("notes.txt", 50));
        src.add(new DirectoryNode("empty"));
        return src;
    }

    static DirectoryNode docs() {
        DirectoryNode docs = new DirectoryNode("docs");
        docs.add(new FileNode("guide.txt", 500));
        docs.add(new FileNode("draft.txt", 49));
        return docs;
    }
}
