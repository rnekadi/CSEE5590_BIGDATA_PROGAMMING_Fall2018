import java.io.IOException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

class KeyReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
  @Override
  protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
    int count = 0;
    int sum   = 0;
    int result = 0;
    for(IntWritable value : values){
      sum += value.get();
      count += 1;
    }
    result = sum / count;
    context.write(key, new IntWritable(result));
  }
}