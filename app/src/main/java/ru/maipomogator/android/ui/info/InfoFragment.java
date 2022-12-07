package ru.maipomogator.android.ui.info;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.maybefragment.R;
import com.example.maybefragment.databinding.FragmentInfoBinding;


public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;
    private Button authorBtn;
    private ImageView bottomImage;
    private ImageView topImage;
    private TextView authorText;
    private TextView backBtn;
    private TextView textView;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        authorText = root.findViewById(R.id.authorText);
        textView = binding.textInfo;
        textView.setText("Информации пока нет\nЗато можете глянуть на карты главного корпуса☝️ и оршанки\uD83D\uDC47");
        bottomImage = root.findViewById(R.id.imageView2);
        topImage = root.findViewById(R.id.imageView);
        backBtn = root.findViewById(R.id.backBtnInfo);
        authorBtn = root.findViewById(R.id.authorBtn);
        authorBtn.setOnClickListener(v -> {
            authorBtn.setVisibility(View.INVISIBLE);
            authorText.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
            bottomImage.setVisibility(View.INVISIBLE);
            topImage.setVisibility(View.INVISIBLE);
            authorText.setText("""
                    Авторы сия творения:
                    Андреев Андрей
                    Кустиков Андрей
                    Муравьёв Иван
                    Поблагодарить авторов и оставить отзыв можно в телеграмме:
                    https://t.me/podkydish
                    https://t.me/bupbipbupbip
                    https://t.me/rufus20145
                    Заценить нашего телеграмм-бота можно тут:
                    https://t.me/maiPomogator_bot
                    """);
        });
        backBtn.setOnClickListener(v -> {
            authorBtn.setVisibility(View.VISIBLE);
            authorText.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            bottomImage.setVisibility(View.VISIBLE);
            topImage.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}



