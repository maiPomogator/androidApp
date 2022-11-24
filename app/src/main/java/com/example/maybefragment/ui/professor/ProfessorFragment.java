package com.example.maybefragment.ui.professor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.maybefragment.R;
import com.example.maybefragment.databinding.FragmentProfessorBinding;


public class ProfessorFragment extends Fragment {

    private FragmentProfessorBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfessorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = root.findViewById(R.id.text_professor);
        textView.setText("Введите ваше ФИО");
        EditText input = root.findViewById(R.id.editText2);
        input.setHint("Вводить сюда...");
        Button searchBtn = root.findViewById(R.id.profSearchBtn);
        searchBtn.setOnClickListener(v->{

            Toast toast = Toast.makeText(getActivity(),
                    input.getText(), Toast.LENGTH_SHORT);
            input.setText("");
            toast.show();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}