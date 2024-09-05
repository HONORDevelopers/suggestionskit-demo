# Honor Suggestions Kit service sample code

[![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)](http://www.apache.org/licenses/LICENSE-2.0)
[![Java Language](https://img.shields.io/badge/language-java-green.svg)](https://www.java.com/en/)

English | [中文](README_ZH.md)

## Contents

* [Introduction](#Introduction)
* [Preparations](#Preparations)
* [Environment Requirements](#Environment-Requirements)
* [Hardware Requirements](#Hardware-Requirements)
* [Installation](#Installation)
* [Technical Support](#Technical Support)
* [License](#License)

## Introduction

In this sample code, you will use the created code project to call the APIs of Honor Suggestions Kit. Through this project, you will:

1. Data feedback: Through the data feedback interface, application usage records and status information are provided to Honor Suggestions Kit service. Based on this data, the system's perception capabilities are improved and user feature tags are enriched to provide users with scenario-based service recommendations.

2. Activity status perception: The activity snapshot query interface is used to perceive the user's current activity status, thereby enabling third-party applications to present service content that is appropriate to the scenario.

For more information, please refer to
[Service Introduction](https://developer.honor.com/cn/docs/11003/guides/introduction)

## Environment Requirements

Android targetSdkVersion 30 or later and JDK 1.8 or later are recommended.

## Hardware Requirements

Honor phones or tablets with MagicOS 7.0 or above are used for business debugging.

## Preparations

1.	Register as a Honor developer.
2.	Create an app and start APIs.
3.  To build this sample code, you need to import it into the Android integrated development environment (Android Studio 4.0 and above is recommended). Then download the mcs-services.json file of the application from the [Honor Developer Site](https://developer.honor.com/) and add it to the root directory of the corresponding sample code. In addition, you need to generate a signature certificate fingerprint and add the certificate file to the project, and then add the configuration to build.gradle. For more information, see the [integration preparations](https://developer.honor.com/cn/docs/11003/guides/intergrate) Integration Preparation.         

## Installation

Method 1: Compile and build the APK in Android Studio. Then, install the APK on your phone and debug it.
Method 2: Generate the APK in Android Studio. Use the Android Debug Bridge (ADB) tool to run the **adb install {*YourPath/YourApp.apk*}** command to install the APK on your phone and debug it.

## Technical Support

If you have any questions about the sample code, try the following:
- Visit [Stack Overflow](https://stackoverflow.com/questions/tagged/honor-developer-services?tab=Votes), submit your questions, and tag them with Satellite Communication Kit. Honor experts will answer your questions.

If you encounter any issues when using the sample code, submit your [issues](https://github.com/HONORDevelopers/suggestionskit-demo/issues) or submit a [pull request](https://github.com/HONORDevelopers/suggestionskit-demo/pulls).

## License

The sample code is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).