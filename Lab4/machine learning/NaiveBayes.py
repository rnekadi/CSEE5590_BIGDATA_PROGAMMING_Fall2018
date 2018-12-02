# NaiveBayes on Abesentesim DataSet

from pyspark.ml.feature import VectorAssembler
from sklearn.metrics import confusion_matrix
from sklearn.metrics import precision_score
from sklearn.metrics import recall_score
from pyspark.ml.classification import NaiveBayes
from pyspark.ml.evaluation import MulticlassClassificationEvaluator


# Creating the Spark Session

from pyspark.sql import SparkSession
spark = SparkSession.builder.getOrCreate()
data = spark.read.load("/Users/sai/Documents/GitHub/ml_data/Absenteeism.csv", format="csv", header=True, delimiter=";")

data = data.withColumn("MOA", data["Month of absence"] - 0).withColumn("label", data['Seasons'] - 0). \
    withColumn("ROA", data["Reason for absence"] - 0). \
    withColumn("distance", data["Distance from Residence to Work"] - 0). \
    withColumn("BMI", data["Body mass index"] - 0)

assem = VectorAssembler(inputCols=["label", "MOA"], outputCol='features')

data = assem.transform(data)
# Split the data into train and test
splits = data.randomSplit([0.7, 0.3], 1000)
train = splits[0]
test = splits[1]

# create the trainer and set its parameters
nb = NaiveBayes(smoothing=1.0, modelType="multinomial")

# train the model
model = nb.fit(train)

# select example rows to display.
predictions = model.transform(test)

# compute accuracy on the test set
evaluator = MulticlassClassificationEvaluator(labelCol="label", predictionCol="prediction",
                                              metricName="accuracy")

y_true = data.select("BMI").rdd.flatMap(lambda x: x).collect()
y_pred = data.select("ROA").rdd.flatMap(lambda x: x).collect()


accuracy = evaluator.evaluate(predictions)

confusionmatrix = confusion_matrix(y_true, y_pred)

precision = precision_score(y_true, y_pred, average='micro')

recall = recall_score(y_true, y_pred, average='micro')


print("Naive Bayes - Test set accuracy = " + str(accuracy))

print("The Confusion Matrix for Naive Bayes Model is :\n" + str(confusionmatrix))

print("The precision score for Naive Bayes Model is: " + str(precision))

print("The recall score for Naive Bayes Model is: " + str(recall))
