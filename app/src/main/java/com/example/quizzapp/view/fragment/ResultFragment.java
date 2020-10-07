package com.example.quizzapp.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quizzapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ResultFragment extends Fragment {
    private NavController navController;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private String quizId;

    private TextView resultCorrect;
    private TextView resultWrong;
    private TextView resultMissed;
    private TextView resultPercent;
    private ProgressBar progressBar;
    private Button resultHomeButton;


    public ResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();

        //get UserId
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        } else {

        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        quizId = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();

        //init UI
        resultCorrect = view.findViewById(R.id.tv_correct_result);
        resultWrong = view.findViewById(R.id.tv_wrong_result);
        resultMissed = view.findViewById(R.id.tv_missed_result);

        resultHomeButton = view.findViewById(R.id.btn_home_result);
        resultHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_resultFragment_to_listFragment);
            }
        });

        resultPercent = view.findViewById(R.id.tv_percent_result);
        progressBar = view.findViewById(R.id.pb_result);

        //get Result
        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(currentUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();

                    Long correct = result.getLong("correct");
                    Long wrong = result.getLong("wrong");
                    Long missed = result.getLong("unanswered");

                    resultCorrect.setText(correct.toString());
                    resultMissed.setText(missed.toString());
                    resultWrong.setText(wrong.toString());

                    //
                    Long total = correct + missed + wrong;
                    Long percent = (correct*100)/total;

                    resultPercent.setText(percent + "%");
                    progressBar.setProgress(percent.intValue());
                }
            }
        });


    }
}