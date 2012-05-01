package cn.uc.play.japid.template;

import java.util.List;
import java.util.Map;

import play.db.DB;

/**
 * Load template(s) from mysql. 
 *
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-29
 */
public class TemplateLoaderMysqlImpl implements UJapidTemplateLoader{
	
	public TemplateLoaderMysqlImpl(){
	}
	
	@Override
	public UJapidTemplate loadTemplate(String name) {
		return null;
	}

	@Override
	public Map<String, UJapidTemplate> loadAllTemplates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UJapidTemplate getTemplate(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
