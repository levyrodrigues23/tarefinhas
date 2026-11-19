package br.com.levy.tarefinhas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TemaTarefinhas { TelaTarefas() } }
    }
}

@Composable
fun TelaTarefas(vm: TarefasViewModel = viewModel()) {
    val tarefas by vm.tarefas.collectAsState()
    var texto by rememberSaveable { mutableStateOf("") }
    val feitas = tarefas.count { it.feita }
    // Pendentes primeiro; dentro de cada grupo, mantém a ordem de criação.
    val ordenadas = tarefas.sortedBy { it.feita }

    fun enviar() {
        vm.adicionar(texto)
        texto = ""
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Cabecalho(feitas, tarefas.size, onLimpar = vm::limparFeitas) }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
                ) {
                    OutlinedTextField(
                        value = texto,
                        onValueChange = { texto = it },
                        placeholder = { Text("O que você precisa fazer?") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { enviar() }),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    FilledIconButton(
                        onClick = ::enviar,
                        enabled = texto.isNotBlank(),
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Icon(Icons.Filled.Add, contentDescription = "Adicionar tarefa") }
                }
            }
            if (tarefas.isEmpty()) {
                item { Vazio() }
            }
            items(ordenadas, key = { it.id }) { tarefa ->
                CartaoTarefa(
                    tarefa,
                    onAlternar = { vm.alternar(tarefa.id) },
                    onRemover = { vm.remover(tarefa.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun Cabecalho(feitas: Int, total: Int, onLimpar: () -> Unit) {
    val progresso by animateFloatAsState(if (total == 0) 0f else feitas.toFloat() / total, label = "progresso")
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(Modifier.fillMaxWidth().padding(22.dp)) {
            Text("Tarefinhas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                when {
                    total == 0 -> "Comece adicionando uma tarefa"
                    feitas == total -> "Tudo feito! 🎉"
                    else -> "${total - feitas} pendente${if (total - feitas > 1) "s" else ""}"
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.alpha(0.85f)
            )
            Spacer(Modifier.height(18.dp))
            LinearProgressIndicator(
                progress = { progresso },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f),
                strokeCap = StrokeCap.Round,
                drawStopIndicator = {}
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                Text("$feitas de $total feitas", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                if (feitas > 0) {
                    TextButton(onClick = onLimpar) {
                        Text("Limpar feitas", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun Vazio() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp)
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Nenhuma tarefa por aqui.\nQue tal adicionar a primeira?",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CartaoTarefa(tarefa: Tarefa, onAlternar: () -> Unit, onRemover: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tarefa.feita) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (tarefa.feita) 0.dp else 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onAlternar).padding(start = 6.dp, end = 4.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Checkbox(checked = tarefa.feita, onCheckedChange = { onAlternar() })
            Text(
                tarefa.titulo,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (tarefa.feita) TextDecoration.LineThrough else null,
                color = if (tarefa.feita) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRemover) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Excluir ${tarefa.titulo}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
