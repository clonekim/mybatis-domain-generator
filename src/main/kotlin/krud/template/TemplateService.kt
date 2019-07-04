package krud.template

import com.github.jknack.handlebars.EscapingStrategy
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import krud.Table
import java.io.Writer

object TemplateService {


    private val handlebars = Handlebars(ClassPathTemplateLoader("/templates").apply {
        suffix = ".hbs"
    }).with(EscapingStrategy.XML).apply {
        JavaSignature.register(this)
        CustomHelper.register(this)
        StringHelpers.register(this)

        for (h in ConditionalHelpers.values())
            registerHelper(h.name, h)
    }

    private val hbsMap = mapOf(
            "model" to handlebars.compile("javaModel"),
            "dao" to handlebars.compile("javaDao"),
            "controller" to handlebars.compile("javaController"),
            "vue" to handlebars.compile("vueData"),
            "mapper" to handlebars.compile("mybatisMapper")
    )


    fun process(writer: Writer, table: Table, hbs: String) {
        table.keys = table.columns.filter { it.key }.map { it.name }
        table.updates = table.columns.filter { !it.key }
        hbsMap[hbs]?.apply(table, writer)
    }
}

