package com.example.afmobile;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
    private String idMedicamento = null; // Se for nulo = Novo, Se tiver ID = Edição
    private String urlExistente = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        db = FirebaseFirestore.getInstance();
        edtNome = findViewById(R.id.edtNome);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtHorario = findViewById(R.id.edtHorario);
        Button btnSalvar = findViewById(R.id.btnSalvar);

        // Verifica se veio da Tela Principal para EDIÇÃO (Requisito 4)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idMedicamento = extras.getString("id");
            edtNome.setText(extras.getString("nome"));
            edtDescricao.setText(extras.getString("descricao"));
            edtHorario.setText(extras.getString("horario"));
            urlExistente = extras.getString("url");
            btnSalvar.setText("Atualizar"); // Muda texto do botão
        }

        btnSalvar.setOnClickListener(v -> salvarMedicamento());
    }

    private void salvarMedicamento() {
        String nome = edtNome.getText().toString();
        String desc = edtDescricao.getText().toString();
        String hora = edtHorario.getText().toString();

        if (idMedicamento != null) {
            // --- MODO EDIÇÃO ---
            Medicamento m = new Medicamento(nome, desc, hora, urlExistente);
            db.collection("medicamentos").document(idMedicamento).set(m)
                    .addOnSuccessListener(v -> {
                        agendarNotificacao(nome, hora); // Requisito Alerta
                        finish();
                    });
        } else {
            // --- MODO NOVO ---
            PokemonService.sortearPokemon(urlImagem -> {
                Medicamento m = new Medicamento(nome, desc, hora, urlImagem);
                db.collection("medicamentos").add(m)
                        .addOnSuccessListener(v -> {
                            agendarNotificacao(nome, hora); // Requisito Alerta
                            runOnUiThread(this::finish);
                        });
            });
        }
    }

    // REQUISITO: Alerta de Horário
    private void agendarNotificacao(String nomeRemedio, String horario) {
        try {
            String[] partes = horario.split(":");
            int hora = Integer.parseInt(partes[0]);
            int minuto = Integer.parseInt(partes[1]);

            Calendar calendario = Calendar.getInstance();
            calendario.set(Calendar.HOUR_OF_DAY, hora);
            calendario.set(Calendar.MINUTE, minuto);
            calendario.set(Calendar.SECOND, 0);

            // Se o horário já passou hoje, agenda para amanhã
            if (calendario.getTimeInMillis() < System.currentTimeMillis()) {
                calendario.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("NOME_REMEDIO", nomeRemedio);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(), // ID único
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // Agenda o alarme exato
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), pendingIntent);

            Toast.makeText(this, "Alarme agendado para " + horario, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao agendar horário. Use formato HH:mm", Toast.LENGTH_LONG).show();
        }
    }
}