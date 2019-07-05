package krud

import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class KtLog(){
    var log: Logger = LoggerFactory.getLogger(
            this.javaClass.name.let {
                val matchIndex = it.length - 10
                when (it.lastIndexOf("\$Companion")) {
                    matchIndex -> it.substring(0, matchIndex)
                    else -> it
                }
            }
    )
}