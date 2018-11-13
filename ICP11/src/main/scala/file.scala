import scala.io.Source
import java.io.File
import java.io.PrintWriter

object file {

  def main(args: Array[String]): Unit = {

    val Ifilename = "/Users/sai/Desktop/CSEE5590_BIGDATA_F2018/SparkSample/loerum.txt"


    val writer = new PrintWriter(new File("/Users/sai/Desktop/CSEE5590_BIGDATA_F2018/" +
      "SparkSample/log.txt"))

        for (line <- Source.fromFile(Ifilename).getLines) {


          writer.write(line)

          Thread.sleep(50000)

      }

          writer.close()


      }

}
