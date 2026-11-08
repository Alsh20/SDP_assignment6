# Assignment 6 - Ospanov Alisher - SE-2526

## Run

```bash
mvn test
mvn -q package
java -cp target/classes edu.example.visitor.Demo
```

Demo output:

```
Total bytes: 830
Search (*.txt, size >= 50): [notes.txt, src/archive/old.txt]
Statistics: files=5, directories=5, maxDepth=3
Total bytes, same visitor reused: 830
Total bytes of docs subtree: 260
Rejected: Sibling name already exists: notes.txt
Rejected: File size cannot be negative
Rejected: Adding element would create a cycle: workspace
```

## UML

`uml/visitor-pattern.puml`