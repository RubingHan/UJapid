package cn.uc.play.japid.util;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class FileUtilTest {

	@Test
	public void testGetFileNameInPath() {
		String expectedFileName = "file.html";

		// Test for windows.
		String path1 = "c:\\Documents Folder\\path\\file.html";
		String fileName1 = FileUtils.getFileNameInPath(path1, "\\");
		System.out.println("fileName1=" + fileName1);
		Assert.assertEquals(expectedFileName, fileName1);

		// Test for Unix.
		String path2 = "/robin/path/file.html";
		String fileName2 = FileUtils.getFileNameInPath(path2, "/");
		System.out.println("fileName2=" + fileName2);
		Assert.assertEquals(expectedFileName, fileName2);

		if (File.separatorChar == '\\') {
			// Test for auto detect.
			String fileName1ByAuto = FileUtils.getFileNameInPath(path1);
			System.out.println("fileName1ByAuto=" + fileName1ByAuto);
			Assert.assertEquals(expectedFileName, fileName1ByAuto);
		}

		if (File.separatorChar == '/') {
			String fileName2ByAuto = FileUtils.getFileNameInPath(path2);
			System.out.println("fileName2ByAuto=" + fileName2ByAuto);
			Assert.assertEquals(expectedFileName, fileName2ByAuto);
		}
	}
	
	@Test
	public void testGetRelativePath(){
		String parentPath = "root";
		String path1 = "root/level1/level2/a.html";
		
		String relative1 = FileUtils.getRelativePath(path1, parentPath);
		Assert.assertEquals("level1/level2/a.html", relative1);
		
		String path2 = "level1/level2/a.html";
		String relative2 = FileUtils.getRelativePath(path2, parentPath);
		Assert.assertEquals("level1/level2/a.html", relative2);
	}
	
	@Test
	public void testConvertExtensionToJava(){
		String path1 = "/a/b/c.html";
		String pathJava1 = FileUtils.convertExtensionTo(path1, "java");
		Assert.assertEquals("/a/b/c.java", pathJava1);
		
		String path2 = "a/b/c";
		String pathJava2 = FileUtils.convertExtensionTo(path2, "java");
		Assert.assertEquals("a/b/c.java", pathJava2);
	}
	
	@Test
	public void testRemoveFileExtension(){
		String path1 = "a/b/c.html";
		String pathWithoutExt1 = FileUtils.removeFileExtension(path1);
		Assert.assertEquals("a/b/c", pathWithoutExt1);
	}
	
	@Test
	public void testConvertPathToPackage(){
		String path1 = "a/b/c.html";
		String pkg1 = FileUtils.convertPathToPackage(path1);
		
		Assert.assertEquals("a.b.c", pkg1);
	}

}
