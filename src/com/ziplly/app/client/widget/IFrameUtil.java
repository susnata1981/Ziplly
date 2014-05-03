package com.ziplly.app.client.widget;

import com.google.gwt.dom.client.IFrameElement;

public class IFrameUtil {
	
	public static final native void fillIframe(IFrameElement iframe, String content) /*-{
	  var doc = iframe.document;

	  if(iframe.contentDocument)
	    doc = iframe.contentDocument; // For NS6
	  else if(iframe.contentWindow)
	    doc = iframe.contentWindow.document; // For IE5.5 and IE6

	   // Put the content in the iframe
//	   doc.
//	  doc.open();
//	  doc.writeln(content);
//	  doc.close();
	}-*/;
}
