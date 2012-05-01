package cn.uc.play.japid.template;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

/**
 * 
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-30
 */
public class TemplateLoaderFileImplTest {

	@Test
	public void testLoadTemplate() {

		try {
			UJapidTemplateLoader loader = new TemplateLoaderFileImpl("test", null,
					1000);
			UJapidTemplate temp = loader
					.loadTemplate("japidviews/Application/index.html");
			Assert.assertEquals("index.html", temp.name);
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	@Test
	public void testLoadAllTemplates() {
		try {
			UJapidTemplateLoader loader = new TemplateLoaderFileImpl("test/japidviews", null,
					1000);
			Map<String, UJapidTemplate> map = loader.loadAllTemplates();

			for (Map.Entry<String, UJapidTemplate> entry : map.entrySet()) {
				String path = entry.getKey();
				UJapidTemplate t = entry.getValue();

				System.out.println("path=" + path);
				System.out.println("name=" + t.name);
				System.out.println("name with path=" + t.nameWithPath);
			}
		} catch (Exception e) {
			fail(e.toString());
		}

	}
	
	@After
	public void recovery() throws IOException{
		FileWriter fw = null;
		
		try {
			fw = new FileWriter("test/japidviews/Application/a.html");
			fw.write("a");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			fw.close();
		}
	}

	@Test
	public void testGetTemplate() {

		FileWriter fw = null;
		try {
			UJapidTemplateLoader loader = new TemplateLoaderFileImpl("test/japidviews", null,
					1000);
			UJapidTemplate temp = loader
					.getTemplate("Application/a.html");

			Assert.assertEquals("a", temp.source);

			try {
				fw = new FileWriter("test/japidviews/Application/a.html");
				fw.write("bc");
				
				Thread.sleep(500);
				
			} finally {
				fw.close();
			}
			
			temp = loader.getTemplate("Application/a.html");
			
			Assert.assertEquals("a", temp.source);
			
			Thread.sleep(600);
			
			temp = loader.getTemplate("Application/a.html");
			Assert.assertEquals("bc", temp.source);

		} catch (Exception e) {
			fail(e.toString());
		}

	}

}
