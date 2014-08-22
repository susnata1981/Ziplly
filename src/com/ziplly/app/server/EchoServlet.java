package com.ziplly.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

@Singleton
public class EchoServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    resp.getWriter().println("Hello! This works!");
    resp.getWriter().flush();
    resp.getWriter().close();
  }

  private static final long serialVersionUID = 1L;

}
