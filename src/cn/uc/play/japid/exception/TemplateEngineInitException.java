package cn.uc.play.japid.exception;

/**
 * Any exceptions thrown when initialize template engine, then throw this one.
 *
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-30
 */
public class TemplateEngineInitException extends RuntimeException{

	public TemplateEngineInitException() {
		super();
	}

	public TemplateEngineInitException(String message, Throwable e) {
		super(message, e);
	}

	public TemplateEngineInitException(String message) {
		super(message);
	}

	public TemplateEngineInitException(Throwable e) {
		super(e);
	}

}
