package cn.uc.play.japid.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.uc.play.japid.UJapidPlugin;

import play.Play;

/**
 * Used to create UJapidTemplateLoader.
 *
 * @author Robin Han<sakuyahan@163.com>
 * @date 2012-4-29
 */
public class UJapidTemplateLoaderFactory {
	static UJapidTemplateLoader customTemplateLoader;
	
	private UJapidTemplateLoaderFactory(){}
	
	/**
	 * If set a custom TemplateLoader object, the factory will always return this object.
	 * After setting the TemplateLoader object once, this method will ignore other settings.
	 * @param customTemplateLoader
	 */
	public static void setCustomTemplateLoaderOnce(UJapidTemplateLoader customTemplateLoader){
		if (customTemplateLoader == null)
			UJapidTemplateLoaderFactory.customTemplateLoader = customTemplateLoader;
	}
	
	static UJapidTemplateLoader createdTemplateLoader; 
	
	/**
	 * This method only creating TemplateLoader object once.
	 * After the first creating, this method will always return the first object.
	 * @return Created TemplateLoader object.
	 * @throws IOException 
	 */
	public static UJapidTemplateLoader create() throws IOException{
		
		if (createdTemplateLoader != null)
			return createdTemplateLoader;
		
		if (customTemplateLoader != null){
			createdTemplateLoader = customTemplateLoader;
			return customTemplateLoader;
		}
		
		
		String mode = Play.configuration.getProperty("ujapid.mode", "file").toLowerCase();
		long nativeCacheExpire = Long.parseLong(Play.configuration.getProperty("ujapid.navitecache.expire","-1"));
		
		if (mode.equals("db")){
			long remoteCacheExpire = Long.parseLong(Play.configuration.getProperty("ujapid.remotecache.expire","-1"));
			createdTemplateLoader = new TemplateLoaderMysqlImpl(UJapidPlugin.ROOT, nativeCacheExpire, remoteCacheExpire);
		}else if (mode.equals("file")){
			String filter = Play.configuration.getProperty("ujapid.filter", UJapidTemplateLoader.FILE_FILTER);
			createdTemplateLoader = new TemplateLoaderFileImpl( UJapidPlugin.ROOT, filter, nativeCacheExpire);
		}
		return createdTemplateLoader;
	}

}
