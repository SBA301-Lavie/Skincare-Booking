package lavie.skincare_booking.util;

import lavie.skincare_booking.model.MetaDataDTO;
import lavie.skincare_booking.model.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<ResponseDTO> getObject(Object result, HttpStatus status, String response) {
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .details(ExceptionUtils.getResponseString(response))
                        .content(result)
                        .statusCode(status.value())
                        .build()
                , status
        );
    }

    public static ResponseEntity<?> getCollection(Object result, HttpStatus status, String response
            , MetaDataDTO metaData) {
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .statusCode(status.value())
                        .details(ExceptionUtils.getResponseString(response))
                        .content(result)
                        .metaDataDTO(metaData)
                        .build()
                , status
        );
    }

    public static ResponseEntity<?> error(String error, String message, HttpStatus status) {
        return new ResponseEntity<>(
                ResponseDTO.builder()
                        .details(ExceptionUtils.getError(error))
                        .statusCode(status.value())
                        .build()
                , status
        );
    }
}
