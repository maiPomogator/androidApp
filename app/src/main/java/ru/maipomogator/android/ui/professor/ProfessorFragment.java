package ru.maipomogator.android.ui.professor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.maybefragment.R;
import com.example.maybefragment.databinding.FragmentProfessorBinding;
import ru.maipomogator.android.client.entities.Professor;
import ru.maipomogator.android.client.entities.Schedule;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


public class ProfessorFragment extends Fragment {
    private static final String[] weekdays = {"", "ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final List<Professor> allProfessors = Professor.professorRequest("SELECT*FROM public.professors");
    private FragmentProfessorBinding binding;
    private Professor clientProfessor;
    private List<Professor> outputProfessors;
    private TextView profInfoText;
    private Spinner profSpinner;
    private Button searchNameBtn;
    private TextView scheduleText;
    private Button nextDay;
    private Button prevDay;
    private Button todayBtn;
    private Button onWeekBtn;
    private TextView profName;
    private List<Schedule> allSchedule;
    private LocalDate date = LocalDate.now();
    private TextView backTextBtn;
    private static final String BEER_SMILE = "\uD83C\uDF7A";
    private static final String NO_PAIRS = BEER_SMILE + " В этот день пар нет!" + BEER_SMILE;
    private static final String TIME_SMILE = "⏳";
    private static final String BOOKS_SMILE = "\uD83D\uDCDA";
    private static final String UNIVERSITY_SMILE = "⛪️";
    private static final String PROFESSOR_SMILE = "\uD83D\uDC68\u200D\uD83C\uDFEB";
    private static final String STUDENT_SMILE = "\uD83D\uDC68\uD83C\uDFFF\u200D\uD83C\uDF93";
    private static final String CALENDAR_SMILE = "📆";
    private static final String ARRAY_REGEX = "^\\[|]$";
    public static final String APP_PREFERENCES = "mysettings";
    private static final String SAVED_TEXT = "client_professor";
    SharedPreferences sPref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfessorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        scheduleText = root.findViewById(R.id.scheduleText_prof);
        nextDay = root.findViewById(R.id.prof_next_btn);
        prevDay = root.findViewById(R.id.back_prof);
        todayBtn = root.findViewById(R.id.now_prof);
        onWeekBtn = root.findViewById(R.id.onWeek_prof);

        backTextBtn = root.findViewById(R.id.backTextBtn);

        profName = root.findViewById(R.id.prof_name_status);
        profInfoText = root.findViewById(R.id.text_professor);
        profInfoText.setText("Введите ваше ФИО");
        profSpinner = root.findViewById(R.id.chooseProfSpin);
        searchNameBtn = root.findViewById(R.id.profChooseBtn);
backTextBtn.setVisibility(View.INVISIBLE);
        EditText input = root.findViewById(R.id.editText2);
        input.setHint("Вводить сюда...");

        Button searchBtn = root.findViewById(R.id.profSearchBtn);

        if (!Objects.equals(loadPref(), "")) {
            outputProfessors = new LinkedList<>(allProfessors);
            outputProfessors.removeIf(prof -> prof.getSiteId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000")));
            for (Professor professor : outputProfessors) {
                if (professor.getFullName().equals(loadPref())) {
                    clientProfessor = professor;
                    break;
                }
            }
        }
        if (clientProfessor != null) {
            searchBtn.setVisibility(View.INVISIBLE);
            input.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.GONE);
            searchBtn.setClickable(false);

            searchByProf();
        }

        searchBtn.setOnClickListener(v -> {
            if (input.getText().toString().split(" ").length < 5) { // Считывание ФИО преподавателя
                backTextBtn.setVisibility(View.VISIBLE);
                backTextBtn.setClickable(true);
                profSpinner.setVisibility(View.VISIBLE);
                searchNameBtn.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.INVISIBLE);
                input.setVisibility(View.INVISIBLE);
                professorSearch(input.getText().toString().toLowerCase());
            } else {
                input.setHint("Проверьте написание и повторите попытку");
            }

        });
        searchNameBtn.setOnClickListener(v -> {
            backTextBtn.setVisibility(View.VISIBLE);
            backTextBtn.setClickable(true);
            profName.setVisibility(View.VISIBLE);
            clientProfessor = outputProfessors.get(profSpinner.getSelectedItemPosition());
            searchByProf();
        });
        backTextBtn.setOnClickListener(v -> {
            profName.setVisibility(View.INVISIBLE);
            profInfoText.setVisibility(View.VISIBLE);
            searchNameBtn.setVisibility(View.VISIBLE);
            onWeekBtn.setVisibility(View.INVISIBLE);
            todayBtn.setVisibility(View.INVISIBLE);
            prevDay.setVisibility(View.INVISIBLE);
            nextDay.setVisibility(View.INVISIBLE);
            scheduleText.setVisibility(View.INVISIBLE);
            profSpinner.setVisibility(View.INVISIBLE);
            searchNameBtn.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.VISIBLE);
            input.setVisibility(View.VISIBLE);
            input.setVisibility(View.VISIBLE);
            searchBtn.setVisibility(View.VISIBLE);
            input.setHint("Вводить сюда...");
            backTextBtn.setVisibility(View.INVISIBLE);
            backTextBtn.setClickable(false);
        });
        nextDay.setOnClickListener(v -> {
            date = date.plusDays(1);
            searchByDate(allSchedule, date);
        });
        prevDay.setOnClickListener(v -> {
            date = date.minusDays(1);
            searchByDate(allSchedule, date);
        });
        todayBtn.setOnClickListener(v -> {
            date = LocalDate.now();
            searchByDate(allSchedule, date);
        });
        onWeekBtn.setOnClickListener(v -> {
            scheduleText.setMovementMethod(new ScrollingMovementMethod());
            scheduleText.setText("");
            LocalDate firstDayOfWeek = date.minusDays(date.getDayOfWeek().getValue() - 1);
            String localText = "";
            for (int i = 0; i < 7; i++) {
                final LocalDate currentDay = firstDayOfWeek.plusDays(i);
                if (currentDay != firstDayOfWeek) {
                    localText = scheduleText.getText().toString();
                }
                searchByDate(allSchedule, currentDay);
                String answer = scheduleText.getText().toString();
                scheduleText.setText(localText + answer);
            }
        });
        return root;
    }

    @SuppressLint("SetTextI18n")
    private void searchByProf() {
        profInfoText.setVisibility(View.INVISIBLE);
        profSpinner.setVisibility(View.INVISIBLE);
        searchNameBtn.setVisibility(View.INVISIBLE);
        onWeekBtn.setVisibility(View.VISIBLE);
        todayBtn.setVisibility(View.VISIBLE);
        prevDay.setVisibility(View.VISIBLE);
        nextDay.setVisibility(View.VISIBLE);
        scheduleText.setVisibility(View.VISIBLE);
        profName.setText(clientProfessor.getFirstName() + " " + clientProfessor.getSecondName());
        allSchedule = Schedule.scheduleRequest("SELECT group_name, lesson_day, lesson_name," +
                "professor_lastname, professor_firstname, professor_secondname," +
                " lesson_number, lesson_type, rooms FROM public.groups AS g" +
                " INNER JOIN public.lessons_groups AS lg ON lg.group_id=g.group_id" +
                " INNER JOIN public.lessons AS l ON l.lesson_id = lg.lesson_id" +
                " INNER JOIN public.lesson_rooms AS lr ON lr.room_id = l.lesson_id" +
                " INNER JOIN public.lessons_professors AS lp ON lp.lesson_id = l.lesson_id" +
                " INNER JOIN public.professors AS p ON p.professor_id = lp.professor_id" +
                " WHERE p.professor_lastname = '" + clientProfessor.getLastName() + "'" +
                " ORDER BY lesson_day");
        savePref(clientProfessor.getFullName());
        searchByDate(allSchedule, date);
    }

    private void professorSearch(String userInputName) {
        outputProfessors = new LinkedList<>(allProfessors);
        outputProfessors.removeIf(prof -> prof.getSiteId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000")));
        outputProfessors.sort((o1, o2) -> {
            Integer tmpToO1 = calculateDistanceBetweenStrings(o1.getFullName(), userInputName);
            Integer tmpToO2 = calculateDistanceBetweenStrings(o2.getFullName(), userInputName);
            return tmpToO1.compareTo(tmpToO2);
        });
        profInfoText.setText("Выберите из списка нужного преподавателя");
        List<String> spinnerArray = new ArrayList<>(); //накидываем в список группы
        for (int i = 0; i < 5; i++) {
            spinnerArray.add(outputProfessors.get(i).getFullName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        profSpinner.setAdapter(adapter);
        profSpinner.setSelection(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int calculateDistanceBetweenStrings(String str1, String str2) { //расчёт разницы для преподавателей
        String[] spl1 = str1.toLowerCase().split(" ");
        String[] spl2 = str2.toLowerCase().split(" ");
        Integer dist = 0;
        LevenshteinDistance distance = LevenshteinDistance.getDefaultInstance();
        if (spl1.length != spl2.length) {
            //Log.warn("arrays length isn't equal");
        }
        for (int i = 0; i < Math.min(spl1.length, spl2.length); i++) {
            dist += distance.apply(spl1[i], spl2[i]);
        }
        return dist;
    }


    @SuppressLint("SetTextI18n")
    public void searchByDate(List<Schedule> allSchedule, LocalDate date) { // поиск расписания по дате
        System.out.println("поиск и вывод сообщения");
        StringBuilder answer = new StringBuilder();
        List<Schedule> lessons = new ArrayList<>();
        for (Schedule sc : allSchedule) {
            if (sc.getLesson_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(date)) {
                lessons.add(sc);
            }
        }
        if (lessons.size() != 0) {
            answer = new StringBuilder();
            answer.append("ㅤㅤㅤㅤㅤ").append(CALENDAR_SMILE).append(date.format(DATE_FORMATTER)).append(" (").append(weekdays[date.getDayOfWeek().getValue()]).append(")").append("\n");
            List<Schedule> lessonsForDay = lessons.stream()
                    .filter(lesson -> lesson.getLesson_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(date))
                    .sorted(Comparator.comparing(Schedule::getLesson_number)).collect(Collectors.toList());
            for (Schedule lsn : lessonsForDay) {
                scheduleMessage(answer, lsn);
            }
            scheduleAnswer(date, date, answer);
        } else {
            scheduleText.setText("ㅤㅤㅤㅤㅤ" + CALENDAR_SMILE + date.format(DATE_FORMATTER) + " (" + weekdays[date.getDayOfWeek().getValue()] + ")" + "\n" + NO_PAIRS + "\n");
        }
    }

    private void scheduleMessage(StringBuilder answer, Schedule lsn) { // тело ответа для
        // расписания
        String localProfs = lsn.getProfessor();
        // if (lsn.getLesson_date().equals(firstDayOfWeek)) {
        // составление сообщения с данными о занятиях
        answer.append("\n" + TIME_SMILE).append(lsn.getNumberToTime()).append("\n")
                .append(BOOKS_SMILE).append(lsn.getName()).append(" (")
                .append(lsn.getLessonType()).append(")\n")
                .append(UNIVERSITY_SMILE).append(lsn.getRoom().replaceAll(ARRAY_REGEX, "")).append("\n")
                .append(PROFESSOR_SMILE)
                .append(localProfs/*.get(0).getFullName()*/);
           /* if (localProfs.size() > 1) {
                for (int i = 1; i < localProfs.size(); i++) {
                    answer.append(", ").append(localProfs.get(i).getFullName());
                }
            }*/
        answer.append("\n").append(STUDENT_SMILE)
                .append(lsn.getGroup_name().toString().replaceAll(ARRAY_REGEX, ""))
                .append("\n").append("\n");
        //  }
        scheduleText.setText(answer.toString());
    }

    @SuppressLint("SetTextI18n")
    private void scheduleAnswer(LocalDate date,
                                LocalDate firstDayOfWeek, StringBuilder answer) { // построение ответа для расписания
        scheduleText.setText(String.valueOf(answer));
        if (scheduleText.getText().equals("")
                || scheduleText.getText().equals("ㅤㅤㅤㅤㅤ" + CALENDAR_SMILE + firstDayOfWeek.format(DATE_FORMATTER) + " (" + weekdays[date.getDayOfWeek().getValue()] + ")" + "\n")) {
            scheduleText.setText(
                    CALENDAR_SMILE + date.format(DATE_FORMATTER) + " (" + weekdays[date.getDayOfWeek().getValue()] + ")" + "\n" + NO_PAIRS);
        }
    }

    void savePref(String prefValue) {
        sPref = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, prefValue);
        ed.apply();
    }

    String loadPref() {
        sPref = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return sPref.getString(SAVED_TEXT, "");
    }
}