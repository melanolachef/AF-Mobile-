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
import androidx.cardview.widget.CardView;
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
        this.context = context;
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

        // --- EXIBIÇÃO DO NOME (Com Recurso Extra) ---
        if (m.getPokemonNome() != null && !m.getPokemonNome().isEmpty()) {
            // Ex: "Dipirona (Pikachu)"
            holder.txtNome.setText(m.getNome() + " (" + m.getPokemonNome() + ")");
        } else {
            holder.txtNome.setText(m.getNome());
        }

        holder.txtHorario.setText(m.getHorario());

        // --- INDICADOR VISUAL (Requisito 1) ---
        holder.chkTomado.setOnCheckedChangeListener(null); // Remove listener antigo para evitar bug visual
        if (m.isConsumido()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C8E6C9")); // Verde
            holder.chkTomado.setChecked(true);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.chkTomado.setChecked(false);
        }

        // --- CARREGAR IMAGEM ---
        if (m.getPokemonUrl() != null && !m.getPokemonUrl().isEmpty()) {
            Picasso.get().load(m.getPokemonUrl()).into(holder.imgPokemon);
        } else {
            holder.imgPokemon.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // --- AÇÃO: MARCAR COMO TOMADO (Requisito 5) ---
        holder.chkTomado.setOnClickListener(v -> {
            boolean status = ((CheckBox) v).isChecked();
            m.setConsumido(status);
            if (m.getId() != null) {
                db.collection("medicamentos").document(m.getId()).update("consumido", status);
            }
        });

        // --- AÇÃO: EXCLUIR (Requisito 3) ---
        holder.btnDeletar.setOnClickListener(v -> {
            if (m.getId() != null) {
                db.collection("medicamentos").document(m.getId()).delete();
            }
        });

        // --- AÇÃO: EDITAR (Requisito 4) ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CadastroActivity.class);
            intent.putExtra("id", m.getId());
            intent.putExtra("nome", m.getNome());
            intent.putExtra("descricao", m.getDescricao());
            intent.putExtra("horario", m.getHorario());
            intent.putExtra("url", m.getPokemonUrl());
            // Passamos o nome do Pokémon para não perder na edição
            intent.putExtra("pokemonNome", m.getPokemonNome());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtHorario;
        ImageView imgPokemon;
        ImageButton btnDeletar;
        CheckBox chkTomado;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtHorario = itemView.findViewById(R.id.txtHorario);
            imgPokemon = itemView.findViewById(R.id.imgPokemon);
            btnDeletar = itemView.findViewById(R.id.btnDeletar);
            chkTomado = itemView.findViewById(R.id.chkTomado);
            cardView = (CardView) itemView;
        }
    }
}
