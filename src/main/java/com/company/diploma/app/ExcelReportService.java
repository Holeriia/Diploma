package com.company.diploma.app;

import com.company.diploma.entity.Group;
import com.company.diploma.entity.Student;
import com.company.diploma.entity.User;
import com.company.diploma.entity.Workspace;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelReportService {

    private final DataManager dataManager;

    public ExcelReportService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public byte[] generateWorkspaceStudentsReport(Workspace selectedWorkspace) throws IOException {
        // Перезагружаем Workspace с группами внутри сервиса
        Workspace workspace = dataManager.load(Workspace.class)
                .id(selectedWorkspace.getId())
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("groups", FetchPlan.BASE);
                })
                .one();

        try (Workbook workbook = new XSSFWorkbook()) {
            for (Group group : workspace.getGroups()) {
                createSheetForGroup(workbook, group, workspace);
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            workbook.write(stream);
            return stream.toByteArray();
        }
    }

    private void createSheetForGroup(Workbook workbook, Group group, Workspace workspace) {
        String sheetName = WorkbookUtil.createSafeSheetName(group.getName());
        Sheet sheet = workbook.createSheet(sheetName);

        // Стили
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // Шапка
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ФИО", "Телефон", "Почта", "СНИЛС", "Основа обучения"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Данные
        List<Student> students = dataManager.load(Student.class)
                .query("select s from Student s where s.group = :group " +
                        "and s.user in (select p.user from Participant p where p.workspace = :ws)")
                .parameter("group", group)
                .parameter("ws", workspace)
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("user", FetchPlan.BASE);
                })
                .list();

        int rowNum = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);
            User u = student.getUser();
            String fio = String.format("%s %s %s",
                    u.getLastName(), u.getFirstName(),
                    u.getPatronymic() != null ? u.getPatronymic() : "").trim();

            row.createCell(0).setCellValue(fio);
            row.createCell(1).setCellValue(u.getPhoneNumber() != null ? u.getPhoneNumber() : "");
            row.createCell(2).setCellValue(u.getEmail() != null ? u.getEmail() : "");
            row.createCell(3).setCellValue(student.getSnils() != null ? student.getSnils() : "");
            row.createCell(4).setCellValue(student.getBasisOfLearning() != null ?
                    student.getBasisOfLearning().name() : "");
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}