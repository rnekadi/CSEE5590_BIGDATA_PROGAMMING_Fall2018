import org.apache.spark._
import org.apache.log4j._

object SparkWordCount  {

  def main(args: Array[String]): Unit = {


    //Controling log level

    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)


    //Spark Context
    val conf = new SparkConf().setAppName("SparkSample").setMaster("local[1]");
    val sc  = new SparkContext(conf);



    //Read the file
    val input = sc.textFile("/Users/sai/Desktop/CSEE5590_BIGDATA_F2018/SparkSample/input.txt");


    //Map Fuction
   var map = input.flatMap(line=>{line.split(" ")}).map(word => (word,1));

    //Reduce Function
    var count = map.reduceByKey(_ + _);

    // print
    count.collect().foreach(println)

    sc.stop();

  }


}
