# FoxLibrary

用于小型app快速开发的工具性框架，封装了部分常用功能和方法

## 接入

在项目根目录中的build.gradle 内添加如下maven库
```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

然后在应用模块的build.gradle 中添加以下依赖
```groovy
dependencies {
    implementation 'com.github.binzeefox:FoxLibrary:Tag'
}
```
Tag处填写发布的最新版本号

[![](https://jitpack.io/v/binzeefox/FoxLibrary.svg)](https://jitpack.io/#binzeefox/FoxLibrary)