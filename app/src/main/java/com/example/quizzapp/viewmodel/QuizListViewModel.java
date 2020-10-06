package com.example.quizzapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizzapp.model.QuizList;
import com.example.quizzapp.view.data.FirebaseRepository;

import java.util.List;

public class QuizListViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {
    //khoi tao FirebaseRepository
    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);
    //khoi tao MutableLiveData
    private MutableLiveData<List<QuizList>> quizListData = new MutableLiveData<>();
    //contructor
    public LiveData<List<QuizList>> getQuizListData() {
        return quizListData;
    }
    public QuizListViewModel() {
        firebaseRepository.getQuizData();
    }

    @Override
    public void quizListDataAdded(List<QuizList> quizListModelsList) {
        quizListData.setValue(quizListModelsList);
    }

    @Override
    public void onError(Exception e) {

    }
}
