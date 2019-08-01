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

        @JsonProperty("option")
        var option: Option? = null,

        @JsonProperty("scale")
        var scale: Scale? = null) {

        @JsonProperty("java_type")
        var javaType: String = sqlType.type
}


data class Scale(var size: Int, var precision: Int )

data class Option(val name: String, val value: String, val format: String?= null)

enum class SqlType(val type: String) {
    VARCHAR("String"),
    VARCHAR2("String"),
    CHAR("String"),
    NUMBER("Integer"),
    DATE("Date"),
    DATATIME("Date"),
    CLOB("String");

}
