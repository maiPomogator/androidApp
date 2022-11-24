package com.example.maybefragment.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.maybefragment.R;
import com.example.maybefragment.databinding.FragmentStudentBinding;

import java.util.ArrayList;
import java.util.List;

// в этом фрагменте, при нажатие на кнопку chooseBtn нужно новое окно либо фрагмент, в котором будет осуществляться вывод расписания и кнопки навигации
public class StudentFragment extends Fragment {
    private FragmentStudentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStudentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Spinner chooseCourse = root.findViewById(R.id.courseNumber);
        Spinner chooseFac = root.findViewById(R.id.facultyNumber);
        Spinner chooseGroup = root.findViewById(R.id.group);
        TextView textView = root.findViewById(R.id.text_student);
        TextView courseText = root.findViewById(R.id.courseText);
        TextView facultyText = root.findViewById(R.id.facultyText);
        TextView groupText = root.findViewById(R.id.groupText);
        textView.setText("Введите вашу группу");
        Button chooseBtn = root.findViewById(R.id.chooseBtn);
        Button searchBtn = root.findViewById(R.id.stud_search_btn);
        Button backButton = root.findViewById(R.id.studBackBtn);
        searchBtn.setOnClickListener(v -> {
            chooseCourse.setVisibility(View.INVISIBLE); //блок насилия над элементами
            chooseFac.setVisibility(View.INVISIBLE);
            courseText.setVisibility(View.INVISIBLE);
            facultyText.setVisibility(View.INVISIBLE);
            chooseBtn.setVisibility(View.VISIBLE);
            searchBtn.setVisibility(View.GONE);
            chooseGroup.setVisibility(View.VISIBLE);
            groupText.setVisibility(View.VISIBLE);
            searchBtn.setClickable(false);
            chooseBtn.setClickable(true);
            backButton.setClickable(true);
            backButton.setVisibility(View.VISIBLE);
            String faculty = chooseFac.getSelectedItem().toString();
            String course = chooseCourse.getSelectedItem().toString();
            //тут надо накидать в список по группе и курсу и пойти искать
            List<String> spinnerArray = new ArrayList<>(); //накидываем в список группы
            spinnerArray.add("item1");
            spinnerArray.add("item2");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
            // Определяем разметку для использования при выборе элемента
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Применяем адаптер к элементу spinner
            chooseGroup.setAdapter(adapter);
        });
        chooseBtn.setOnClickListener(v -> {
            Toast toast = Toast.makeText(getActivity(),
                    "издец!", Toast.LENGTH_SHORT);
            toast.show();


        });
        backButton.setOnClickListener(v -> {
            chooseCourse.setVisibility(View.VISIBLE); //блок насилия над элементами
            chooseFac.setVisibility(View.VISIBLE);
            courseText.setVisibility(View.VISIBLE);
            facultyText.setVisibility(View.VISIBLE);
            chooseBtn.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.VISIBLE);
            chooseGroup.setVisibility(View.INVISIBLE);
            groupText.setVisibility(View.INVISIBLE);
            searchBtn.setClickable(true);
            chooseBtn.setClickable(false);
            backButton.setClickable(false);
            backButton.setVisibility(View.INVISIBLE);
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}