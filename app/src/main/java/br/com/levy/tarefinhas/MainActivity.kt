package br.com.levy.tarefinhas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                TelaTarefas()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaTarefas(vm: TarefasViewModel = viewModel()) {
    val tarefas by vm.tarefas.collectAsState()
    var texto by rememberSaveable { mutableStateOf("") }
    val feitas = tarefas.count { it.feita }

    fun enviar() {
        vm.adicionar(texto)
        texto = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarefinhas") },
                actions = {
                    if (feitas > 0) TextButton(onClick = vm::limparFeitas) { Text("Limpar feitas") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = texto,
                        onValueChange = { texto = it },
                        label = { Text("Nova tarefa") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { enviar() }),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = ::enviar, enabled = texto.isNotBlank()) { Text("Adicionar") }
                }
                Text(
                    "$feitas de ${tarefas.size} feitas",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            if (tarefas.isEmpty()) {
                item { Text("Nenhuma tarefa ainda. Adicione a primeira acima.") }
            }
            items(tarefas, key = { it.id }) { tarefa ->
                LinhaTarefa(tarefa, onAlternar = { vm.alternar(tarefa.id) }, onRemover = { vm.remover(tarefa.id) })
            }
        }
    }
}

@Composable
fun LinhaTarefa(tarefa: Tarefa, onAlternar: () -> Unit, onRemover: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onAlternar),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = tarefa.feita, onCheckedChange = { onAlternar() })
        Text(
            tarefa.titulo,
            modifier = Modifier.weight(1f),
            textDecoration = if (tarefa.feita) TextDecoration.LineThrough else null
        )
        TextButton(onClick = onRemover) { Text("Excluir") }
    }
}
