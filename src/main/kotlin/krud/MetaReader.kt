package krud

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.DatabaseMetaData
import java.sql.SQLException
import javax.sql.DataSource

@Component
class MetaReader {


    private inline fun <reified T> T.logger(): Logger {
        return LoggerFactory.getLogger(T::class.java)
    }

    val log = logger()

    @Autowired
    var dataSource: DataSource? = null

    @Throws(SQLException::class)
    fun connect(schema: Schema) : List<Table> {

        log.debug("Accept Schema ==> {}", schema)
        val targets = mutableListOf<Table>()

        dataSource!!.connection.use { conn ->

            val metaData = conn.metaData

            val resultSet = metaData.getTables(
                    schema.schema, null, null,
                    arrayOf("TABLE")
            )

            while (resultSet.next()) {
                val tableName = resultSet.getString("TABLE_NAME")

                for (table in schema.tables) {
                    if (tableName == table.name) {

                        readColumn(schema.schema, table, metaData)
                        targets.add(table)
                    }

                }
            }
        }

        return targets
    }


    fun readColumn(schemaName: String, table: Table, metaData: DatabaseMetaData) {
        val rs = metaData.getColumns(schemaName, null, table.name, null)

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
                    log.info("Replaced by default fallback type (String)")
                    SqlType.valueOf("VARCHAR")
                }

            )

            if(col.sqlType == SqlType.NUMBER && col.size > 0) {
                col.scale = Scale(col.size, rs.getInt("DECIMAL_DIGITS"))
            }

            table.columns += col
        }


    }


}