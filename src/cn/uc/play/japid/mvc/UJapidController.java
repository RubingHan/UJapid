package cn.uc.play.japid.mvc;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.bran.japid.compiler.NamedArgRuntime;
import cn.bran.japid.template.RenderResult;
import cn.bran.play.JapidResult;
import cn.bran.play.JapidTemplateBase;
import cn.uc.play.japid.UJapidPlugin;
import cn.uc.play.japid.template.UJapidTemplate;

import play.Play;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Http.Request;

/**
 * Hide UJapid template API. Some code copied from Bing Ran's
 * JapidController.java.
 * 
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-30
 * 
 * @author Bing Ran<bing_ran@hotmail.com>
 */
public class UJapidController extends Controller {
	/**
	 * pickup the Japid renderer in the conventional location and render it.
	 * Positional match is used to assign values to parameters
	 * 
	 * TODO: the signature would be confusing for cases where there is a single
	 * argument and the type is an array! In that case the user must cast it to
	 * Object: <code>renderJapid((Object)myArray);</code>
	 * 
	 * @param objects
	 */
	protected static void renderJapid(Object... objects) {
		String action = template();
		renderJapidWith(action, objects);
	}

	public static void renderJapidWith(String template, Object... args) {
		RenderResult rr = getRenderResultWith(template, args);
		JapidResult jr = new JapidResult(rr);
		throw jr;
	}

	/**
	 * just hide the result throwing
	 * 
	 * @param rr
	 */
	protected static void render(RenderResult rr) {
		throw new JapidResult(rr);
	}
	
	protected static void renderJapidByName(NamedArgRuntime... namedArgs) {
		String action = template();
		renderJapidWith(action, namedArgs);
	}

	protected static void renderJapidEager(Object... objects) {
		String action = template();
		renderJapidWithEager(action, objects);
	}

	public static void renderJapidWith(String template,
			NamedArgRuntime[] namedArgs) {
		throw new JapidResult(getRenderResultWith(template, namedArgs));
	}

	public static void renderJapidWithEager(String template, Object... args) {
		throw new JapidResult(getRenderResultWith(template, args)).eval();
	}
	
	/**
	 * render a text in a RenderResult so it can work with invoke tag in
	 * templates.
	 * 
	 * @param s
	 */
	protected static void renderText(String s) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "text/plain; charset=utf-8");
		render(new RenderResult(headers, new StringBuilder(s), -1L));
	}

	protected static void renderText(Object o) {
		String str = o == null ? "" : o.toString();
		renderText(str);
	}

	protected static void renderText(int o) {
		renderText(new Integer(o));
	}

	protected static void renderText(long o) {
		renderText(new Long(o));
	}

	protected static void renderText(float o) {
		renderText(new Float(o));
	}

	protected static void renderText(double o) {
		renderText(new Double(o));
	}

	protected static void renderText(boolean o) {
		renderText(new Boolean(o));
	}

	protected static void renderText(char o) {
		renderText(new String(new char[] { o }));
	}


	protected static String template() {
		// the super.template() class uses current request object to determine
		// the caller and method to find the matching template
		// this won't work if the current method is called from another action.
		// let's fall back to use the stack trace to deduce the template.
		// String caller2 = StackTraceUtils.getCaller2();

		final StackTraceElement[] stes = new Throwable().getStackTrace();
		// let's iterate back in the stacktrace to find the recent action calls.
		for (StackTraceElement st : stes) {
			String controller = st.getClassName();
			String action = st.getMethodName();
			ApplicationClass conAppClass = Play.classes
					.getApplicationClass(controller);
			if (conAppClass != null) {
				Class controllerClass = conAppClass.javaClass;
				if (UJapidController.class.isAssignableFrom(controllerClass)) {
					Method actionMethod = /* Java. */findActionMethod(action,
							controllerClass);
					if (actionMethod != null) {
						controller = controller.replace(".", File.separator);
						String expr = controller + File.separator + action;
						// content negotiation
						String format = Request.current().format;
						if ("html".equals(format)) {
							return expr + ".html";
						} else {
							String expr_format = expr + "." + format;
							if (expr_format.startsWith("controllers"
									+ File.separator)) {
								expr_format = "japidviews" + File.separator
										+ expr_format;
							}
							ApplicationClass appClass = Play.classes
									.getApplicationClass(expr_format);
							if (appClass != null)
								return expr_format;
							else {
								// fallback
								return expr;
							}
						}
					}
				}
			}
		}
		throw new RuntimeException(
				"The calling stack does not contain a valid controller. Should not have happended...");
	}

	/**
	 * copies from the same method in the Java class. Removed the public
	 * requirement for easier chaining.
	 * 
	 * @param name
	 * @param clazz
	 * @return
	 */
	public static Method findActionMethod(String name, Class clazz) {
		while (!clazz.getName().equals("java.lang.Object")) {
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.getName().equalsIgnoreCase(name) /*
														 * &&
														 * Modifier.isPublic(m
														 * .getModifiers())
														 */) {
					// Check that it is not an intercepter
					if (!m.isAnnotationPresent(Before.class)
							&& !m.isAnnotationPresent(After.class)
							&& !m.isAnnotationPresent(Finally.class)) {
						return m;
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

	/**
	 * @param template
	 * @return
	 */
	private static String getTemapletIndexName(String template) {
		if (template == null || template.length() == 0) {
			template = template();
		}

		// String action = StackTraceUtils.getCaller(); // too tricky to use
		// stacktrace to track the caller action name
		// something like controllers.japid.SampleController.testFindAction

		if (template.startsWith("@")) {
			// a template in the current directory
			template = Request.current().controller + File.separator
					+ template.substring(1);
		}

		// map to default japid view
		if (template.startsWith("controllers" + File.separator)) {
			template = template.substring(template.indexOf(File.separator) + 1);
		}
		String templateClassName = template
				.startsWith(UJapidPlugin.JAPIDVIEWS_ROOT) ? template
				: UJapidPlugin.JAPIDVIEWS_ROOT + File.separator + template;

		return templateClassName;
	}

	public static RenderResult getRenderResultWith(String templateName,
			Object... args) {
		String nameWithJapidRoot = getTemapletIndexName(templateName);

		try {
			UJapidTemplate template = UJapidPlugin.templateLoader
					.getTemplate(nameWithJapidRoot);

			return template.render(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	public static RenderResult getRenderResultWith(String templateName,
			NamedArgRuntime[] args) {
		
		String nameWithJapidRoot = getTemapletIndexName(templateName);

		try {
			UJapidTemplate template = UJapidPlugin.templateLoader
					.getTemplate(nameWithJapidRoot);

			return template.renderWithNamedArgs(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
