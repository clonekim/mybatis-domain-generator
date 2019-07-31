package krud

fun main() {

    val stmt: String = """
         INSERT INTO (
         
            UPDATE AA SET A=3
         )  INSERT
         
         insert
        
    """.trimIndent()


    val s = stmt.replace("INSERT", "***", true).replace("\\s".toRegex(), " ")

    println(s)
}