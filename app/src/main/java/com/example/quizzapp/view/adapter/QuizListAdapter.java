package com.example.quizzapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizzapp.R;
import com.example.quizzapp.model.QuizList;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {
    private List<QuizList> quizLists;
    private OnQuizListItemClicked onQuizListItemClicked;

    public QuizListAdapter(OnQuizListItemClicked onQuizListItemClicked) {
        this.onQuizListItemClicked = onQuizListItemClicked;
    }

    public void setQuizLists(List<QuizList> quizLists) {
        this.quizLists = quizLists;
    }

    @NonNull
    @Override
    public QuizListAdapter.QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizListAdapter.QuizViewHolder holder, int position) {
        //title
        holder.listTitle.setText(quizLists.get(position).getName());
        //image
        String imageUrl = quizLists.get(position).getImage();
        Glide.with(holder.itemView.getContext()).load(imageUrl).placeholder(R.drawable.placeholder_image).into(holder.listImage);
        //desc
        String desc = quizLists.get(position).getDesc();
        if (desc.length() > 150) {
            desc = desc.substring(0, 150);
        }
        holder.listDesc .setText(desc + "...");
        //level
        holder.listLevel.setText(quizLists.get(position).getLevel());
    }

    @Override
    public int getItemCount() {
        if (quizLists == null) {
            return 0;
        } else {
            return quizLists.size();
        }
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView listImage;
        private TextView listTitle;
        private TextView listDesc;
        private TextView listLevel;
        private Button listButton;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            listImage = itemView.findViewById(R.id.iv_single_list_item);
            listTitle = itemView.findViewById(R.id.tv_list_title);
            listDesc = itemView.findViewById(R.id.tv_list_desc);
            listLevel = itemView.findViewById(R.id.tv_list_difficulty);
            listButton = itemView.findViewById(R.id.btn_list);

            listButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onQuizListItemClicked.onItemClicked(getAdapterPosition());
        }
    }

    public interface OnQuizListItemClicked {
        void onItemClicked(int position);
    }
}
