package com.atlassian.tutorial;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;


public class HelloWorld extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(HelloWorld.class);
    //private String issueSummary;
    private static final String TEST_TEMPLATE="/templates/hello.vm";
    private static final String TEST_TEMPLATE_A="/templates/answer.vm";

    @JiraImport
    private final TemplateRenderer templateRenderer;
    String issuekey;
    String errormessage;




    public HelloWorld(TemplateRenderer templateRenderer)
    {
        this.templateRenderer=templateRenderer;
        this.errormessage=" ";



    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        resp.setContentType("text/html");
        Map<String, Object> context = new HashMap<>();
        context.put("errorMessage",errormessage);
        templateRenderer.render(TEST_TEMPLATE,context,resp.getWriter());

        //RequestDispatcher dispatcher = req.getRequestDispatcher("/issuesummary");
        //dispatcher.forward(req, resp);
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try {
            String issuekey = req.getParameter("issuekey").trim();

            //HttpSession session=req.getSession();
            //session.setAttribute("issuekey",issuekey);


            //resp.sendRedirect("/jira/plugins/servlet/issuesummary");
            String issueSummary = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issuekey).getSummary();
            String issueDescription=ComponentAccessor.getIssueManager().getIssueByCurrentKey(issuekey).getDescription();
            Map<String, Object> context = new HashMap<>();
            context.put("issueSummary", issueSummary);
            context.put("issueDescription",issueDescription);
            resp.setContentType("text/html");
            templateRenderer.render(TEST_TEMPLATE_A, context, resp.getWriter());
            errormessage="";


        }
        catch (NullPointerException e) {
            System.out.print("Caught the NullPointerException");
            resp.sendRedirect(req.getHeader("referer"));
            errormessage="Enter Correct data";


        }
    }



}