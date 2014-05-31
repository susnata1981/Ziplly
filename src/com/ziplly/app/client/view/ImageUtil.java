package com.ziplly.app.client.view;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.RecordStatus;

public class ImageUtil {

	/**
	 * This assumes UploadServlet is sending back the image data in the following
	 * format <serving_url>:<blob_key>
	 * 
	 * @param imageUrl
	 *          <serving_url>:<blob_key>
	 * @return {@link ImageDTO}
	 */
	public static ImageDTO parseImageUrl(String imageUrl) {
		ImageDTO image = new ImageDTO();
		String[] data = imageUrl.split(StringConstants.VALUE_SEPARATOR);
		System.out.println(data);
		if (data.length == 2) {
			image.setUrl(data[0].trim());
			image.setId(Long.parseLong(data[1].trim()));
			image.setBlobKey(data[0].trim());
			image.setStatus(RecordStatus.ACTIVE);
		} else {
			throw new RuntimeException("Invalid image url format: " + imageUrl);
		}
		return image;
	}
	
	public static String getImageUtil(AccountDTO account) {
		return account.getProfileImage() != null ? 
				account.getProfileImage().getUrl() + "=s300": "images/no-photo.jpg";
	}
}
