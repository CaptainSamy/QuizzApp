package com.example.quizzapp.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
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
import java.util.HashMap;
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
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    allQuestionsList = task.getResult().toObjects(Question.class);
                    pickQuestions();
                    loadUI();
                } else {
                    quizTitle.setText("Error: " + task.getException().getMessage());
                    Log.d("BUG", task.getException().getMessage());
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
        loadQuestion(1);
    }

    private void loadQuestion(int i) {
        //load question num
        questionNumber.setText(i + "");
        //load question text
        questionText.setText(questionsToAnswer.get(i-1).getQuestion());
        //load option
        optionButtonA.setText(questionsToAnswer.get(i-1).getOption_a());
        optionButtonB.setText(questionsToAnswer.get(i-1).getOption_b());
        optionButtonC.setText(questionsToAnswer.get(i-1).getOption_c());

        //question loaded, set can answer
        canAnswer = true;
        currentQuestion = i;
        //start time
        startTimer(i);
    }

    private void startTimer(int i) {
        //set timer text
        final Long timeToAnswer = questionsToAnswer.get(i-1).getTimer();
        questionTime.setText(timeToAnswer.toString());
        //show timer progressBar
        progressBar.setVisibility(View.VISIBLE);

        //start countdown
        countDownTimer = new CountDownTimer(timeToAnswer*1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                //update timer
                questionTime.setText(millisUntilFinished/1000 + "");
                //progress in percent
                Long percent = millisUntilFinished/(timeToAnswer*10);
                progressBar.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                //time up, cannot answer
                canAnswer = false;

                questionFeedback.setText("Time Up! No answer was submitted");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                notAnswer++;
                showNextButton();
            }
        };
        countDownTimer.start();
    }

    private void showNextButton() {
        if (currentQuestion == totalQuestionsToAnswer) {
            nextButton.setText("Submit");
        }
        questionFeedback.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setEnabled(true);
    }


    private void enableOptions() {
        //show all options
        optionButtonA.setVisibility(View.VISIBLE);
        optionButtonB.setVisibility(View.VISIBLE);
        optionButtonC.setVisibility(View.VISIBLE);

        //enable option button
        optionButtonA.setEnabled(true);
        optionButtonB.setEnabled(true);
        optionButtonC.setEnabled(true);

        //hide feedback, next button
        questionFeedback.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        nextButton.setEnabled(false);
    }

    private void pickQuestions() {
        for (int j = 0; j < totalQuestionsToAnswer; j++) {
            int randomNumber = getRandomInt(0, allQuestionsList.size());
            questionsToAnswer.add(allQuestionsList.get(randomNumber));
            allQuestionsList.remove(randomNumber);
        }
    }

    private int getRandomInt(int i, int size) {
        return ((int) (Math.random()*(size - i))) + i;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_quiz_option_one:
                verifyAnswer(optionButtonA);
                break;
            case R.id.btn_quiz_option_two:
                verifyAnswer(optionButtonB);
                break;
            case R.id.btn_quiz_option_three:
                verifyAnswer(optionButtonC);
                break;
            case R.id.btn_quiz_next:
                if (currentQuestion == totalQuestionsToAnswer) {
                    //load result
                    submitRsults();
                } else {
                    currentQuestion++;
                    loadQuestion(currentQuestion);
                    resetOption();
                }
                break;
        }
    }

    private void resetOption() {
        optionButtonA.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionButtonB.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionButtonC.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));

        optionButtonA.setTextColor(getResources().getColor(R.color.colorLightText, null));
        optionButtonB.setTextColor(getResources().getColor(R.color.colorLightText, null));
        optionButtonC.setTextColor(getResources().getColor(R.color.colorLightText, null));

        questionFeedback.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        nextButton.setEnabled(true);
    }

    private void submitRsults() {
        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("correct", correctAnswer);
        hashMap.put("wrong", wrongAnswer);
        hashMap.put("unanswered", notAnswer);

        firebaseFirestore.collection("QuizList")
                .document(quizId)
                .collection("Results")
                .document(currentUserId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // go to result page
                            QuizFragmentDirections.ActionQuizFragmentToResultFragment action = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                            action.setQuizId(quizId);
                            navController.navigate(action);
                        } else {
                            // error
                            quizTitle.setText(task.getException().getMessage());
                        }
                    }
                });
    }

    private void verifyAnswer(Button optionButton) {
        //check answer
        if (canAnswer) {
            //set color button answer
            optionButton.setTextColor(getResources().getColor(R.color.colorAccent, null));

            if (questionsToAnswer.get(currentQuestion - 1).getAnswer().equals(optionButton.getText())) {
                //correct answer
                correctAnswer++;
                optionButton.setBackground(getResources().getDrawable(R.drawable.correct_answer_btn_bg, null));

                //set feedback text
                questionFeedback.setText("Correct Answer");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                //wrong answer
                wrongAnswer++;
                optionButton.setBackground(getResources().getDrawable(R.drawable.wrong_answer_btn_bg, null));

                //set feedback text
                questionFeedback.setText("Correct Answer");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorAccent, null));
            }
            //set can answer to false
            canAnswer = false;
            //stop timer
            countDownTimer.cancel();
            //show next button
            showNextButton();
        }
    }
}