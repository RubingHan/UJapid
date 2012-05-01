package cn.uc.play.japid.exception;

public class TemplateCompileException extends RuntimeException {

	public TemplateCompileException() {
		super();
	}

	public TemplateCompileException(String templateName, Throwable cause) {
		super(templateName, cause);
	}

	public TemplateCompileException(String templateName) {
		super(templateName);
	}

	public TemplateCompileException(Throwable cause) {
		super(cause);
	}

}
