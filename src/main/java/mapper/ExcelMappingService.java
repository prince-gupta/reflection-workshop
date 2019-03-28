package mapper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.ReflectionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class ExcelMappingService {


    private List<ExcelImportResult> results = new ArrayList<>();

    public List<Object> load() {
        try {
            this.results.clear();
            JAXBContext context = JAXBContext.newInstance(Mappings.class);
            Mappings mappings = (Mappings) context.createUnmarshaller().unmarshal(loadFile("/Users/princegupta/Documents/local/Reflection/src/main/resources/staff-mapper.xml"));
            return parse(mappings, loadFile("/Users/princegupta/Documents/local/Reflection/src/main/resources/staff.xlsx"));
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private InputStream loadFile(String filePath) throws IOException {
        return new FileInputStream(new File(filePath));
    }

    private List<Object> parse(Mappings mappings, InputStream stream) {
        String targetClassName = mappings.getMap().getClazz();
        List<Object> objList = new ArrayList<>();
        try {
            Class<?> cls = Class.forName(targetClassName);
            Map<Integer, Map<String, Object>> rowList = parse(stream, mappings.getMap());
            rowList.forEach((_i, _e) -> {
                try {
                    Object obj = cls.newInstance();
                    _e.forEach((_k, _ei) -> ReflectionUtils.callSetter(obj, _k, _ei));
                    objList.add(obj);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return objList;
    }

    private Map<Integer, Map<String, Object>> parse(InputStream stream, Mappings.Map map) throws IOException {
        Workbook workbook = new XSSFWorkbook(stream);

        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, Map<String, Object>> indexMap = new HashMap<>();
        Map<String, Object> cellMap = new HashMap<>();

        Integer counter = 0;
        Iterator<Row> rowItr = sheet.rowIterator();
        while (rowItr.hasNext()) {
            if (counter == 0) {
                counter++;
                rowItr.next();
                continue;
            }
            Row row = rowItr.next();
            for (Mappings.Map.Row.Cell cell : map.getRow().getCell()) {
                try {
                    Cell eCell = row.getCell(cell.getIndex());
                    Object data;
                    if (eCell != null) {
                        switch (eCell.getCellType()) {
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(eCell)) {
                                    data = performValidations(cell, convertToTargetDataType(CellType.NUMERIC, cell.getDataType(), eCell.getDateCellValue(), true));
                                } else {
                                    data = performValidations(cell, convertToTargetDataType(CellType.NUMERIC, cell.getDataType(), eCell.getNumericCellValue(), false));
                                }
                                break;
                            case BOOLEAN:
                                data = performValidations(cell, convertToTargetDataType(CellType.BOOLEAN, cell.getDataType(), eCell.getBooleanCellValue(), false));
                                break;
                            case STRING:
                            default:
                                data = performValidations(cell, eCell.getRichStringCellValue().getString());
                                break;
                        }
                        cellMap.put(cell.getTargetField(), data);
                    } else if (cell.getRequired().equals("true")) {
                        results.add(ExcelImportResult.getErrorInstance(row.getRowNum() + "", cell.getIndex() + "", "Value is mandatory."));
                    }
                } catch (IllegalArgumentException e) {
                    results.add(ExcelImportResult.getValidationExecptionInstance(counter.toString(), (cell.getIndex() + 1) + "", e.getMessage()));
                } catch (Exception e) {
                    results.add(ExcelImportResult.getErrorInstance(counter.toString(), (cell.getIndex() + 1) + "", e.getMessage()));
                }
            }
            counter++;
            // results.add(ExcelImportResult.getSuccessInstance(counter, 0,"Parsed"));
            indexMap.put(counter, new HashMap<>(cellMap));
        }
        return indexMap;
    }


    private Object convertToTargetDataType(CellType sourceDataType, String targetDataType, Object value, Boolean isDate) throws Exception {
        Object targetValue = new Object();
        targetDataType = "java.lang." + targetDataType;
        try {
            switch (sourceDataType) {
                case NUMERIC:
                    if (Integer.class.equals(Class.forName(targetDataType))) {
                        targetValue = ((Double) value).intValue();
                    } else if (String.class.equals(Class.forName(targetDataType))) {
                        targetValue = value.toString();
                    } else if (isDate && Date.class.equals(Class.forName(targetDataType))) {
                        targetValue = ((Date) value).toString();
                    } else {
                        targetValue = value;
                    }
                    break;
                case BOOLEAN:
                    if (String.class.equals(Class.forName(targetDataType))) {
                        targetValue = ((Integer) value).toString();
                    } else {
                        targetValue = value;
                    }
                    break;
                case STRING:
                    if (Integer.class.equals(Class.forName(targetDataType))) {
                        targetValue = Integer.parseInt(value.toString());
                    } else if (Double.class.equals(Class.forName(targetDataType))) {
                        targetValue = Double.parseDouble(value.toString());
                    }
                    break;
            }
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        }
        return targetValue;
    }

    private Object performValidations(Mappings.Map.Row.Cell cell, Object value) throws IllegalArgumentException {
        if (value instanceof String) {
            String castedValue = (String) value;
            if ((cell.getMinLenght() > castedValue.length() || (cell.getMaxLength() != -1 && cell.getMaxLength() < castedValue.length()))) {
                throw new IllegalArgumentException("Provided Value is not in length range of Min = " + cell.getMinLenght() + " Max = " + cell.getMaxLength());
            }
            String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
            if ("email".equals(cell.getRestirctionType()) && !Pattern.matches(EMAIL_REGEX, castedValue)) {
                throw new IllegalArgumentException("Provided Value \" " + castedValue + " \" is not a valid email.");
            }
        } else if (value instanceof Integer) {
            Integer castedValue = (Integer) value;
            if ((cell.getMinLenght() > castedValue || (cell.getMaxLength() != -1 && cell.getMaxLength() < castedValue))) {
                throw new IllegalArgumentException("Provided Value is not in range of Min = " + cell.getMinLenght() + " Max = " + cell.getMaxLength());
            }
        }
        return value;
    }

    public List<ExcelImportResult> getResults() {
        return results;
    }
}

