package krud

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty


data class SqlModel(

        @JsonProperty("schema")
        var schema: String? = null,

        @JsonProperty("name")
        val name: String? = null,

        @JsonProperty("package_name")
        @NotEmpty
        val packageName: String,

        @JsonProperty("pojo_name")
        @NotEmpty
        val pojoName: String,

        @JsonProperty("validation")
        val validation: Boolean,

        @JsonProperty("dao")
        val useDao: Boolean = false,

        @JsonProperty("controller")
        val useController: Boolean = false,

        @JsonProperty("vue_data")
        val useVueData: Boolean = false,

        @JsonProperty("result_map")
        val useResultMap: Boolean = false,

        @JsonProperty("json_prefix")
        val jsonPrefix: String? = null,

        @JsonProperty("columns")
        var columns: List<Column> = mutableListOf(),

        @JsonIgnore
        var updates: List<Column> = mutableListOf(),

        @JsonIgnore
        var keys: List<String> = mutableListOf(),

        @JsonIgnore
        var keysize: Int = 0,

        @JsonProperty("statement")
        var statement: String? = null,

        @JsonProperty("sampling")
        val sampling: Boolean = false,

        @JsonProperty("samples")
        var samples: List<Any?> = mutableListOf<List<Any>>()
)



data class Column(
        @JsonProperty("name")
        var name: String,

        @JsonProperty("sql_type")
        var sqlType: SqlType,

        @JsonProperty("null")
        var nullable: Boolean = false,

        @JsonProperty("auto_increment")
        var autoincrement: Boolean = false,

        @JsonProperty("key")
        var key: Boolean = false,

        @JsonProperty("size")
        var size: Int,

        @JsonProperty("data_type")
        var dataType: Int,


        @JsonProperty("scale")
        var scale: Scale? = null) {


        @JsonProperty("java_type")
        var javaType: JavaType = DefaultJavaType.match(sqlType.type)
}


data class Scale(var size: Int, var precision: Int )

enum class SqlType(val type: String) {
    VARCHAR("String"),
    VARCHAR2("String"),
    CHAR("String"),
    NUMBER("Integer"),
    DATE("Date"),
    DATATIME("Date"),
    CLOB("String");

}

data class JavaType( val name: String, val value: String, val format: String?= null)


object DefaultJavaType {

    val STRING = JavaType("String", "String")
    val PRIME_INT =  JavaType("int", "int")
    val PRIME_LONG = JavaType("long", "long")
    val INTEGER = JavaType("Integer", "Integer")
    val LONG = JavaType("Long", "long")
    val DATE = JavaType("Date", "java.util.Date")
    val LOCALDATE = JavaType("LocalDate", "java.time.LocalDate", "yyyy-MM-dd")
    val LOCALDATE_TIME = JavaType("LocalDateTime", "java.time.LocalDateTime", "yyyy-MM-dd HH:mm:ss")

    private val javaTypes = listOf(
        STRING, PRIME_INT, PRIME_LONG, INTEGER, LONG, DATE, LOCALDATE, LOCALDATE_TIME
    )

    fun match (value: String ): JavaType {
        return javaTypes.first { it.name == value }
    }

}

