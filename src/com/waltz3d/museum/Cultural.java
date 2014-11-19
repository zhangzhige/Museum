package com.waltz3d.museum;

import java.util.List;

public class Cultural {
	
	public List<ProductPicture> ProductPictures;
	
	public List<ProductSpecificationAttribute> ProductSpecificationAttributes;
	
	class ProductPicture{
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
	
	public class ProductSpecificationAttribute{
		public int SpecificationAttributeOptionId;
		public String Name;
		public String Value;
		public int DisplayOrder;
		public int Id;
	}
}
