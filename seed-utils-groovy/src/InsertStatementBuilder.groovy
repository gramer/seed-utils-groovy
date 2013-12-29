import lombok.Getter
import lombok.Setter

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

import com.google.common.collect.Lists


@Getter
@Setter
class InsertStatementBuilder {

	def targetSheets = [];
	
	def List<String> build(InputStream input) {
		def result = [];
		final def LINE_SEPARATOR = System.getProperty("line.separator");
		Workbook workbook = WorkbookFactory.create(input);
		(0..workbook.getNumberOfSheets()-1).each {
			Sheet sheet = workbook.getSheetAt(it);
			if (targetSheets.contains(sheet.getSheetName())) {
				result += build(sheet);
				result += LINE_SEPARATOR;
			}
		}
		
		return result;
	}
	
	def List<String> build(Sheet sheet) {
		def result = [];
		Row headerRow = sheet.getRow(0);
		
		final int lastColumnIdx = headerRow.getLastCellNum();
		final int lastRowIdx = sheet.getLastRowNum();
		List<ColumnMeta> columnMetas = createColumnMetas(headerRow, lastColumnIdx);
		
		(1..lastRowIdx).each { 
			result << createInsertStatementString(sheet.getSheetName(), columnMetas, sheet.getRow(it), lastColumnIdx);
		}
		
		return result;
	}
	
	def List<ColumnMeta> createColumnMetas(Row headerRow, final int lastColumnIdx) {
		List<ColumnMeta> result = Lists.newArrayList();
		
		(0..lastColumnIdx-1).each { i ->
			String cellValue = headerRow.getCell(i).getStringCellValue();
			String[] metaStrings = cellValue.split(":");
			if (metaStrings.length == 0) {
				throw new RuntimeException(String.format("첫번째 행의 셀은 name:data_type 형식입니다. invalid value[%s]", cellValue));
			} else if (metaStrings.length == 1) {
				result << new ColumnMeta(metaStrings[0], DataType.STRING);
			} else {
				result << new ColumnMeta(metaStrings[0], DataType.from(metaStrings[1]));
			}
		}
		
		return result;
	}
	
	def String createInsertStatementString(String sheetName, List<ColumnMeta> metas, Row row, final int columnIndex) {
		if (row == null)
			return "";
		
		def buffer = new StringBuffer();
		try {
			def firstColumnMeta = metas.get(0);
			buffer << "insert into $sheetName ($firstColumnMeta.name";
			
			(metas[1..-1]).each {
				buffer << ",";
				buffer << it.getName();
			}
			
			buffer << ") values (";
			buffer << getStringByCellType(row.getCell(0));
			
			(1..columnIndex).each {
				buffer << ",";
				buffer << getStringByCellType(row.getCell(it));
			}
			
			buffer << ");";
			return buffer;
		} catch(Exception e) {
			println "sheetName=[$sheetName], row=[$row]colIndex=[$columnIndex]";
			throw new RuntimeException(e);
		}
		
	}
	
	def String getStringByCellType(Cell cell) {
		if (cell == null) {
			return "null";
		}
		
		final int CELL_TYPE = cell.getCellType();
		switch(CELL_TYPE) {
		case Cell.CELL_TYPE_BLANK:
			return "null";
		case Cell.CELL_TYPE_STRING:
			String value = cell.getRichStringCellValue().toString();
			return value == null || value.isEmpty() ? "null" :  "'$value'";
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		default:
			def typeString = cell.getCellType();
			throw new IllegalArgumentException("지원하지 않는 타입입니다. invalid value : $typeString");
		}
	}

}
