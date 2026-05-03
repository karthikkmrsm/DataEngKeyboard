package com.dataeng.keyboard;

import java.util.*;

public class KeywordData {

    public static final String TYPE_SQL   = "sql";
    public static final String TYPE_SPARK = "spark";
    public static final String TYPE_DB    = "db";

    // ── SQL ──────────────────────────────────────────────────────────────────
    public static final Map<String, String[]> SQL = new LinkedHashMap<>();
    static {
        SQL.put("Clauses", new String[]{
            "SELECT","FROM","WHERE","GROUP BY","ORDER BY","HAVING","LIMIT",
            "OFFSET","JOIN","LEFT JOIN","RIGHT JOIN","FULL JOIN","INNER JOIN",
            "CROSS JOIN","ON","AS","DISTINCT","TOP","UNION","UNION ALL",
            "EXCEPT","INTERSECT","WITH","LATERAL"
        });
        SQL.put("Functions", new String[]{
            "COUNT(*)","SUM()","AVG()","MAX()","MIN()","COALESCE()","NULLIF()",
            "ISNULL()","IFNULL()","NVL()","CAST()","CONVERT()","LEN()","LENGTH()",
            "SUBSTR()","SUBSTRING()","TRIM()","LTRIM()","RTRIM()","UPPER()","LOWER()",
            "REPLACE()","CONCAT()","ROUND()","FLOOR()","CEIL()","ABS()","MOD()",
            "POWER()","SQRT()","NOW()","GETDATE()","SYSDATE","CURRENT_DATE",
            "DATEADD()","DATEDIFF()","DATE_FORMAT()","EXTRACT()","TO_DATE()","TO_CHAR()"
        });
        SQL.put("Window", new String[]{
            "ROW_NUMBER()","RANK()","DENSE_RANK()","NTILE()","LAG()","LEAD()",
            "FIRST_VALUE()","LAST_VALUE()","PERCENT_RANK()","CUME_DIST()","OVER()",
            "PARTITION BY","ROWS BETWEEN","RANGE BETWEEN","UNBOUNDED PRECEDING",
            "CURRENT ROW","UNBOUNDED FOLLOWING"
        });
        SQL.put("DDL", new String[]{
            "CREATE TABLE","ALTER TABLE","DROP TABLE","TRUNCATE","INSERT INTO",
            "VALUES ()","UPDATE","SET","DELETE FROM","CREATE INDEX","CREATE VIEW",
            "CREATE SCHEMA","PRIMARY KEY","FOREIGN KEY","REFERENCES","NOT NULL",
            "DEFAULT","UNIQUE","CHECK","AUTO_INCREMENT"
        });
        SQL.put("Predicates", new String[]{
            "AND","OR","NOT","IN ()","NOT IN ()","BETWEEN","LIKE","NOT LIKE",
            "IS NULL","IS NOT NULL","EXISTS","NOT EXISTS","ANY","ALL",
            "CASE WHEN","THEN","ELSE","END","IF()","IIF()","ESCAPE"
        });
        SQL.put("CTE / Adv", new String[]{
            "WITH cte AS ()","RECURSIVE","MATERIALIZED","WITH ROLLUP","WITH CUBE",
            "GROUPING SETS","PIVOT","UNPIVOT","TABLESAMPLE","FOR JSON","FOR XML",
            "EXPLAIN","ANALYZE","VACUUM","REINDEX"
        });
    }

