package ru.maipomogator.android.ui.student;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.maybefragment.R;
import com.example.maybefragment.databinding.FragmentStudentBinding;
import ru.maipomogator.android.client.entities.Group;
import ru.maipomogator.android.client.entities.Schedule;


import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class StudentFragment extends Fragment {
    public static final String APP_PREFERENCES = "mysettings";
    private static final String SAVED_TEXT = "client_group";
    private SharedPreferences sPref;
    private static final String[] weekdays = {"", "ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private Group clientGroup = null;
    private List<Group> groups;
    private final List<Group> allGroups = Group.groupRequest("SELECT * FROM public.groups");
    private List<Schedule> allSchedule;
    private LocalDate date = LocalDate.now();
    private Button chooseBtn;
    private Spinner chooseCourse;
    private Spinner chooseFac;
    private Spinner chooseGroup;
    private TextView mainText;
    private TextView courseText;
    private TextView facultyText;
    private TextView groupText;
    private Button searchBtn;
    private TextView backButton;
    private Button nextButton;
    private Button prevButton;
    private Button onWeekBtn;
    private Button nowBtn;
    private TextView scheduleText;
    private TextView statusText;
    private static final String BEER_SMILE = "\uD83C\uDF7A";
    private static final String NO_PAIRS = BEER_SMILE + " В этот день пар нет!" + BEER_SMILE;
    private static final String TIME_SMILE = "⏳";
    private static final String BOOKS_SMILE = "\uD83D\uDCDA";
    private static final String UNIVERSITY_SMILE = "⛪️";
    private static final String PROFESSOR_SMILE = "\uD83D\uDC68\u200D\uD83C\uDFEB";
    private static final String STUDENT_SMILE = "\uD83D\uDC68\uD83C\uDFFF\u200D\uD83C\uDF93";
    private static final String CALENDAR_SMILE = "📆";
    private static final String ARRAY_REGEX = "^\\[|]$";

    public StudentFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        com.example.maybefragment.databinding.FragmentStudentBinding binding = FragmentStudentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        statusText = root.findViewById(R.id.group_name_status);
        chooseCourse = root.findViewById(R.id.courseNumber);
        chooseFac = root.findViewById(R.id.facultyNumber);
        chooseGroup = root.findViewById(R.id.group);
        mainText = root.findViewById(R.id.text_student);
        courseText = root.findViewById(R.id.courseText);
        facultyText = root.findViewById(R.id.facultyText);
        groupText = root.findViewById(R.id.groupText);
        mainText.setText("Введите вашу группу");
        chooseBtn = root.findViewById(R.id.chooseBtn);
        searchBtn = root.findViewById(R.id.stud_search_btn);
        backButton = root.findViewById(R.id.backTextBtn);
        nextButton = root.findViewById(R.id.prof_next_btn);
        prevButton = root.findViewById(R.id.back_prof);
        onWeekBtn = root.findViewById(R.id.onWeek_prof);
        nowBtn = root.findViewById(R.id.now_prof);
        scheduleText = root.findViewById(R.id.scheduleText_prof);

        if (!Objects.equals(loadPref(), "")) {
            clientGroup = new Group();
            for (Group group : allGroups) {
                if (group.getName().equals(loadPref())) {
                    clientGroup = group;
                    break;
                }
            }
        }
        if (clientGroup != null) {
            chooseCourse.setVisibility(View.INVISIBLE); //блок насилия над элементами
            chooseFac.setVisibility(View.INVISIBLE);
            courseText.setVisibility(View.INVISIBLE);
            facultyText.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.GONE);
            searchBtn.setClickable(false);

            searchByGroup();
        }
        searchBtn.setOnClickListener(v -> {
            String faculty = chooseFac.getSelectedItem().toString();
            String course = chooseCourse.getSelectedItem().toString();
            groups = Group.groupRequest("SELECT * FROM public.groups WHERE group_faculty = " + faculty + " AND group_course = " + course);
            groups.sort(Comparator.comparing(Group::getName));
            //тут надо накидать в список по группе и курсу и пойти искать
            List<String> spinnerArray = new ArrayList<>(); //накидываем в список группы
            if (groups.size() != 0) {
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
                for (Group group : groups) {
                    spinnerArray.add(group.getName());
                }
            } else {
                Toast.makeText(getContext(), "Выбранных групп не существует, укажите верные данные", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
            // Определяем разметку для использования при выборе элемента
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Применяем адаптер к элементу spinner
            chooseGroup.setAdapter(adapter);

        });
        chooseBtn.setOnClickListener(v -> {
            scheduleText.setText("");
            for (Group group : groups) {
                if (group.getName().equals(chooseGroup.getSelectedItem().toString())) {
                    clientGroup = group;
                    break;
                }
            }
            searchByGroup();
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
            nextButton.setVisibility(View.INVISIBLE);
            nextButton.setClickable(false);

            prevButton.setVisibility(View.INVISIBLE);
            prevButton.setClickable(false);

            onWeekBtn.setVisibility(View.INVISIBLE);
            onWeekBtn.setClickable(false);

            nowBtn.setVisibility(View.INVISIBLE);
            nowBtn.setClickable(false);

            scheduleText.setVisibility(View.INVISIBLE);
            statusText.setVisibility(View.INVISIBLE);
        });
        nextButton.setOnClickListener(v -> {
            scheduleText.setText("");
            date = date.plusDays(1);
            searchByDate(allSchedule, date);
        });
        prevButton.setOnClickListener(v -> {
            scheduleText.setText("");
            date = date.minusDays(1);
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
        nowBtn.setOnClickListener(v -> {
            scheduleText.setText("");
            date = LocalDate.now();
            searchByDate(allSchedule, date);
        });
        return root;
    }

    private void searchByGroup() {
        statusText.setText(clientGroup.getName());
        savePref(clientGroup.toString());
        chooseBtn.setClickable(false);
        backButton.setClickable(true);
        backButton.setVisibility(View.VISIBLE);
        chooseBtn.setVisibility(View.INVISIBLE);
        chooseGroup.setVisibility(View.INVISIBLE);
        groupText.setVisibility(View.INVISIBLE);
        mainText.setVisibility(View.INVISIBLE);

        nextButton.setVisibility(View.VISIBLE);
        nextButton.setClickable(true);

        prevButton.setVisibility(View.VISIBLE);
        prevButton.setClickable(true);

        onWeekBtn.setVisibility(View.VISIBLE);
        onWeekBtn.setClickable(true);

        nowBtn.setVisibility(View.VISIBLE);
        nowBtn.setClickable(true);

        scheduleText.setVisibility(View.VISIBLE);
        allSchedule = Schedule.scheduleRequest("SELECT group_name, lesson_day, lesson_name,"
                + " professor_lastname, professor_firstname, professor_secondname,"
                + "lesson_number, lesson_type, rooms FROM public.groups AS g"
                + " INNER JOIN public.lessons_groups AS lg ON lg.group_id=g.group_id"
                + " INNER JOIN public.lessons AS l ON l.lesson_id = lg.lesson_id"
                + " INNER JOIN public.lesson_rooms AS lr ON lr.room_id = l.lesson_id"
                + " INNER JOIN public.lessons_professors AS lp ON lp.lesson_id = l.lesson_id"
                + " INNER JOIN public.professors AS p ON p.professor_id = lp.professor_id"
                + " WHERE g.group_name='" + clientGroup.getName() + "'"
                + " ORDER BY lesson_day");

        searchByDate(allSchedule, date);
    }

    @SuppressLint("SetTextI18n")
    public void searchByDate(List<Schedule> allSchedule, LocalDate date) { // поиск расписания по дате
        System.out.println("поиск и вывод сообщения");
        StringBuilder answer;
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
                scheduleMessage(date, answer, lsn);
            }
            scheduleAnswer(date, date, answer);
        } else {
            scheduleText.setText("ㅤㅤㅤㅤㅤ" +
                    CALENDAR_SMILE + date.format(DATE_FORMATTER) + " (" + weekdays[date.getDayOfWeek().getValue()] + ")" + "\n" + NO_PAIRS);
        }
    }

    private void scheduleMessage(LocalDate firstDayOfWeek, StringBuilder answer, Schedule lsn) { // тело ответа для
        // расписания
        String localProfs = lsn.getProfessor();
        // if (lsn.getLesson_date().equals(firstDayOfWeek)) {
        // составление сообщения с данными о занятиях
        answer.append("\n" + TIME_SMILE).append(lsn.getNumberToTime()).append("\n")
                .append(BOOKS_SMILE).append(lsn.getName()).append(" (")
                .append(lsn.getLessonType()).append(")\n")
                .append(UNIVERSITY_SMILE).append(lsn.getRoom().toString().replaceAll(ARRAY_REGEX, "")).append("\n")
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