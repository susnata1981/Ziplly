package com.ziplly.app.server;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ziplly.app.client.exceptions.CouponAlreadyUsedException;
import com.ziplly.app.client.exceptions.InvalidCouponException;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.model.Session;
import com.ziplly.app.server.bli.CouponBLI;
import com.ziplly.app.server.bli.SessionBLI;
import com.ziplly.app.server.bli.ServiceModule.ApplicationLoginUrl;

@Singleton
public class RedeemCouponServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
	private CouponBLI couponBli;
	private SessionBLI sessionBli;
	private String applicationLoginPageUrl;

  @Inject
  public RedeemCouponServlet(
  		CouponBLI couponBli,
  		SessionBLI sessionBli,
  		@ApplicationLoginUrl String applicationLoginPageUrl) {
  	this.couponBli = couponBli;
  	this.sessionBli = sessionBli;
  	this.applicationLoginPageUrl = applicationLoginPageUrl;
  }
  
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		String code = req.getParameter("code");
		checkNotNull(code);
		
		try {
			Session session = sessionBli.validateSession();
			couponBli.redeemCoupon(code, session.getAccount().getAccountId());
		} catch(NeedsLoginException ex) {
			Window.alert("Please login first");
			Window.Location.assign(applicationLoginPageUrl);
			return;
		} catch (InvalidCouponException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (CouponAlreadyUsedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}
}
