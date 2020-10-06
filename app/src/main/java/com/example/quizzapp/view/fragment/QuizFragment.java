package com.example.quizzapp.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quizzapp.R;
import com.example.quizzapp.model.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class QuizFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    private String quizName;
    private String quizId;

    //UI
    private TextView quizTitle;
    private TextView questionFeedback;
    private TextView questionText;
    private TextView questionTime;
    private TextView questionNumber;

    private Button optionButtonA;
    private Button optionButtonB;
    private Button optionButtonC;
    private Button nextButton;
    private ImageButton closeButton;

    private ProgressBar progressBar;

    private List<Question> allQuestionsList = new ArrayList<>();
    private List<Question> questionsToAnswer = new ArrayList<>();
    private Long totalQuestionsToAnswer = 0L;
    private CountDownTimer countDownTimer;

    private boolean canAnswer = false;
    private int currentQuestion = 0;

    private int correctAnswer = 0;
    private int wrongAnswer = 0;
    private int notAnswer = 0;



    public QuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
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
            //go back home page
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        //init UI
        quizTitle = view.findViewById(R.id.tv_title_quiz);
        questionFeedback = view.findViewById(R.id.tv_quiz_question_feedback);
        questionText = view.findViewById(R.id.tv_quiz_question);
        questionTime = view.findViewById(R.id.tv_quiz_question_time);
        questionNumber = view.findViewById(R.id.tv_quiz_question_number);

        optionButtonA = view.findViewById(R.id.btn_quiz_option_one);
        optionButtonB = view.findViewById(R.id.btn_quiz_option_two);
        optionButtonC = view.findViewById(R.id.btn_quiz_option_three);
        nextButton = view.findViewById(R.id.btn_quiz_next);
        closeButton = view.findViewById(R.id.iBtn_close_quiz);

        progressBar = view.findViewById(R.id.quiz_progress);

        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        quizName = QuizFragmentArgs.fromBundle(getArguments()).getQuizName();
        totalQuestionsToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();

        //query firestore data
        firebaseFirestore.collection("QuizList")
                .document(quizId)
                .collection("Questions")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    allQuestionsList = task.getResult().toObjects(Question.class);
                    pickQuestions();
                    loadUI();
                } else {
                    quizTitle.setText("Error: " + task.getException().getMessage());
                }
            }
        });

        //button listeners
        optionButtonA.setOnClickListener(this);
        optionButtonB.setOnClickListener(this);
        optionButtonC.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    private void loadUI() {
        //loaded data, load UI
        quizTitle.setText(quizName);
        questionText.setText("Load Question");

        //enable option
        enableOptions();

        //load question
        loadQuestion();
    }

    private void loadQuestion() {

    }

    private void enableOptions() {

    }

    private void pickQuestions() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }
}