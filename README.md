# Kotlin Algorand Android Wallet 

This solution will guide you in developing and deploying android application using the 
Algorand blockchain features that addresses the following use case:

* Account creation

* Funding accounts

* Transferring funds

* Getting account transactions

* Stateful and Stateless smart contract

The sample application here was done following Kotlin\Android development best practice, using MVVM and Couroutines for handling background task.

![Screens](https://github.com/gconnect/AlgorandAndroidWallet/blob/master/screens.png)

# Requirements

* Android studio setup

* Familiarity with the Java and Kotlin programming language and its use within the Android Environment.

* Basic understanding of some blockchain terminologies.

# Tools/ Libraries used
  This project was built following the MVVM design pattern. Below are some of the important libraries used:
  - Algorand SDK
  - Hilt
  - Retrofit
  - ViewModels and Coroutines
  - Databinding

# Setup Development Environment
To get started, your android studio should be up and running. To get the code on your android studio, simply click the clone button to clone the project or download the the project. Then from Android studio click on file and  select import to import the project from your local machine.

# Installation Guide

  To install the app, here is the link to the apk
  [KotlinAlgorandAndroidApp](https://github.com/gconnect/AlgorandAndroidWallet/blob/master/app-debug.apk) .
  
# How the app works
  After installation. This is what happens once any of the buttons are clicked;
  
  - Create account button : This will generate a new public key and Passphrase
  - Recover account button : This will will recover an existing account. For the purpose of this example a default recovery account has been provided. You can change it in the code as you deem fit.
  - Dashboard button : This will take you to the dashboard screen. This screen makes use of the recovery account. The dashboard displays the account balance and transactions of the recovery account. Also on the dashboard you can interact with the buttons to send and receive algo. You can also copy the address/public key using the copy icon.
  - Stateful Smart Contract : This is demonstrated in the code and console. Once the button is clicked a log of the response will be displayed. No UI is available for this.
  - Stateless Smart Contract : This is demonstrated in the code and console. Once the button is clicked a log of the response will be displayed. No UI is available for this.
   
# License
  Distributed under the MIT License. See [LICENSE](https://github.com/gconnect/AlgorandAndroidWallet/blob/master/LICENSE) for more information.
  
# Blog and Vide Tutorial
For more details you can checkout the blog post [here](https://developer.algorand.org/tutorials/creating-an-android-smart-contract-with-the-algorand-java-sdk-and-with-the-algorand-purestake-rest-api-using-kotlin/) and the video tutorial [here](https://www.youtube.com/watch?v=ToukVdeJhXU) .


# Disclaimer
 This project is not audited and should not be used in a production environment.
 

