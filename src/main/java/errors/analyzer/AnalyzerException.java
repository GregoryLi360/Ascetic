package errors.analyzer;

public abstract class AnalyzerException extends Exception {
    public AnalyzerException(String message) {
        super(message);
    } 
}
