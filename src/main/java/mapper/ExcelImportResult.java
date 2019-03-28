package mapper;

public class ExcelImportResult {

    public enum STATUS {
        SUCCESS("Success"),
        ERROR("Error"),
        WARNING("Warning"),
        VALIDATION_EXCEPTION("Validation Exception");

        private String displayValue;

        STATUS(String displayValue) {
            this.displayValue = displayValue;
        }
    }

    private STATUS status;

    private String rowNumber;

    private String cellNumber;

    private String comment;

    private static ExcelImportResult set(String comment, String rowNumber, String cellNumber) {
        ExcelImportResult obj = new ExcelImportResult();
        obj.comment = comment;
        obj.rowNumber = rowNumber;
        obj.cellNumber = cellNumber;
        return obj;
    }

    public static ExcelImportResult getErrorInstance(String rowNumber, String cellNumber, String comment) {
        ExcelImportResult obj = set(comment, rowNumber, cellNumber);
        obj.status = STATUS.ERROR;
        return obj;
    }

    public static ExcelImportResult getWarningInstance(String rowNumber, String cellNumber, String comment) {
        ExcelImportResult obj = set(comment, rowNumber, cellNumber);
        obj.status = STATUS.WARNING;
        return obj;
    }

    public static ExcelImportResult getValidationExecptionInstance(String rowNumber, String cellNumber, String comment) {
        ExcelImportResult obj = set(comment, rowNumber, cellNumber);
        obj.status = STATUS.VALIDATION_EXCEPTION;
        return obj;
    }

    public static ExcelImportResult getSuccessInstance(String rowNumber, String cellNumber, String comment) {
        ExcelImportResult obj = set(comment, rowNumber, cellNumber);
        obj.status = STATUS.SUCCESS;
        return obj;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(String rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
