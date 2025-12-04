package com.example.afmobile;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class CadastroActivity extends AppCompatActivity {

    private EditText edtNome, edtDescricao, edtHorario;
    private FirebaseFirestore db;

    // Variáveis para controlar se é EDIÇÃO
    private String idMedicamento = null;
    private String urlExistente = null;
    private String nomePokemonExistente = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        db = FirebaseFirestore.getInstance();

        edtNome = findViewById(R.id.edtNome);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtHorario = findViewById(R.id.edtHorario);
        Button btnSalvar = findViewById(R.id.btnSalvar);

        // Verifica se veio dados da Tela Principal (Modo EDIÇÃO)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idMedicamento = extras.getString("id");
            edtNome.setText(extras.getString("nome"));
            edtDescricao.setText(extras.getString("descricao"));
            edtHorario.setText(extras.getString("horario"));
            urlExistente = extras.getString("url");
            nomePokemonExistente = extras.getString("pokemonNome"); // Recupera o nome do Pokémon

            btnSalvar.setText(R.string.btn_atualizar); // Texto "Atualizar"
        }

        btnSalvar.setOnClickListener(v -> salvarMedicamento());
    }

    private void salvarMedicamento() {
        String nome = edtNome.getText().toString();
        String desc = edtDescricao.getText().toString();
        String hora = edtHorario.getText().toString();

        if (nome.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Preencha nome e horário", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idMedicamento != null) {
            // --- MODO EDIÇÃO (Mantém o Pokémon antigo) ---
            // Certifique-se que seu Medicamento.java tem este construtor atualizado!
            Medicamento m = new Medicamento(nome, desc, hora, urlExistente, nomePokemonExistente);

            db.collection("medicamentos").document(idMedicamento).set(m)
                    .addOnSuccessListener(v -> {
                        agendarNotificacao(nome, hora);
                        Toast.makeText(this, R.string.msg_salvo, Toast.LENGTH_SHORT).show();
                        finish();
                    });

        } else {
            // --- MODO NOVO (Sorteia novo Pokémon) ---
            PokemonService.sortearPokemon((urlImagem, nomePokemon) -> {
                // Cria o objeto com URL e NOME do Pokémon
                Medicamento m = new Medicamento(nome, desc, hora, urlImagem, nomePokemon);

                db.collection("medicamentos").add(m)
                        .addOnSuccessListener(v -> {
                            agendarNotificacao(nome, hora);
                            // Volta para a thread principal para fechar a tela
                            runOnUiThread(() -> {
                                Toast.makeText(this, R.string.msg_salvo, Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        });
            });
        }
    }

    private void agendarNotificacao(String nomeRemedio, String horario) {
        try {
            String horarioLimpo = horario.trim();
            if (!horarioLimpo.contains(":")) throw new Exception("Formato inválido");

            String[] partes = horarioLimpo.split(":");
            int hora = Integer.parseInt(partes[0]);
            int minuto = Integer.parseInt(partes[1]);

            Calendar calendario = Calendar.getInstance();
            calendario.set(Calendar.HOUR_OF_DAY, hora);
            calendario.set(Calendar.MINUTE, minuto);
            calendario.set(Calendar.SECOND, 0);

            if (calendario.getTimeInMillis() <= System.currentTimeMillis()) {
                calendario.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("NOME_REMEDIO", nomeRemedio);

            // ID único baseada no tempo para não sobrescrever alarmes
            int idAlarme = (int) System.currentTimeMillis();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, idAlarme, intent, PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Verifica permissão no Android 12+ (API 31)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), pendingIntent); // Fallback
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), pendingIntent);
            }

        } catch (Exception e) {
            Toast.makeText(this, R.string.msg_erro_hora, Toast.LENGTH_SHORT).show();
        }
    }
}