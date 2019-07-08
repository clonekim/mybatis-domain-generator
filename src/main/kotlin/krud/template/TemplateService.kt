package krud.template

import com.github.jknack.handlebars.EscapingStrategy
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import krud.KtLog
import krud.Table
import org.springframework.stereotype.Component
import java.io.Writer

@Component
class TemplateService {

    companion object:KtLog()


    private val handlebars = Handlebars(ClassPathTemplateLoader("/templates").apply {
        suffix = ".hbs"
    }).with(EscapingStrategy.XML).apply {
        JavaSignature.register(this)
        CustomHelper.register(this)
        StringHelpers.register(this)


        for (h in ConditionalHelpers.values())
            registerHelper(h.name, h)
    }

    private val hbsMap by lazy { mapOf(
            "model" to handlebars.compile("javaModel"),
            "dao" to handlebars.compile("javaDao"),
            "controller" to handlebars.compile("javaController"),
            "vue" to handlebars.compile("vueData"),
            "mapper" to handlebars.compile("mybatisMapper")
    )}



    fun process(writer: Writer, table: Table, hbs: String) {
        log.debug("Processing ==> {}, Template => {}", table, hbs)
        table.keys = table.columns.filter { it.key }.map { it.name }
        table.updates = table.columns.filter { !it.key }
        table.keysize = table.keys.size
        hbsMap[hbs]?.apply(table, writer)
    }
}

