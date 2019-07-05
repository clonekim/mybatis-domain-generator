package krud.template

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import org.apache.commons.text.CaseUtils
import java.io.IOException

enum class CustomHelper : Helper<Object> {

   firstCap {
       override fun toApply(value: Any, options: Options): String {
           val v = value.toString()
           return v.substring(0, 1).toLowerCase() + v.substring(1)
       }
   },

    javaName {
        override fun toApply(value: Any, options: Options): String {
            return CaseUtils.toCamelCase(value.toString(), false, '_')
        }

    },

    sharp {
        override fun toApply(value: Any, options: Options): String {
            return String.format("#{%s}", value.toString().toLowerCase())
        }
    };

    protected  abstract fun toApply(value: Any, options: Options): String

    @Throws(IOException::class)
    override fun apply(value: Object?, options: Options): Any? {
        return if(options.isFalsy(value)) {
            val param = options.param(0, null as Any?)
            param?.toString()
        } else {
            value?.let { this.toApply(it, options) }
        }
    }

    companion object {

        fun register(handlebars: Handlebars) {
            val values = values()
            for (h in values) {
                handlebars.registerHelper(h.name, h)
            }
        }
    }
}