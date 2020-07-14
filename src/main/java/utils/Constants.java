package utils;

public class Constants {

    private Constants() {}

    /** CONFIG OPTIONS */
    public static final int HTTP_PORT = 8082;

    /** API Routes */
    public static final String API_GET_SYMBOLS = "/symbols";
    public static final String API_GET_LATEST_RATE = "/latest";
    public static final String API_GET_HISTORICAL_RATE = "/:date";
    public static final String API_GET_CONVERT = "/convert";
}
