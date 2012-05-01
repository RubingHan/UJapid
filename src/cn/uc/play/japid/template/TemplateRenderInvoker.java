package cn.uc.play.japid.template;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import play.exceptions.TemplateExecutionException;
import cn.bran.japid.compiler.NamedArgRuntime;
import cn.bran.japid.template.JapidTemplateBaseWithoutPlay;
import cn.bran.japid.template.RenderResult;
import cn.bran.japid.util.RenderInvokerUtils;

/**
 * 
 *
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-5-1
 */
public class TemplateRenderInvoker {
	
	/**
	 * @param <T>
	 * @param c
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static <T extends JapidTemplateBaseWithoutPlay> RenderResult invokeRender(
			Class<T> c, Object... args) {
		int modifiers = c.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			throw new RuntimeException(
					"Cannot init the template class since it's an abstract class: "
							+ c.getName());
		}
		try {
			Constructor<T> ctor = c.getConstructor(StringBuilder.class);
			StringBuilder sb = new StringBuilder(8000);
			T t = ctor.newInstance(sb);
			RenderResult rr = (RenderResult) RenderInvokerUtils.render(t, args);
			return rr;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Could not match the arguments with the template args.");
		} catch (InstantiationException e) {
			// e.printStackTrace();
			throw new RuntimeException(
					"Could not instantiate the template object. Abstract?");
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			Throwable te = e.getTargetException();
			if (te instanceof TemplateExecutionException)
				throw (TemplateExecutionException) te;
			Throwable cause = te.getCause();
			if (cause != null)
				if (cause instanceof RuntimeException)
					throw (RuntimeException) cause;
				else
					throw new RuntimeException(
							"error in running the renderer: "
									+ cause.getMessage(), cause);
			else if (te instanceof RuntimeException)
				throw (RuntimeException) te;
			else
				throw new RuntimeException("error in running the renderer: "
						+ te.getMessage(), te);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(
						"Could not invoke the template object: ", e);
			// throw new RuntimeException(e);
		}
	}
	
	public static <T extends JapidTemplateBaseWithoutPlay> RenderResult invokeNamedArgsRender(
			Class<T> c, NamedArgRuntime[] args) {
		int modifiers = c.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			throw new RuntimeException(
					"Cannot init the template class since it's an abstract class: "
							+ c.getName());
		}
		try {
			Constructor<T> ctor = c.getConstructor(StringBuilder.class);
			StringBuilder sb = new StringBuilder(8000);
			JapidTemplateBaseWithoutPlay t = ctor.newInstance(sb);
			RenderResult rr = (RenderResult) RenderInvokerUtils
					.renderWithNamedArgs(t, args);
			return rr;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Could not match the arguments with the template args.");
		} catch (InstantiationException e) {
			// e.printStackTrace();
			throw new RuntimeException(
					"Could not instantiate the template object. Abstract?");
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			Throwable e1 = e.getTargetException();
			throw new RuntimeException(
					"Could not invoke the template object:  ", e1);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(
						"Could not invoke the template object: ", e);
			// throw new RuntimeException(e);
		}
	}

}
