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
		return "Cultural [Id=" + Id + ", Name=" + Name + ", Product3D=" + Product3D + "]";
	}

	public List<ProductPicture> ProductPictures;
	
	public List<ProductSpecificationAttribute> ProductSpecificationAttributes;
	
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
	}
}
