package krud.template

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import krud.Column
import krud.SqlModel
import org.apache.commons.text.CaseUtils
import java.io.IOException


class JavaSignature : Helper<Column> {

    val name: String
        get() = "javaSig"

    @Throws(IOException::class)
    override fun apply(context: Column, options: Options): Any {
        val expr = ArrayList<String>()

        val scale = context.scale
        val table = options.context.parent().model() as SqlModel
        val javaBeanPropName = CaseUtils.toCamelCase(context.name, false, '_')

        if (table.validation) {
            if (context.javaType == "String") {
                expr.add(String.format("@Size(max=%s)", context.size))
            }

            if (context.javaType == "Date") {
                expr.add(String.format("@JsonFormat(shape = JsonFormat.Shape.NUMBER)"))
            }

            if (scale != null) {
                expr.add(String.format("@Digits(integer=%d, fraction=%d)",
                        context.scale!!.size,
                        context.scale!!.precision))

            }

            if (!context.nullable)
                expr.add(String.format("%s", if (context.javaType == "String") "@NotEmpty" else "@NotNull"))

        }



        if (scale != null) {

            when {
                scale.precision > 0 -> expr.add(String.format("private java.math.BigDecimal %s;", javaBeanPropName))
                scale.size > 12 -> expr.add(String.format("private Long %s;", javaBeanPropName))
                else -> expr.add(String.format("private Integer %s;", javaBeanPropName))
            }

        } else {
            expr.add(String.format("private %s %s;", if (context.javaType == "Date") "java.util.Date" else context.javaType, javaBeanPropName))
        }

        return  expr.joinToString(separator = "\n")


    }

    companion object {

        fun register(handlebars: Handlebars) {
            val js = JavaSignature()
            handlebars.registerHelper(js.name, js)
        }
    }
}
