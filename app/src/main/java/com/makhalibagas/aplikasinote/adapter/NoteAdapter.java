package com.makhalibagas.aplikasinote.adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.makhalibagas.aplikasinote.R;
import com.makhalibagas.aplikasinote.entities.Note;
import com.makhalibagas.aplikasinote.utils.onClickItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bagas Makhali on 6/18/2020.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Filterable {

    private final List<Note> noteList;
    private List<Note> noteListFilter;
    private final onClickItem onClickItem;

    public NoteAdapter(List<Note> noteList, onClickItem onClickItem) {
        this.noteList = noteList;
        this.noteListFilter = noteList;
        this.onClickItem = onClickItem;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteViewHolder holder, final int position) {

        final Note note = noteListFilter.get(position);

        holder.title.setText(note.getTitle());
        holder.text.setText(note.getNoteText());
        holder.timeDate.setText(note.getDateTime());
        GradientDrawable gradientDrawable = (GradientDrawable) holder.layoutBackground.getBackground();

        if (note.getColor() != null){
            gradientDrawable.setColor(Color.parseColor(note.getColor()));
        }else {
            gradientDrawable.setColor(Color.parseColor("#ffffff"));
        }

        if (note.getImagePath() != null){
            holder.roundedImageView.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
            holder.roundedImageView.setVisibility(View.VISIBLE);
        }else {
            holder.roundedImageView.setVisibility(View.GONE);
        }


        holder.layoutBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem.onClick(note, position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return noteListFilter.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()){
                    noteListFilter = noteList;
                }else {
                    List<Note>  noteFilter = new ArrayList<>();
                    for (Note note : noteList){
                        if (note.getTitle().toLowerCase().contains(charString.toLowerCase()) || note.getNoteText().toLowerCase().contains(charString.toLowerCase())){
                            noteFilter.add(note);
                        }
                    }

                    noteListFilter = noteFilter;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = noteListFilter;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                noteListFilter = (List<Note>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        final TextView title;
        final TextView text;
        final TextView timeDate;
        final LinearLayout layoutBackground;
        final RoundedImageView roundedImageView;
        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            text = itemView.findViewById(R.id.tvText);
            timeDate = itemView.findViewById(R.id.tvTime);
            roundedImageView = itemView.findViewById(R.id.roundedImage);
            layoutBackground = itemView.findViewById(R.id.layoutBackground);
        }
    }
}
