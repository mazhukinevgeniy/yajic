# yajic

yet another java incremental compilation tool

## How to build

//TODO provide useful instruction

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
echo "testJdkDir=C\:\\Users\\evgen\\.jdks\\jbr-17.0.6" > src/test/resources/org/example/jictest/local.properties
```
