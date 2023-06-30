# yajic

yet another java incremental compilation tool

## How to build

You can open repository root as an intellij project, and build everything with gradle. Tool is tested with jbr-17.0.6.

You can create "fat jar" with `shadowJar` for convenience.

## How to use the tool

```
Usage: yajic <options>
Options:
    --classpath, -c (required) { String }
    --sourceDir, -s (required) { String }
    --java_home, -j -> path to jdk. JAVA_HOME is used by default { String }
    --output, -o -> output directory. yajic_out is used by default { String }
    --help, -h -> Usage info
```

## How to run tests

Provide path to JDK. Example:

```
mkdir -p src/test/resources/org/example/jictest
echo "testJdkDir=C\:\\Users\\evgen\\.jdks\\jbr-17.0.6" > src/test/resources/org/example/jictest/local.properties
```

## How to read tests

Every test does multiple compilation steps. To compare sources between these steps:
```
diff -r src/test/resources/sources/change_constructor/1 src/test/resources/sources/change_constructor/2
```
