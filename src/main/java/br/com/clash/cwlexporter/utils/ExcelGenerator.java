package br.com.clash.cwlexporter.utils;

import br.com.clash.cwlexporter.model.PlayerData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExcelGenerator {

    public void generatePlayerDataExcel(List<PlayerData> playerDataList, Workbook workbook, String sheetName) {
        Sheet sheet = workbook.createSheet(sheetName);

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle valueStyleWhite = createValueStyle(workbook, IndexedColors.WHITE);
        CellStyle valueStyleGray = createValueStyle(workbook, IndexedColors.GREY_25_PERCENT);

        Row warHeaderRow = sheet.createRow(0);
        for (int i = 2; i <= 14; i += 2) {
            Cell cell = warHeaderRow.createCell(i);
            cell.setCellValue("Guerra " + (i / 2));
            cell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, i, i + 1));
        }

        String[] headers = {"Tag", "Name", "Ataque", "Defesa", "Ataque", "Defesa", "Ataque", "Defesa",
                "Ataque", "Defesa", "Ataque", "Defesa", "Ataque", "Defesa", "Ataque", "Defesa", "ATK", "DEF", "TOTAL"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 2;
        for (PlayerData player : playerDataList) {
            Row row = sheet.createRow(rowNum);
            CellStyle valueStyle = (rowNum % 2 == 0) ? valueStyleWhite : valueStyleGray;

            createCell(row, 0, player.getTag(), valueStyle);
            createCell(row, 1, player.getName(), valueStyle);
            createCell(row, 2, player.getWar1().getAttackStars(), valueStyle);
            createCell(row, 3, player.getWar1().getDefenseStars(), valueStyle);
            createCell(row, 4, player.getWar2().getAttackStars(), valueStyle);
            createCell(row, 5, player.getWar2().getDefenseStars(), valueStyle);
            createCell(row, 6, player.getWar3().getAttackStars(), valueStyle);
            createCell(row, 7, player.getWar3().getDefenseStars(), valueStyle);
            createCell(row, 8, player.getWar4().getAttackStars(), valueStyle);
            createCell(row, 9, player.getWar4().getDefenseStars(), valueStyle);
            createCell(row, 10, player.getWar5().getAttackStars(), valueStyle);
            createCell(row, 11, player.getWar5().getDefenseStars(), valueStyle);
            createCell(row, 12, player.getWar6().getAttackStars(), valueStyle);
            createCell(row, 13, player.getWar6().getDefenseStars(), valueStyle);
            createCell(row, 14, player.getWar7().getAttackStars(), valueStyle);
            createCell(row, 15, player.getWar7().getDefenseStars(), valueStyle);
            createCell(row, 16, player.getTotalAttackStars(), valueStyle);
            createCell(row, 17, player.getTotalDefenseStars(), valueStyle);
            createCell(row, 18, player.getTotalStars(), valueStyle);

            rowNum++;
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createValueStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }
}
