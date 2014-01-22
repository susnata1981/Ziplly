package com.ziplly.app.server;

import java.io.IOException;
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
import com.google.inject.Singleton;
import com.ziplly.app.client.view.StringConstants;

@Singleton
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(UploadServlet.class.getCanonicalName());
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		
		// "image" name of the input[type="file"] form field.
		List<BlobKey> blobKeys = blobs.get("image");

		logger.log(Level.INFO, "doPost method called with blobKey: "+blobKeys);
		
		if (blobKeys == null || blobKeys.size() == 0) {
			logger.log(Level.ERROR, "Didn't get the blob key");
		}
		String uploadEndpoint = System.getProperty(StringConstants.UPLOAD_ENDPOINT);
		ImagesService imageService = ImagesServiceFactory.getImagesService();
		res.sendRedirect(uploadEndpoint+"?imageUrl="+imageService.getServingUrl(
				ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0))));
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String imageUrl = req.getParameter("imageUrl");
		res.setHeader("Content-Type", "text/html");
//		res.setContentType("text/html");
		res.getWriter().println(imageUrl);
	}
}
