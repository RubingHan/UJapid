package cn.uc.play.japid.template;

import static org.junit.Assert.*;

import org.junit.Test;

import play.Play;

/**
 * 
 *
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-30
 */
public class UJapidTemplateCompilerTest {

	@Test
	public void testTransformToJava() {
		
		
		UJapidTemplateCompiler compiler = UJapidTemplateCompiler.getInstance("test");
		compiler.resetImports();
		try {
			compiler.transformToJava("japidviews/Application/test/b.html");
		} catch (Exception e) {
			fail(e.toString());
		}
	}

}
