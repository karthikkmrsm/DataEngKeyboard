package com.dataeng.keyboard;

import java.util.LinkedHashMap;
import java.util.Map;

public class TemplateData {

    public static final Map<String, String> TEMPLATES = new LinkedHashMap<>();

    static {
        TEMPLATES.put("Basic SELECT", 
            "SELECT\n" +
            "    col1,\n" +
            "    col2,\n" +
            "    col3\n" +
            "FROM schema.table_name\n" +
            "WHERE condition = 'value'\n" +
            "ORDER BY col1 ASC\n" +
            "LIMIT 100;");

        TEMPLATES.put("SELECT with JOIN",
            "SELECT\n" +
            "    a.id,\n" +
            "    a.name,\n" +
            "    b.value\n" +
            "FROM schema.table_a a\n" +
            "LEFT JOIN schema.table_b b\n" +
            "    ON a.id = b.a_id\n" +
            "WHERE a.status = 'active'\n" +
            "ORDER BY a.id DESC;");

        TEMPLATES.put("GROUP BY Aggregation",
            "SELECT\n" +
            "    department,\n" +
            "    COUNT(*) AS total_count,\n" +
            "    SUM(salary) AS total_salary,\n" +
            "    AVG(salary) AS avg_salary,\n" +
            "    MAX(salary) AS max_salary,\n" +
            "    MIN(salary) AS min_salary\n" +
            "FROM schema.employees\n" +
            "GROUP BY department\n" +
            "HAVING COUNT(*) > 5\n" +
            "ORDER BY total_salary DESC;");

        TEMPLATES.put("CTE (WITH clause)",
            "WITH cte_name AS (\n" +
            "    SELECT\n" +
            "        id,\n" +
            "        name,\n" +
            "        value\n" +
            "    FROM schema.source_table\n" +
            "    WHERE condition = true\n" +
            "),\n" +
            "cte_second AS (\n" +
            "    SELECT\n" +
            "        c.id,\n" +
            "        c.name,\n" +
            "        t.extra\n" +
            "    FROM cte_name c\n" +
            "    JOIN schema.other_table t ON c.id = t.id\n" +
            ")\n" +
            "SELECT * FROM cte_second\n" +
            "ORDER BY id;");

        TEMPLATES.put("ROW_NUMBER Window",
            "SELECT\n" +
            "    id,\n" +
            "    name,\n" +
            "    department,\n" +
            "    salary,\n" +
            "    ROW_NUMBER() OVER (\n" +
            "        PARTITION BY department\n" +
            "        ORDER BY salary DESC\n" +
            "    ) AS row_num\n" +
            "FROM schema.employees\n" +
            "QUALIFY row_num = 1;");

        TEMPLATES.put("RANK & DENSE_RANK",
            "SELECT\n" +
            "    id,\n" +
            "    name,\n" +
            "    department,\n" +
            "    salary,\n" +
            "    RANK() OVER (\n" +
            "        PARTITION BY department\n" +
            "        ORDER BY salary DESC\n" +
            "    ) AS rnk,\n" +
            "    DENSE_RANK() OVER (\n" +
            "        PARTITION BY department\n" +
            "        ORDER BY salary DESC\n" +
            "    ) AS dense_rnk,\n" +
            "    PERCENT_RANK() OVER (\n" +
            "        PARTITION BY department\n" +
            "        ORDER BY salary DESC\n" +
            "    ) AS pct_rank\n" +
            "FROM schema.employees;");

        TEMPLATES.put("LAG & LEAD",
            "SELECT\n" +
            "    id,\n" +
            "    date_col,\n" +
            "    value,\n" +
            "    LAG(value, 1, 0) OVER (\n" +
            "        PARTITION BY category\n" +
            "        ORDER BY date_col\n" +
            "    ) AS prev_value,\n" +
            "    LEAD(value, 1, 0) OVER (\n" +
            "        PARTITION BY category\n" +
            "        ORDER BY date_col\n" +
            "    ) AS next_value,\n" +
            "    value - LAG(value, 1, 0) OVER (\n" +
            "        PARTITION BY category\n" +
            "        ORDER BY date_col\n" +
            "    ) AS delta\n" +
            "FROM schema.metrics;");

        TEMPLATES.put("Running Total (SUM)",
            "SELECT\n" +
            "    id,\n" +
            "    date_col,\n" +
            "    amount,\n" +
            "    SUM(amount) OVER (\n" +
            "        PARTITION BY category\n" +
            "        ORDER BY date_col\n" +
            "        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW\n" +
            "    ) AS running_total,\n" +
            "    AVG(amount) OVER (\n" +
            "        PARTITION BY category\n" +
            "        ORDER BY date_col\n" +
            "        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW\n" +
            "    ) AS rolling_7day_avg\n" +
            "FROM schema.sales;");

        TEMPLATES.put("FIRST_VALUE & LAST_VALUE",
            "SELECT\n" +
            "    id,\n" +
            "    department,\n" +
            "    salary,\n" +
            "    FIRST_VALUE(salary) OVER (\n" +
            "        PARTITION BY department\n" +
            "        ORDER BY salary DESC\n" +
            "        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING\n" +
            "    ) AS highest_salary,\n" +
            "    LAST_VALUE(salary) OVER (\n" +
            "        PARTITION BY department\n" +
            "        ORDER BY salary DESC\n" +
            "        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING\n" +
            "    ) AS lowest_salary\n" +
            "FROM schema.employees;");

        TEMPLATES.put("NTILE Quartiles",
            "SELECT\n" +
            "    id,\n" +
            "    name,\n" +
            "    score,\n" +
            "    NTILE(4) OVER (\n" +
            "        ORDER BY score DESC\n" +
            "    ) AS quartile,\n" +
            "    NTILE(10) OVER (\n" +
            "        ORDER BY score DESC\n" +
            "    ) AS decile,\n" +
            "    CUME_DIST() OVER (\n" +
            "        ORDER BY score\n" +
            "    ) AS cumulative_dist\n" +
            "FROM schema.scores;");

        TEMPLATES.put("All Window Functions",
            "SELECT\n" +
            "    id,\n" +
            "    name,\n" +
            "    department,\n" +
            "    salary,\n" +
            "    date_col,\n" +
            "    -- Ranking functions\n" +
            "    ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) AS row_num,\n" +
            "    RANK()       OVER (PARTITION BY department ORDER BY salary DESC) AS rnk,\n" +
            "    DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS dense_rnk,\n" +
            "    NTILE(4)     OVER (PARTITION BY department ORDER BY salary DESC) AS quartile,\n" +
            "    PERCENT_RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS pct_rank,\n" +
            "    CUME_DIST()    OVER (PARTITION BY department ORDER BY salary DESC) AS cum_dist,\n" +
            "    -- Offset functions\n" +
            "    LAG(salary, 1)  OVER (PARTITION BY department ORDER BY date_col) AS prev_salary,\n" +
            "    LEAD(salary, 1) OVER (PARTITION BY department ORDER BY date_col) AS next_salary,\n" +
            "    FIRST_VALUE(salary) OVER (PARTITION BY department ORDER BY salary DESC\n" +
            "        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS max_in_dept,\n" +
            "    LAST_VALUE(salary)  OVER (PARTITION BY department ORDER BY salary DESC\n" +
            "        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS min_in_dept,\n" +
            "    -- Aggregate window functions\n" +
            "    SUM(salary)   OVER (PARTITION BY department ORDER BY date_col\n" +
            "        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS running_sum,\n" +
            "    AVG(salary)   OVER (PARTITION BY department ORDER BY date_col\n" +
            "        ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS rolling_avg_3,\n" +
            "    COUNT(*)      OVER (PARTITION BY department) AS dept_count,\n" +
            "    MAX(salary)   OVER (PARTITION BY department) AS dept_max,\n" +
            "    MIN(salary)   OVER (PARTITION BY department) AS dept_min\n" +
            "FROM schema.employees\n" +
            "ORDER BY department, row_num;");

        TEMPLATES.put("Delta MERGE INTO",
            "MERGE INTO target_table AS t\n" +
            "USING (\n" +
            "    SELECT\n" +
            "        id,\n" +
            "        name,\n" +
            "        value,\n" +
            "        updated_at\n" +
            "    FROM source_table\n" +
            ") AS s\n" +
            "ON t.id = s.id\n" +
            "WHEN MATCHED AND s.updated_at > t.updated_at THEN\n" +
            "    UPDATE SET\n" +
            "        t.name = s.name,\n" +
            "        t.value = s.value,\n" +
            "        t.updated_at = s.updated_at\n" +
            "WHEN NOT MATCHED THEN\n" +
            "    INSERT (id, name, value, updated_at)\n" +
            "    VALUES (s.id, s.name, s.value, s.updated_at);");

        TEMPLATES.put("PySpark Read & Transform",
            "from pyspark.sql import SparkSession\n" +
            "from pyspark.sql import functions as F\n" +
            "from pyspark.sql.window import Window\n\n" +
            "spark = SparkSession.builder.appName('MyApp').getOrCreate()\n\n" +
            "# Read data\n" +
            "df = spark.read.format('delta').load('/path/to/table')\n\n" +
            "# Transform\n" +
            "window = Window.partitionBy('department').orderBy(F.col('salary').desc())\n\n" +
            "result = (\n" +
            "    df\n" +
            "    .filter(F.col('status') == 'active')\n" +
            "    .withColumn('row_num', F.row_number().over(window))\n" +
            "    .withColumn('running_sum', F.sum('salary').over(window))\n" +
            "    .groupBy('department')\n" +
            "    .agg(\n" +
            "        F.count('*').alias('total'),\n" +
            "        F.avg('salary').alias('avg_salary'),\n" +
            "        F.max('salary').alias('max_salary')\n" +
            "    )\n" +
            "    .orderBy('department')\n" +
            ")\n\n" +
            "result.show()\n" +
            "result.write.format('delta').mode('overwrite').saveAsTable('output_table')");

        TEMPLATES.put("CASE WHEN",
            "SELECT\n" +
            "    id,\n" +
            "    salary,\n" +
            "    CASE\n" +
            "        WHEN salary >= 100000 THEN 'High'\n" +
            "        WHEN salary >= 60000  THEN 'Medium'\n" +
            "        WHEN salary >= 30000  THEN 'Low'\n" +
            "        ELSE 'Entry'\n" +
            "    END AS salary_band,\n" +
            "    CASE department\n" +
            "        WHEN 'Engineering' THEN 'Tech'\n" +
            "        WHEN 'Sales'       THEN 'Revenue'\n" +
            "        ELSE 'Other'\n" +
            "    END AS dept_group\n" +
            "FROM schema.employees;");
    }

    public static String[] getNames() {
        return TEMPLATES.keySet().toArray(new String[0]);
    }

    public static String getTemplate(String name) {
        return TEMPLATES.getOrDefault(name, "");
    }
}
