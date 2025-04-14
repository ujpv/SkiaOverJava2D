# About
This project was set up to make some comparative analysis of the most basic rendering 
capabilities of Skia backend by Java2D.

# Build
```bash
./gradlew runTestApp
```

Notes:
- Make sure that JAVA_HOME is pointing at some JDK
- Make sure that there is `cmake` in `PATH`
- On Windows only Visual Studio is supported as the compiler
- Building the native lib is not supported on Linux

# Development
Run `./gradlew unzipSkiaNative` to dowlonad the SKIA sources.
To browse the native sources open [CMakeLists.txt](native/CMakeLists.txt). 
Make sure that `JAVA_HOME is set`.