    // ── PYSPARK ──────────────────────────────────────────────────────────────
    public static final Map<String, String[]> SPARK = new LinkedHashMap<>();
    static {
        SPARK.put("Session", new String[]{
            "SparkSession.builder","spark.read","spark.sql()","spark.table()",
            "spark.range()","spark.createDataFrame()","spark.catalog","spark.conf.set()",
            "spark.conf.get()","spark.stop()","spark.sparkContext","spark.version",
            "getOrCreate()","appName()","master()","config()"
        });
        SPARK.put("DataFrame", new String[]{
            "df.show()","df.printSchema()","df.describe()","df.count()","df.collect()",
            "df.take()","df.first()","df.head()","df.cache()","df.persist()",
            "df.unpersist()","df.schema","df.columns","df.dtypes","df.rdd",
            "df.toDF()","df.toPandas()","df.isEmpty()","df.explain()","df.checkpoint()"
        });
        SPARK.put("Transform", new String[]{
            "df.select()","df.filter()","df.where()","df.withColumn()","df.drop()",
            "df.alias()","df.distinct()","df.dropDuplicates()","df.orderBy()","df.sort()",
            "df.limit()","df.sample()","df.randomSplit()","df.repartition()","df.coalesce()",
            "df.groupBy()","df.agg()","df.pivot()","df.join()","df.union()",
            "df.unionByName()","df.crossJoin()","df.withColumnRenamed()",
            "df.na.drop()","df.na.fill()","df.na.replace()","df.stat.corr()"
        });
        SPARK.put("Functions", new String[]{
            "F.col()","F.lit()","F.when()","F.otherwise()","F.isNull()","F.isNotNull()",
            "F.coalesce()","F.concat()","F.concat_ws()","F.split()","F.regexp_replace()",
            "F.regexp_extract()","F.trim()","F.lower()","F.upper()","F.length()",
            "F.substring()","F.lpad()","F.rpad()","F.to_date()","F.to_timestamp()",
            "F.date_format()","F.datediff()","F.months_between()","F.year()","F.month()",
            "F.dayofmonth()","F.hour()","F.minute()","F.round()","F.floor()","F.ceil()",
            "F.abs()","F.sqrt()","F.log()","F.pow()","F.row_number()","F.rank()",
            "F.dense_rank()","F.lag()","F.lead()","F.sum()","F.avg()","F.count()",
            "F.countDistinct()","F.max()","F.min()","F.collect_list()","F.collect_set()",
            "F.explode()","F.explode_outer()","F.posexplode()","F.flatten()","F.array()",
            "F.array_contains()","F.array_distinct()","F.map_keys()","F.map_values()",
            "F.struct()","F.udf()","F.pandas_udf()","F.broadcast()"
        });
        SPARK.put("I/O", new String[]{
            "spark.read.csv()","spark.read.parquet()","spark.read.json()","spark.read.orc()",
            "spark.read.delta()","spark.read.jdbc()","spark.read.option()","spark.read.options()",
            "spark.read.schema()","df.write.csv()","df.write.parquet()","df.write.json()",
            "df.write.orc()","df.write.delta()","df.write.jdbc()","df.write.mode(\"overwrite\")",
            "df.write.mode(\"append\")","df.write.partitionBy()","df.write.bucketBy()",
            "df.write.saveAsTable()","df.write.save()"
        });
        SPARK.put("Window", new String[]{
            "Window.partitionBy()","Window.orderBy()","Window.rowsBetween()",
            "Window.rangeBetween()","Window.unboundedPreceding","Window.unboundedFollowing",
            "Window.currentRow"
        });
        SPARK.put("Imports", new String[]{
            "from pyspark.sql import SparkSession",
            "from pyspark.sql import functions as F",
            "from pyspark.sql.types import *",
            "from pyspark.sql.window import Window",
            "from pyspark.sql.types import StructType",
            "from pyspark.sql.types import StructField",
            "from pyspark.sql.types import StringType",
            "from pyspark.sql.types import IntegerType",
            "from pyspark.sql.types import LongType",
            "from pyspark.sql.types import DoubleType",
            "from pyspark.sql.types import BooleanType",
            "from pyspark.sql.types import DateType",
            "from pyspark.sql.types import TimestampType",
            "from pyspark.sql.types import ArrayType",
            "from pyspark.sql.types import MapType"
        });
    }

