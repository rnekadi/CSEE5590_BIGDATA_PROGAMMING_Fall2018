import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.storage.StorageLevel


object LogStream {

  def main(args: Array[String]): Unit = {


    // Create a local StreamingContext with two working thread and batch interval of 1 second.
    // The master requires 2 cores to prevent a starvation scenario.

    val conf = new SparkConf().setMaster("local[2]").setAppName("LogStream")
    val ssc = new StreamingContext(conf, Seconds(5))

    val lines = ssc.textFileStream("Users/sai/Desktop/CSEE5590_BIGDATA_F2018/SparkSample/log")

    val words = lines.flatMap(_.split(" "))

    // Count each word in each batch
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()


  }

}
