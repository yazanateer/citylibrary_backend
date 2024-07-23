package RESTfulApi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException{

	private static final long serialVersionUID = -2915218112463261184L;
	
	public BadRequestException() {
		
	}
	
	public BadRequestException(String msg) {
		super(msg);
	}
	
	public BadRequestException(Throwable err) { 
		super(err);
	}
	
	public BadRequestException(String msg, Throwable err) { 
		super(msg, err); 
	}
}
