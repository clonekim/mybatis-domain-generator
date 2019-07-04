package krud.template

import com.github.jknack.handlebars.EscapingStrategy
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import krud.Table
import java.io.Writer

object TemplateService {


    private val handlebars = Handlebars(ClassPathTemplateLoader("templates").apply {
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
            "vueData" to handlebars.compile("vueData"),
            "mybatisMapper" to handlebars.compile("mybatisMapper")
    )


    fun process(writer: Writer, table: Table, hbs: String) {
        hbsMap[hbs]?.apply(table, writer)
    }
}

