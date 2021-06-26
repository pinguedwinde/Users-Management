package org.sliga.usersmanagement.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@AllArgsConstructor
public class HttpResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Europe/Paris")
    private Date timeStamp;
    private int statusCode;
    private HttpStatus httpStatus;
    private String reason;
    private String message;

    public HttpResponse(){
        this.timeStamp = new Date();
    }
    public static class Builder {
        private int statusCode;
        private HttpStatus httpStatus;
        private String reason;
        private String message;

        public Builder withStatusCode(int statusCode){
            this.statusCode = statusCode;
            return this;
        }

        public Builder withHttpStatus (HttpStatus httpStatus){
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder withReason (String reason){
            this.reason = reason;
            return this;
        }
        public Builder withMessage (String message){
            this.message = message;
            return this;
        }

        public HttpResponse build(){
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(this.statusCode);
            httpResponse.setHttpStatus(this.httpStatus);
            httpResponse.setReason(this.reason);
            httpResponse.setMessage(this.message);
            return httpResponse;
        }
    }
}
