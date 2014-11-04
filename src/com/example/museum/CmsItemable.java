/**
 * 
 */
package com.example.museum;


/**
 * @author Sven.Zhan
 * 配置界面，首页，电影tab等页面展示的Item对象
 */
public interface CmsItemable {

	public int getId();
	
	public String getTitle();
	
	public String getPosterUrl();
	
	public String getScore();

	public String getMessage();
	
	public String getSize();
}
