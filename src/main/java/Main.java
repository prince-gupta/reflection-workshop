import dto.Staff;
import mapper.ExcelImportResult;
import mapper.ExcelMappingService;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        ExcelMappingService excelMappingService = new ExcelMappingService();
        List<Object> mappedResults = excelMappingService.load();
        List<Staff> resultsToSave = mappedResults.stream().map(s -> (Staff) s).collect(Collectors.toList());
        List<ExcelImportResult> excelImportResults = excelMappingService.getResults();
    }
}
