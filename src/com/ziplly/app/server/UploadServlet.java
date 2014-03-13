package com.ziplly.app.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Singleton;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.dao.ImageDAO;
import com.ziplly.app.dao.ImageDAOImpl;
import com.ziplly.app.model.Image;
import com.ziplly.app.model.RecordStatus;

@Singleton
public class UploadServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(UploadServlet.class.getCanonicalName());
	private static final long serialVersionUID = 1L;
	private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private final ImageDAO imageDao;
	private final ImagesService imageService = ImagesServiceFactory.getImagesService();

	public UploadServlet() {
		imageDao = new ImageDAOImpl();
	}

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
		String imageUrl = req.getParameter(StringConstants.IMAGE_URL_KEY);
		String imageId = req.getParameter(StringConstants.IMAGE_ID);

		logger
		.log(Level.INFO, String.format(
				"doGet method called with " + "image url %s, imageId %s",
				imageUrl,
				imageId));

		String response = imageUrl + StringConstants.VALUE_SEPARATOR + imageId;
		res.setHeader("Content-Type", "text/html");
		res.setContentType("text/html");
		res.getWriter().println(response);
		res.getWriter().flush();
		//		res.getWriter().close();
	}

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);

		// "image" name of the input[type="file"] form field.
		List<BlobKey> blobKeys = blobs.get("image");

		logger.log(Level.INFO, "doPost method called with blobKey: " + blobKeys);

		if (blobKeys == null || blobKeys.size() == 0) {
			logger.log(Level.ERROR, "Didn't get the blob key");
			// TODO: handle it.
			res.sendRedirect("");
		}

		// Save the image
		try {
			Image image = new Image();
			image
			.setUrl(imageService.getServingUrl(ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0))));
			image.setBlobKey(blobKeys.get(0).getKeyString());
			image.setStatus(RecordStatus.ACTIVE);
			image.setTimeCreated(new Date());
			imageDao.save(image);

			// res.setHeader("Access-Control-Allow-Methods", "POST");
			// res.setHeader("Access-Control-Allow-Origin", "*");
			// Redirect (302)
			res.sendRedirect(getImageUrl(image));
		} catch (RuntimeException ex) {
			res.sendRedirect("");
		}
	}

	private String getImageUrl(final Image image) {
		String uploadEndpoint = System.getProperty(ZipllyServerConstants.UPLOAD_ENDPOINT);

		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			uploadEndpoint = "http://127.0.0.1:8888/ziplly/upload";
		}

		String url =
				uploadEndpoint + "?" + StringConstants.IMAGE_URL_KEY + "=" + image.getUrl() + "&"
						+ StringConstants.IMAGE_ID + "=" + image.getId();
		return url;
	}
}
