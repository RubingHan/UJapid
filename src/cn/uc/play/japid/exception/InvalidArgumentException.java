package cn.uc.play.japid.exception;

/**
 * 
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-5-2
 */
public class InvalidArgumentException extends RuntimeException {

	public InvalidArgumentException() {
		super();
	}

	public InvalidArgumentException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidArgumentException(String arg0) {
		super(arg0);
	}

	public InvalidArgumentException(Throwable arg0) {
		super(arg0);
	}

}
