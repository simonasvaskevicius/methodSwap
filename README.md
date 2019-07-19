# methodSwap

 Plugin that allows to edit external libraries source code!

## Requirements
- minSdkVersion 16
- compileSdkVersion 28

## Installation
#### Add methodSwap plugin into the project:

1. Item
2. Add methodSwap plugin into the project:
   * Add this to your project level build.gradle:
   
        ```
             buildscript {
                          repositories {
                            maven {
                              url "https://plugins.gradle.org/m2/"
                            }
                          }
                          dependencies {
                            classpath "gradle.plugin.com.simonasvaskevicius:methodSwap:1.0"
                          }
                        }
        ```
             
   * Add this to your application level build.gradle:  
        
        ```
              apply plugin: "com.simonasvaskevicius.methodSwap"
        ```   
3. Create an extension in application level build.gradle file, with custom settings:

   * Single method swap:
        ```
        android {
        
           methodReplace {
                   methodReplaceItems = [[
                                             //Your method title (without '()').
                                             methodTitle : 'testMethod',
        
                                             //New code, that will be swapped.
                                             replaceTo : 'public static boolean testMethod() { return true; }',
        
                                             //Package name, but with the class name at the end.
                                             className : 'com.simonasvaskevius.TestClass'
                                         ]]
               }
           }

        ```
   * Multiple method swaps:

        ```
        android {
        methodReplace {
                methodReplaceItems = [[
                                          methodTitle : 'testMethod1',
                                          replaceTo :
                                              'final boolean testMethod1() { return false; }',
                                          className : 'com.simonasvaskevius.TestClass1'
                                      ],
                                      [
                                          methodTitle : 'testMethod2',
                                          replaceTo :
                                              'public final java.lang.String testMethod2() { return null;}',
                                          className : 'com.simonasvaskevius.TestClass1'
                                      ],
                                      [
                                          methodTitle : 'testMethod3',
                                          replaceTo :
                                           'static java.lang.String 3(java.lang.String name)  { return null;}',
                                          className : 'com.simonasvaskevius.TestClass1'
                                          ]]
                }
           }
        ```

After successful project clean and rebuild, the methods will be changed!