package krud

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList
import javax.sql.DataSource
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

@Component
class MetaReader {

    companion object:KtLog()

    @Autowired
    var dataSource: DataSource? = null


    @Throws(SQLException::class)
    fun connect(sqlModel: SqlModel) : SqlModel {

        dataSource!!.connection.use { conn ->

            val metaData = conn.metaData

            val resultSet = metaData.getTables(
                    sqlModel.schema, null, sqlModel.name,
                    arrayOf("TABLE")
            )

            while (resultSet.next()) {
                readColumn(sqlModel, metaData)
            }
        }

        return sqlModel
    }


    fun readColumn(sqlModel: SqlModel, metaData: DatabaseMetaData) {
        val rs = metaData.getColumns(sqlModel.schema, null, sqlModel.name, null)

        while (rs.next()) {


            val col = Column(
                name = rs.getString("COLUMN_NAME"),
                size = rs.getInt("COLUMN_SIZE"),
                nullable = rs.getBoolean("NULLABLE"),
                dataType = rs.getInt("DATA_TYPE"),
                autoincrement = rs.getBoolean("IS_AUTOINCREMENT"),
                sqlType = try {
                    SqlType.valueOf(rs.getString("TYPE_NAME"))
                } catch (e: java.lang.IllegalArgumentException) {
                    log.info("Replaced by default fallback type (String) => DATA_TYPE({}) TYPE_NAME({})", rs.getInt("DATA_TYPE"), rs.getString("TYPE_NAME"))
                    SqlType.valueOf("VARCHAR")
                }

            )

            if(col.sqlType == SqlType.NUMBER && col.size > 0) {
                col.scale = Scale(col.size, rs.getInt("DECIMAL_DIGITS"))
            }

            sqlModel.columns += col
        }
    }



    fun validateSQL(@Valid @NotEmpty statement: String?): String {

        return statement!!
                .replace("\\s+".toRegex(), " ")
                .replace("INSERT ", "<INSERT>", true)
                .replace("UPDATE ", "<UPDATE>", true)
                .replace("DELETE ", "<DELETE>", true)
    }

    fun runSQL(sqlModel: SqlModel) :SqlModel {


        dataSource!!.connection.use { conn ->

            log.debug("Run SQL ==> {}", sqlModel.statement)
            val query = validateSQL(sqlModel.statement)

            log.debug("After Validated SQL ==> {}", query)

            val statement = conn.createStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
            statement.fetchSize = 20

            val  resultSet = statement.executeQuery( query)
            resultSet.fetchSize =20

            val metaData = resultSet.metaData



            (0 until metaData.columnCount).forEachIndexed { index, _ ->
               val col = Column(
                    name = metaData.getColumnName(index + 1),
                    size = metaData.getColumnDisplaySize(index + 1),
                    nullable =  metaData.isNullable(index + 1) > 0,
                    dataType = metaData.getColumnType(index + 1),
                    autoincrement = metaData.isAutoIncrement(index + 1),
                    sqlType = SqlType.valueOf(metaData.getColumnTypeName(index + 1)?: "VARCHAR")
               )

                if(col.sqlType == SqlType.NUMBER && col.size > 0) {
                    col.scale = Scale(col.size, metaData.getPrecision(index + 1))
                }

                sqlModel.columns += col

            }

            //샘플링 데이터 수집
            if(sqlModel.sampling) {
                var virtualNum = 1
                var count = 20
                var rows = mutableListOf<List<Map<String, Any>>>()

                while(resultSet.next()) {

                    log.debug("sampling...")
                    var row: List<Map<String, Any>> = mutableListOf()

                    (0 until metaData.columnCount).forEachIndexed { index, _ ->

                        row += mapOf(
                            "column" to metaData.getColumnName(index + 1),
                            "value" to (resultSet.getObject(index + 1)?: "NULL") as Object
                        )

                    }

                    var row2: List<Map<String, Any>> = mutableListOf()

                    //가상 컬럼 번호
                    row2 += (mapOf(
                            "column" to "NUM",
                            "value" to virtualNum ++))

                    row2 += row

                    rows.add(row2)
                    count--

                    if(count ==0)
                        break
                }



                sqlModel.samples +=  mutableListOf<Any>(rows.map {
                        it.map{ m -> m["column"]}
                    }.first())


                sqlModel.samples += rows.map {
                    it.map{ m -> m["value"]}
                }


            }


        }



        return sqlModel
    }


}