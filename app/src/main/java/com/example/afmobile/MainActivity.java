package com.example.afmobile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<Medicamento> lista = new ArrayList<>();
    private MedicamentoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        criarCanalNotificacao(); // Configura canal para Android 8+ (Aula 10)

        db = FirebaseFirestore.getInstance();

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Adapter e vincular à lista
        adapter = new MedicamentoAdapter(lista, this);
        recyclerView.setAdapter(adapter);

        // Botão flutuante para ir para o Cadastro
        findViewById(R.id.fabAdicionar).setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class))
        );

        // Iniciar escuta do banco de dados
        ouvirAtualizacoesFirestore();
    }

    private void ouvirAtualizacoesFirestore() {
        db.collection("medicamentos").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }

            lista.clear(); // Limpa a lista antiga
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    Medicamento m = doc.toObject(Medicamento.class);
                    m.setId(doc.getId()); // Importante pegar o ID para poder deletar depois
                    lista.add(m);
                }
            }
            // Avisa o adapter que os dados mudaram
            adapter.notifyDataSetChanged();
        });
    }

    private void criarCanalNotificacao() {
        // Necessário para notificações funcionarem em Android recentes (Aula 10)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "CANAL_REMEDIO";
            CharSequence nome = "Lembretes de Remédios";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(id, nome, importancia);

            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(canal);
            }
        }
    }
}