package com.example.quizzapp.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quizzapp.R;
import com.example.quizzapp.model.QuizList;
import com.example.quizzapp.viewmodel.QuizListViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetailsFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private QuizListViewModel quizListViewModel;
    private int position;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private ImageView detailImage;
    private TextView detailTitle;
    private TextView detailDesc;
    private TextView detailDiff;
    private TextView detailQuestions;
    private TextView detailScore;

    private Button detailButton;

    private String quizId;
    private long totalQuestions = 0;
    private String quizName;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition();

        //init UI
        detailTitle = view.findViewById(R.id.tv_list_title_detail);
        detailImage = view.findViewById(R.id.iv_detail);
        detailDesc = view.findViewById(R.id.tv_list_desc_detail);
        detailDiff = view.findViewById(R.id.tv_difficulty_detail);
        detailQuestions = view.findViewById(R.id.tv_questions_detail);
        detailScore = view.findViewById(R.id.tv_score_detail);

        detailButton = view.findViewById(R.id.btn_start_detail);
        detailButton.setOnClickListener(this);

        //load
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListData().observe(getViewLifecycleOwner(), new Observer<List<QuizList>>() {
            @Override
            public void onChanged(List<QuizList> quizListList) {
                Glide.with(getContext()).load(quizListList.get(position).getImage())
                        .placeholder(R.drawable.placeholder_image)
                        .into(detailImage);

                detailTitle.setText(quizListList.get(position).getName());

                detailDesc.setText(quizListList.get(position).getDesc());
                detailDiff.setText(quizListList.get(position).getLevel());
                detailQuestions.setText(quizListList.get(position).getQuestions()+"");

                quizId = quizListList.get(position).getQuiz_id();
                quizName = quizListList.get(position).getName();
                totalQuestions = quizListList.get(position).getQuestions();

                //load data
                loadResultData();
            }
        });
    }

    private void loadResultData() {
        firebaseFirestore.collection("QuizList")
                .document(quizId)
                .collection("Results")
                .document(firebaseAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null && doc.exists()) {
                        //get result
                        Long correct = doc.getLong("correct");
                        Long wrong = doc.getLong("wrong");
                        Long missed = doc.getLong("unanswered");

                        //calculate
                        Long total = correct + wrong + missed;
                        Long percent = (correct*100)/total;

                        detailScore.setText(percent+"%");
                    }
                } else {
                    //nothing, stay NA
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_detail:
                DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment action = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
                action.setQuizId(quizId);
                action.setQuizName(quizName);
                action.setTotalQuestions(totalQuestions);
                navController.navigate(action);
                break;
        }
    }
}