package br.com.levy.tarefinhas

import org.junit.Assert.assertEquals
import org.junit.Test

class TarefaTest {

    @Test
    fun adicionarIgnoraTituloVazio() {
        assertEquals(emptyList<Tarefa>(), emptyList<Tarefa>().adicionar("   ", 1))
    }

    @Test
    fun alternarERemover() {
        val lista = emptyList<Tarefa>().adicionar("estudar", 1).adicionar("treinar", 2)
        val alternada = lista.alternar(1)
        assertEquals(true, alternada[0].feita)
        assertEquals(listOf(Tarefa(2, "treinar")), alternada.limparFeitas())
        assertEquals(listOf(Tarefa(1, "estudar")), lista.remover(2))
    }

    @Test
    fun serializacaoIdaEVolta() {
        val lista = listOf(Tarefa(1, "a|b com barra", true), Tarefa(2, "comprar pão"))
        assertEquals(lista, desserializar(lista.serializar()))
        assertEquals(emptyList<Tarefa>(), desserializar(""))
    }
}
