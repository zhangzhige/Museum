package com.waltz3d.museum;

import java.io.Serializable;
import java.util.List;

public class Cultural implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int Id;
	
	public String Name;
	
	public String Product3D;


	@Override
	public String toString() {
		return "Cultural [Id=" + Id + ", Name=" + Name + ", Product3D=" + Product3D + ", ProductPictures=" + ProductPictures + ", ProductSpecificationAttributes=" + ProductSpecificationAttributes
				+ "]";
	}

	public List<ProductPicture> ProductPictures;
	
	public List<ProductSpecificationAttribute> ProductSpecificationAttributes;
	
	
	public String getLocation(){
		if(ProductSpecificationAttributes != null && ProductSpecificationAttributes.size() > 0){
			for(ProductSpecificationAttribute mItem:ProductSpecificationAttributes){
				if(mItem.Name.equals("产地")){
					return mItem.Value;
				}
			}
		}
		return "昙石山";
	}
	
	/**
	 * 获取保护程度
	 * @return
	 */
	public String getLevel(){
		if(ProductSpecificationAttributes != null && ProductSpecificationAttributes.size() > 0){
			for(ProductSpecificationAttribute mItem:ProductSpecificationAttributes){
				if(mItem.Name.equals("级别")){
					return mItem.Value;
				}
			}
		}
		return "一级保护";
	}
	
	public String getdisability(){
		if(ProductSpecificationAttributes != null && ProductSpecificationAttributes.size() > 0){
			for(ProductSpecificationAttribute mItem:ProductSpecificationAttributes){
				if(mItem.Name.equals("残度")){
					return mItem.Value;
				}
			}
		}
		return "残";
	}
	
	class ProductPicture implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		int ProductId;
		int PictureId;
		String PictureUrl;
		int DisplayOrder;
		
		@Override
		public String toString() {
			return "ProductPicture [ProductId=" + ProductId + ", PictureId="
					+ PictureId + ", PictureUrl=" + PictureUrl
					+ ", DisplayOrder=" + DisplayOrder + "]";
		}
	}
	
	public class ProductSpecificationAttribute implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public int SpecificationAttributeOptionId;
		public String Name;
		public String Value;
		public int DisplayOrder;
		public int Id;
		@Override
		public String toString() {
			return "ProductSpecificationAttribute [SpecificationAttributeOptionId=" + SpecificationAttributeOptionId + ", Name=" + Name + ", Value=" + Value + ", DisplayOrder=" + DisplayOrder
					+ ", Id=" + Id + "]";
		}
		
		
	}
}
