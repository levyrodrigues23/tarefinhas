package br.com.levy.tarefinhas

data class Tarefa(val id: Long, val titulo: String, val feita: Boolean = false)

// Regras da lista como funções puras: fáceis de testar sem Android.
fun List<Tarefa>.adicionar(titulo: String, id: Long): List<Tarefa> {
    val limpo = titulo.replace('\n', ' ').trim()
    return if (limpo.isEmpty()) this else this + Tarefa(id, limpo)
}

fun List<Tarefa>.alternar(id: Long): List<Tarefa> =
    map { if (it.id == id) it.copy(feita = !it.feita) else it }

fun List<Tarefa>.remover(id: Long): List<Tarefa> = filterNot { it.id == id }

fun List<Tarefa>.limparFeitas(): List<Tarefa> = filterNot { it.feita }

// Uma tarefa por linha: "id|feita|titulo". O título vai por último, então pode conter "|".
fun List<Tarefa>.serializar(): String =
    joinToString("\n") { "${it.id}|${it.feita}|${it.titulo}" }

fun desserializar(texto: String): List<Tarefa> =
    texto.lineSequence().mapNotNull { linha ->
        val partes = linha.split("|", limit = 3)
        val id = partes.getOrNull(0)?.toLongOrNull() ?: return@mapNotNull null
        if (partes.size < 3) return@mapNotNull null
        Tarefa(id, partes[2], partes[1] == "true")
    }.toList()
