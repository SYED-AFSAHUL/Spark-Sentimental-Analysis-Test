import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.sum
import org.apache.spark.sql._
//val sparkSession: SparkSession = ???
//import sparkSession.implicits._
//import org.apache.spark.sql.SparkSession.implicits._


//load dataset
val training = sc.textFile("checktrain.txt")


/*
//print raw dataset
print("\ntraining---\n\n")
training.foreach(println)
print("\n\n\ntest\n\n")
test.foreach(println)
*/

/*----------------- training starts ------------------------*/

//divide dataset to classes
val pos1 = training.filter(_.startsWith("__label__2"))
val neg1 = training.filter(_.startsWith("__label__1"))

val pos = pos1.map(line=>line.replaceAll("__label__2 ",""))
val neg = neg1.map(line=>line.replaceAll("__label__1 ",""))
/*
//print class dataset
print("\n\n\n+ve\n\n")
pos.collect().foreach(println)
print("\n\n\n-ve\n\n")
neg.collect().foreach(println)
*/
//make tf table
val pcounts = pos.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_ + _).toDF("term","+ve")
val ncounts = neg.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_ + _).toDF("termn","-ve")
/*
//print tf table
print("\n\n\n+ve\n\n")
pcounts.printSchema()
pcounts.collect().foreach(println)
print("\n\n\n-ve\n\n")
ncounts.printSchema()
ncounts.collect().foreach(println)
*/
//val w = Window.pcounts//partitionBy("_1").rowsBetween(Long.MinValue, Long.MaxValue)
//.val p = $"+ve" / sum($"+ve").over(w)

//val ddf = pcounts.agg(sum("+ve").alias("total")).join(pcounts, Seq("term"), "inner").withColumn("p", $"+ve" / $"total")
//ddf.collect().foreach(println)


val joined_df = pcounts.join(ncounts, pcounts.col("term") === ncounts.col("termn"),"FullOuter") //[term,+ve,termn,-ve]
//joined_df.printSchema()
//joined_df.collect().foreach(println)

/*
joined_df.map(row => {val row0 = row.getAs[String](0)
					  val row2 = row.getAs[String](2)
					  val make = if (row0 == "null") row2 else row0;
					  Row(make,row(1),row(3))}).collect().foreach(println)

*/

//total +ve and -ve terms
val a = pcounts.select(col("+ve")).rdd.map(_(0).asInstanceOf[Int]).reduce(_+_)  //sum(+ve)
val b = ncounts.select(col("-ve")).rdd.map(_(0).asInstanceOf[Int]).reduce(_+_)  //sum(-ve)



val joined_df1 = joined_df.withColumn("term", when(col("term").isNotNull,col("term")).otherwise(col("termn"))) 
val joined_df2 = joined_df1.drop("termn") 					//[term,+ve,-ve]

//remove null
val joined_df3 = joined_df2.withColumn("+ve", when(col("+ve").isNotNull, col("+ve") ).otherwise("0") )
val joined_df4 = joined_df3.withColumn("-ve", when(col("-ve").isNotNull, col("-ve")).otherwise("0") )

// calc P(x|+ve)
val joined_df5 = joined_df4.withColumn("PofXforPOS", col("+ve")/b )
val joined_df6 = joined_df5.withColumn("PofXforNEG", col("-ve")/a)       //[term,+ve,-ve,P(x|+ve),P(x|-ve)]

//joined_df6.printSchema()
//joined_df6.collect().foreach(println)

//calc P(x)
val joined_df7 = joined_df6.withColumn("P(x)",(( col("+ve") + col("-ve"))/(a+b) )) //[term,+ve,-ve,P(x|+ve),P(x|-ve), P(x)]
//println("with Px -----")
//joined_df7.printSchema()
//joined_df7.collect().foreach(println)

//val joined_df6 = joined_df5.withColumn("P(+ve)",( col("+ve")/(col("+ve") + col("-ve"))))
//val joined_df7 = joined_df6.withColumn("P(-ve)",( col("-ve")/(col("+ve") + col("-ve"))))

val Pp = a.toFloat/(a+b)
val Pn = b.toFloat/(a+b)
println("P(+ve) = n(+ve)/n(+ve + -ve). Same for -ve.")
println(Pp,Pn)

println("with P+ve & -ve -----")
//joined_df7.printSchema()
//joined_df7.collect().foreach(println)

//val df = spark.createDataFrame(z).toDF("stuff")
//print("\n\n\none\n\n")
//onedf.collect().foreach(println)


/*----------------- testing starts ------------------------*/


//load dataset
val test = sc.textFile("checktest.txt")

//divide dataset to classes
val tpos1 = training.filter(_.startsWith("__label__2"))
val tneg1 = training.filter(_.startsWith("__label__1"))

val tpos = tpos1.map(line=>line.replaceAll("__label__2 ",""))
val tneg = tneg1.map(line=>line.replaceAll("__label__1 ",""))
val q=0
tpos.collect().foreach{i => 
						print(i); 
						print("****************************************")
						//i.collect().foreach{j=>
							///print(j)
						//}
						val terms = i.split(" ")
						terms.foreach{j =>
										val present = joined_df7.filter(j)
										present.replaceAll("dcc","")
										
									 }
					  }
