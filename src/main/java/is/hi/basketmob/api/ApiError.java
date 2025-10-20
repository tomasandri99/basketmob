package is.hi.basketmob.api;
public class ApiError { public String code; public String message;
    public ApiError() {}
    public ApiError(String code, String message){ this.code = code; this.message = message; }
    public static ApiError of(String code, String message){ return new ApiError(code, message); }
}

