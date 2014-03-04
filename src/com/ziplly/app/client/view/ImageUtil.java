package com.ziplly.app.client.view;

import com.ziplly.app.model.ImageDTO;

public class ImageUtil {

	/**
	 * This assumes UploadServlet is sending back the image data in the
	 * following format <serving_url>:<blob_key>
	 * 
	 * @param imageUrl
	 *            <serving_url>:<blob_key>
	 * @return {@link ImageDTO}
	 */
	public static ImageDTO parseImageUrl(String imageUrl) {
		ImageDTO image = new ImageDTO();
		String[] data = imageUrl.split(StringConstants.VALUE_SEPARATOR);
		if (data.length == 2) {
			image.setUrl(data[0].trim());
			image.setId(Long.parseLong(data[1].trim()));
		} else {
			throw new RuntimeException("Invalid image url format: "+imageUrl);
		}
		return image;
	}
}
