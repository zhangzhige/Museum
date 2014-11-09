package com.example.museum;

import java.util.List;

public class Cultural {
	
	public List<ProductPicture> ProductPictures;
	
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
}
