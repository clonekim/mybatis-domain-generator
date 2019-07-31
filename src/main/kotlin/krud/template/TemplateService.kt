package krud.template

import com.github.jknack.handlebars.EscapingStrategy
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.ConditionalHelpers
import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import krud.KtLog
import krud.SqlModel
import org.springframework.stereotype.Component
import java.io.Writer

@Component
class TemplateService {

    companion object:KtLog()


    private val handlebars = Handlebars(ClassPathTemplateLoader("/templates").apply {
        suffix = ".hbs"
    }).with(EscapingStrategy.XML).with(EscapingStrategy.NOOP) .apply {
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
            "mapper" to handlebars.compile("mybatisMapper"),
            "sql" to handlebars.compile("sqlMapper")
    )}



    fun process(writer: Writer, sqlModel: SqlModel, hbs: String) {
        log.debug("Processing ==> {}, Template => {}", sqlModel, hbs)
        sqlModel.keys = sqlModel.columns.filter { it.key }.map { it.name }
        sqlModel.updates = sqlModel.columns.filter { !it.key }
        sqlModel.keysize = sqlModel.keys.size

        if(sqlModel.statement != null) {
            sqlModel.statement = sqlModel.statement!!
                    .replace("<", "&lg;")
                    .replace(">", "&gt;")
        }

        hbsMap[hbs]?.apply(sqlModel, writer)
    }
}

