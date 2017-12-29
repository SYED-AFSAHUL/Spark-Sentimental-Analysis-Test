# Exploring key features of Apache Spark using Sentimental Analysis (Naive Bayes Classification)

Apache Spark is an open-source cluster-computing framework. It is much better than the othor options available at present.This is aattempt to test Apache Spark's key features with other setups. The features of Apache Spark that we are interested in are In-memory data abstraction, Partial DAG, Lineage base fault recovery, Data co-partition, Unification of Streaming, Batch and Interactive Processing and Hybrid Storage Architecture.

## Getting Started

You will need to have Apache Spark installed on your system to run Scala file. You can download it from [here](https://spark.apache.org/downloads.html). To get a better guide to install it on ubuntu, refer [here](https://medium.com/@josemarcialportilla/installing-scala-and-spark-on-ubuntu-5665ee4b62b1) 
You will need R Studio and R installed to run the R code. To install R studio refer [here](https://www.rstudio.com/products/rstudio/download/)

> checknb.scala is the Scala code for Naive Bayes Classification.

> finalnb.r is the R code for Naive Bayes Classification.

> checktrain.txt is the training dataset.

> checktest.txt is the testing dataset.

### Prerequisites

Things you need to install to run the code on Spark:
```
JAVA
```
```
Scala
```

Things you need to install to run the R code:

1) READR
```
install.packages("readr", INSTALL_opts = c('--no-lock'))
```
2) STRINGR
```
install.packages("stringr", INSTALL_opts = c('--no-lock'))
```
3) TOKENIZERS
```
install.packages("tokenizers", INSTALL_opts = c('--no-lock'))
```
Run the above three lines on R-Studio console.

## Authors

* **SYED AFSAHUL HAQUE** - [afsahulsyed](https://github.com/SYED-AFSAHUL).

* **SUMIT KALRA.** - *Guidance* - [Lemon](https://github.com/siriussky)
