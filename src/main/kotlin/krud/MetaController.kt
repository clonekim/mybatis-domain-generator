package krud

import krud.template.TemplateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.BufferedWriter
import java.io.StringWriter
import java.sql.SQLException
import javax.validation.Valid

@RestController
class MetaController {

    @Autowired
    internal var metaReader: MetaReader? = null

    @Autowired
    var templateService: TemplateService? = null

    @PostMapping("/meta")
    fun scanMeta(@Valid @RequestBody schema: Schema): ResponseEntity<Any> {
        return try {
            val tables = metaReader!!.connect(schema)
            ResponseEntity.ok(tables.first())

        } catch(e: SQLException) {
            ResponseEntity.badRequest().body(
                    mapOf("message" to e.message,
                            "errorCode" to e.errorCode)
            )
        }
    }

    @PostMapping("/scaffold/{template}")
    fun createScaffold(@RequestBody table: Table, @PathVariable template: String ): String {
        val sw = StringWriter()
        BufferedWriter(sw).use { out ->

            templateService!!.process(
                    out,
                    table,
                    template
            )
        }

        return sw.toString()
    }
}