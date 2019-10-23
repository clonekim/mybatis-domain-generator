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

    companion object : KtLog()

    @Autowired
    internal var metaReader: MetaReader? = null

    @Autowired
    var templateService: TemplateService? = null


    @Autowired
    lateinit var editConfig: EditConfig

    @GetMapping("/editConfig")
    fun initPage(): ResponseEntity<Any> {
        return ResponseEntity.ok(
                mapOf("schema" to editConfig.schema,
                        "packageName" to editConfig.packageName)
        )
    }


    @PostMapping("/meta")
    fun scanMeta(@Valid @RequestBody sqlModel: SqlModel): ResponseEntity<Any> {

        log.debug("[ Request ] => {}", sqlModel)

        return try {
            ResponseEntity.ok(
                    metaReader!!.run {

                        if (sqlModel.statement == null)
                            this.connect(sqlModel)
                        else
                            this.runSQL(sqlModel)
                    })


        } catch (e: SQLException) {
            ResponseEntity.badRequest().body(
                    mapOf("message" to e.message,
                            "errorCode" to e.errorCode)
            )
        }
    }

    @PostMapping("/source/{format}")
    fun createScaffold(@RequestBody sqlModel: SqlModel, @PathVariable format: String): String {
        log.debug("[ Generating source format ] => {}", format)
        val sw = StringWriter()
        BufferedWriter(sw).use { out ->

            templateService!!.process(
                    out,
                    sqlModel,
                    format
            )
        }

        return sw.toString()
    }


}