    // ── DATABRICKS ───────────────────────────────────────────────────────────
    public static final Map<String, String[]> DB = new LinkedHashMap<>();
    static {
        DB.put("Delta", new String[]{
            "DELTA TABLE","CREATE TABLE USING DELTA","OPTIMIZE","VACUUM","DESCRIBE HISTORY",
            "RESTORE TABLE TO VERSION","CLONE TABLE","MERGE INTO","WHEN MATCHED THEN",
            "WHEN NOT MATCHED THEN","USING (SELECT)","Z-ORDER BY","ALTER TABLE ADD COLUMNS",
            "CONVERT TO DELTA","DeltaTable.forPath()","DeltaTable.forName()",
            "deltaTable.update()","deltaTable.delete()","deltaTable.merge()",
            "deltaTable.history()","deltaTable.vacuum()","deltaTable.optimize()",
            "df.write.format(\"delta\")","GENERATE SYMLINK_FORMAT_MANIFEST","DESCRIBE DETAIL"
        });
        DB.put("Unity", new String[]{
            "USE CATALOG","USE SCHEMA","SHOW CATALOGS","SHOW SCHEMAS","SHOW TABLES",
            "DESCRIBE TABLE","CREATE CATALOG","CREATE SCHEMA","GRANT","REVOKE",
            "SHOW GRANTS","ALTER CATALOG","INFORMATION_SCHEMA","spark.catalog.listTables()",
            "spark.catalog.listColumns()","spark.catalog.tableExists()",
            "spark.catalog.setCurrentCatalog()","spark.catalog.currentCatalog()"
        });
        DB.put("dbutils", new String[]{
            "dbutils.fs.ls()","dbutils.fs.cp()","dbutils.fs.mv()","dbutils.fs.rm()",
            "dbutils.fs.mkdirs()","dbutils.fs.put()","dbutils.fs.head()",
            "dbutils.secrets.get()","dbutils.secrets.listScopes()",
            "dbutils.notebook.run()","dbutils.notebook.exit()",
            "dbutils.widgets.get()","dbutils.widgets.text()","dbutils.widgets.dropdown()",
            "dbutils.widgets.combobox()","dbutils.widgets.multiselect()",
            "dbutils.library.restartPython()"
        });
        DB.put("Compute", new String[]{
            "%python","%sql","%scala","%r","%sh","%md","%run","%pip install",
            "display()","displayHTML()","spark.conf.set()","AutoLoader",
            "readStream.format()","writeStream.format()","trigger(once=True)",
            "trigger(availableNow=True)","checkpointLocation","outputMode(\"append\")",
            "outputMode(\"complete\")","outputMode(\"update\")","foreachBatch()",
            "awaitTermination()"
        });
        DB.put("MLflow", new String[]{
            "import mlflow","mlflow.start_run()","mlflow.end_run()","mlflow.log_param()",
            "mlflow.log_params()","mlflow.log_metric()","mlflow.log_metrics()",
            "mlflow.log_artifact()","mlflow.set_experiment()","mlflow.set_tag()",
            "mlflow.sklearn.log_model()","mlflow.spark.log_model()",
            "mlflow.pyfunc.load_model()","mlflow.register_model()","mlflow.search_runs()",
            "mlflow.get_experiment()","MlflowClient()","FeatureStoreClient()",
            "fs.create_table()","fs.write_table()","fs.read_table()","fs.get_table()"
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    public static String getType(String kw) {
        for (String[] arr : SQL.values())   for (String s : arr) if (s.equals(kw)) return TYPE_SQL;
        for (String[] arr : SPARK.values()) for (String s : arr) if (s.equals(kw)) return TYPE_SPARK;
        for (String[] arr : DB.values())    for (String s : arr) if (s.equals(kw)) return TYPE_DB;
        return TYPE_SQL;
    }

    public static List<String> search(String query, String mode) {
        query = query.toLowerCase(Locale.ROOT);
        List<String> results = new ArrayList<>();
        Map<String, String[]>[] pools;
        if ("sql".equals(mode)) {
            pools = new Map[]{SQL};
        } else if ("spark".equals(mode)) {
            pools = new Map[]{SPARK};
        } else if ("db".equals(mode)) {
            pools = new Map[]{DB};
        } else {
            pools = new Map[]{SQL, SPARK, DB};
        }
        for (Map<String, String[]> pool : pools) {
            for (String[] arr : pool.values()) {
                for (String kw : arr) {
                    if (kw.toLowerCase(Locale.ROOT).contains(query) && results.size() < 15) {
                        results.add(kw);
                    }
                }
            }
        }
        return results;
    }
}
