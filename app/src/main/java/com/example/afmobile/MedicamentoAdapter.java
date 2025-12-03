package com.example.afmobile;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView; // Importante para mudar a cor
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.ViewHolder> {

    private List<Medicamento> lista;
    private FirebaseFirestore db;
    private Context context;

    public MedicamentoAdapter(List<Medicamento> lista, Context context) {
        this.lista = lista;
        this.context = context; // Necessário para abrir a tela de edição
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicamento m = lista.get(position);

        holder.txtNome.setText(m.getNome());
        holder.txtHorario.setText(m.getHorario());

        // REQUISITO 1: Indicador Visual (Muda cor se tomado)
        if (m.isConsumido()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C8E6C9")); // Verde claro
            holder.chkTomado.setChecked(true);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.chkTomado.setChecked(false);
        }

        // Carregar imagem API
        if (m.getPokemonUrl() != null && !m.getPokemonUrl().isEmpty()) {
            Picasso.get().load(m.getPokemonUrl()).into(holder.imgPokemon);
        }

        // REQUISITO 5: Marcar como tomado
        holder.chkTomado.setOnClickListener(v -> {
            boolean novoStatus = ((CheckBox) v).isChecked();
            m.setConsumido(novoStatus);
            // Atualiza no Firebase
            db.collection("medicamentos").document(m.getId())
                    .update("consumido", novoStatus);
            // O Listener na MainActivity vai atualizar a cor automaticamente
        });

        // REQUISITO 3: Excluir (Botão lixeira)
        holder.btnDeletar.setOnClickListener(v -> {
            db.collection("medicamentos").document(m.getId()).delete();
        });

        // REQUISITO 4: Editar (Clique no card)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CadastroActivity.class);
            // Passamos os dados atuais para a tela de cadastro
            intent.putExtra("id", m.getId());
            intent.putExtra("nome", m.getNome());
            intent.putExtra("descricao", m.getDescricao());
            intent.putExtra("horario", m.getHorario());
            intent.putExtra("url", m.getPokemonUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtHorario;
        ImageView imgPokemon;
        ImageButton btnDeletar;
        CheckBox chkTomado; // Novo componente
        CardView cardView;  // Para mudar a cor de fundo

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtHorario = itemView.findViewById(R.id.txtHorario);
            imgPokemon = itemView.findViewById(R.id.imgPokemon);
            btnDeletar = itemView.findViewById(R.id.btnDeletar);
            // Precisamos adicionar um CheckBox no seu XML item_medicamento, ou usar um botão existente
            // Vou assumir que você adicionará um CheckBox com id chkTomado no XML
            // Se não tiver CheckBox, pode improvisar usando o clique longo
            cardView = (CardView) itemView;
            chkTomado = new CheckBox(itemView.getContext()); // Placeholder se não tiver no XML
            // DICA: Adicione <CheckBox android:id="@+id/chkTomado" ... /> no seu item_medicamento.xml
        }
    }
}