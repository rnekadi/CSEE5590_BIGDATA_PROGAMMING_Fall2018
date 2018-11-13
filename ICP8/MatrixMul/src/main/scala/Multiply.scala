import org.apache.spark._
import org.apache.spark.rdd.RDD
import scala.collection.mutable


object Multiply {

    def getSparkConf(local: Boolean): SparkConf = {
     if (local) new SparkConf().setMaster("local[*]").setAppName("Multiply")
      else new SparkConf().setAppName("Multiply")
  }

  def getFirstMatrix(sc: SparkContext): RDD[(Int, Elem)] = {
      sc.textFile("/Users/sai/Documents/GitHub/CSEE5590_BIGDATA_PROGAMMING_Fall2018/ICP8/MatrixMul/MatA.txt").
        map(line => {
        val a = line.split(",")
        (a(1).toInt, new Elem(0, a(0).toInt, a(2).toDouble))
      })

  }

  def getSecondMatrix(sc: SparkContext): RDD[(Int, Elem)] = {
      sc.textFile("/Users/sai/Documents/GitHub/CSEE5590_BIGDATA_PROGAMMING_Fall2018/ICP8/MatrixMul/MatB.txt").
        map(line => {val a = line.split(",")
          (a(1).toInt, new Elem(0, a(0).toInt, a(2).toDouble))
        })

  }


  def saveOutput(reducer2Output: RDD[(Pair, Double)]): Unit = {

      reducer2Output.saveAsTextFile("/Users/sai/Documents/GitHub/CSEE5590_BIGDATA_PROGAMMING_Fall2018/ICP8/MatrixMul/MatC")

  }

  var reducer1Output: Vector[(Pair, Double)] = Vector()

  def main(args: Array[String]): Unit = {
    val local = args.length == 0


    val conf = getSparkConf(local)
    val sc = new SparkContext(conf)
    val input1 = getFirstMatrix(sc)
    val input2 = getSecondMatrix(sc)

    val mapper1Output = (input1 union input2) coalesce 1


    mapper1Output.groupByKey().collect.foreach {
      case (_, values) =>
        val A = mutable.Buffer[Elem]()
        val B = mutable.Buffer[Elem]()

        values.foreach(value => {
          if (value.tag == 0)
            A.+=(value)
          if (value.tag == 1)
            B.+=(value)
        })

        A.foreach(aElem => {
          B.foreach(bElem => {
            val key = new Pair(aElem.index, bElem.index)
            val value = aElem.value * bElem.value
            reducer1Output = reducer1Output :+ (key, value)
          })
        })
    }

    val mapper2Output = sc parallelize reducer1Output
    val reducer2Output = mapper2Output reduceByKey (_ + _) coalesce 1 sortBy (pairDouble => (pairDouble._1.i, pairDouble._1.j))
    reducer2Output.collect.foreach { pairDouble =>
      println(s"${pairDouble._1},${pairDouble._2}")
    }
    saveOutput(reducer2Output)
    sc.stop()
  }


}

class Elem(var tag: Int, var index: Int, var value: Double) extends Serializable {
  override def toString = s"($tag, $index, $value)"
}

class Pair(var i: Int, var j: Int) extends Serializable {
  override def equals(obj: scala.Any): Boolean = obj match {
    case other: Pair => i == other.i && j == other.j
    case _ => false
  }

  override def hashCode(): Int = i.hashCode() * 37 + j.hashCode() * 13

  override def toString = s"$i,$j"
}
