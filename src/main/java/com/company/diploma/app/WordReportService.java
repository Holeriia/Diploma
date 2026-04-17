package com.company.diploma.app;

import com.company.diploma.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WordReportService {

    @Autowired
    private DataManager dataManager;

    public byte[] generateApprovedTopicsReport(Workspace workspace) throws IOException {
        // 1. Загружаем все утвержденные назначения для воркспейса
// 1. Загружаем все утвержденные назначения
        List<Assignment> assignments = dataManager.load(Assignment.class)
                .query("select e from Assignment e where e.workspace = :ws and e.status = :status")
                .parameter("ws", workspace)
                .parameter("status", AssignmentStatus.APPROVED.getId())
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("topic", FetchPlan.BASE);

                    // Загружаем ментора и его пользователя
                    plan.add("mentor", mentorPlan -> {
                        mentorPlan.addFetchPlan(FetchPlan.BASE);
                        mentorPlan.add("user", FetchPlan.BASE);
                    });

                    // Загружаем менти (студента) и его пользователя
                    plan.add("mentee", menteePlan -> {
                        menteePlan.addFetchPlan(FetchPlan.BASE);
                        menteePlan.add("user", FetchPlan.BASE);
                    });
                })
                .list();

        // 2. Нам нужны данные о студентах (группа и основа обучения)
        // Группируем назначения по группам студентов
        Map<Group, List<Assignment>> groupedByGroup = assignments.stream()
                .collect(Collectors.groupingBy(a -> {
                    Student s = getStudentByUser(a.getMentee().getUser());
                    return s != null ? s.getGroup() : new Group(); // Заглушка, если группа не найдена
                }));

        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            for (Map.Entry<Group, List<Assignment>> groupEntry : groupedByGroup.entrySet()) {
                // Заголовок Группы
                XWPFParagraph groupPara = document.createParagraph();
                groupPara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun groupRun = groupPara.createRun();
                groupRun.setBold(true);
                groupRun.setFontSize(16);
                groupRun.setText("ГРУППА: " + groupEntry.getKey().getName());

                // Группируем внутри группы по основе обучения
                Map<String, List<Assignment>> byBasis = groupEntry.getValue().stream()
                        .collect(Collectors.groupingBy(a -> {
                            Student s = getStudentByUser(a.getMentee().getUser());
                            return s != null && s.getBasisOfLearning() != null ?
                                    s.getBasisOfLearning().name() : "Не указано";
                        }));

                for (String basis : byBasis.keySet()) {
                    XWPFParagraph basisPara = document.createParagraph();
                    XWPFRun basisRun = basisPara.createRun();
                    basisRun.setItalic(true);
                    basisRun.setText(basis.toLowerCase() + " основа обучения");

                    // Создаем таблицу
                    createAssignmentTable(document, byBasis.get(basis));
                    document.createParagraph(); // Отступ после таблицы
                }
            }

            document.write(out);
            return out.toByteArray();
        }
    }

    private void createAssignmentTable(XWPFDocument document, List<Assignment> assignments) {
        XWPFTable table = document.createTable();
        table.setWidth("100%");

        // Шапка таблицы
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("ФИО студента");
        header.addNewTableCell().setText("Тема");
        header.addNewTableCell().setText("Руководитель (Степень, Звание, Должность)");

        for (Assignment a : assignments) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(a.getMentee().getUser().getDisplayName());
            row.getCell(1).setText(a.getTopic() != null ? a.getTopic().getName() : "-");

            // Собираем данные руководителя
            row.getCell(2).setText(getMentorFullInfo(a.getMentor()));
        }
    }

    private String getMentorFullInfo(Participant mentor) {
        User u = mentor.getUser();
        // ВАЖНО: Мы ищем сущность Teacher, связанную с этим User, чтобы достать регалии
        Teacher t = dataManager.load(Teacher.class)
                .query("select t from Teacher t where t.user = :user")
                .parameter("user", u)
                .fetchPlan(plan -> {
                    plan.add("degree", FetchPlan.BASE);
                    plan.add("title", FetchPlan.BASE);
                    plan.add("position", FetchPlan.BASE);
                })
                .optional().orElse(null);

        if (t == null) return u.getDisplayName();

        StringBuilder sb = new StringBuilder(u.getDisplayName());
        if (t.getDegree() != null) sb.append(", ").append(t.getDegree().getName());
        if (t.getTitle() != null) sb.append(", ").append(t.getTitle().getName());
        if (t.getPosition() != null) sb.append(", ").append(t.getPosition().getName());

        return sb.toString();
    }

    private Student getStudentByUser(User user) {
        return dataManager.load(Student.class)
                .query("select s from Student s where s.user = :user")
                .parameter("user", user)
                .fetchPlan("_base")
                .optional().orElse(null);
    }
}