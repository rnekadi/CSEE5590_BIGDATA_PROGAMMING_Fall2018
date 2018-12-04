# Import Model

from pyspark.ml.regression import LinearRegression

# Load training data
from pyspark.sql import SparkSession
spark =SparkSession.builder.appName('Auto').getOrCreate()

training = spark.read.csv("/Users/sai/Documents/GitHub/CSEE5590_BIGDATA_PROGAMMING_Fall2018/ICP14/auto.csv",
                           header=True, inferSchema="true")

training.cache()


training.printSchema()

training.dropna()

from pyspark.ml.linalg import Vectors
from pyspark.ml.feature import VectorAssembler


featureAssembler = VectorAssembler(inputCols=["wheel-base","length","width","height", "curb-weight",  "engine-size",
                                               "compression-ratio",  "city-mpg", "highway-mpg"],
                                                outputCol='features')



output = featureAssembler.transform(training)

splits = output.randomSplit([0.7, 0.3])
train_df = splits[0]
test_df = splits[1]


lr = LinearRegression(featuresCol = 'features', labelCol='price', maxIter=10, regParam=0.3, elasticNetParam=0.8)
lr_model = lr.fit(train_df)
print("Coefficients: " + str(lr_model.coefficients))
print("Intercept: " + str(lr_model.intercept))


trainingSummary = lr_model.summary
print("RMSE: %f" % trainingSummary.rootMeanSquaredError)
print("r2: %f" % trainingSummary.r2)




# # Fit the model
# lrModel = lr.fit(training)
#
# # Print the coefficients and intercept for linear regression
# print("Coefficients: %s" % str(lrModel.coefficients))
# print("Intercept: %s" % str(lrModel.intercept))
#
# # Summarize the model over the training set and print out some metrics
# trainingSummary = lrModel.summary
# print("numIterations: %d" % trainingSummary.totalIterations)
# print("objectiveHistory: %s" % str(trainingSummary.objectiveHistory))
# trainingSummary.residuals.show()
# print("RMSE: %f" % trainingSummary.rootMeanSquaredError)
# print("r2: %f" % trainingSummary.r2)