package br.com.levy.tarefinhas

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TarefasViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("tarefas", Context.MODE_PRIVATE)

    private val _tarefas = MutableStateFlow(desserializar(prefs.getString(CHAVE, "") ?: ""))
    val tarefas: StateFlow<List<Tarefa>> = _tarefas.asStateFlow()

    fun adicionar(titulo: String) = atualizar { it.adicionar(titulo, System.currentTimeMillis()) }
    fun alternar(id: Long) = atualizar { it.alternar(id) }
    fun remover(id: Long) = atualizar { it.remover(id) }
    fun limparFeitas() = atualizar { it.limparFeitas() }

    private fun atualizar(mudanca: (List<Tarefa>) -> List<Tarefa>) {
        val nova = mudanca(_tarefas.value)
        _tarefas.value = nova
        prefs.edit { putString(CHAVE, nova.serializar()) }
    }

    private companion object {
        const val CHAVE = "lista"
    }
}
