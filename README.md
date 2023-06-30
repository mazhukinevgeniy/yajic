# yajic

yet another java incremental compilation tool

## How to build

Use gradle. Tested with jbr-17.0.6.

You can create "fat jar" with `shadowJar` for convenience.

## How to use

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

Provide path to JDK, example:

```
mkdir -p src/test/resources/org/example/jictest
echo "testJdkDir=C\:\\Users\\evgen\\.jdks\\jbr-17.0.6" > src/test/resources/org/example/jictest/local.properties
```
