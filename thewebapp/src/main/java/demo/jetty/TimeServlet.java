package demo.jetty;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/time/")
public class TimeServlet extends HttpServlet
{
    @Override
    public void init() throws ServletException
    {
        super.init();
        System.err.println("INIT: " + TimeServlet.class.getName());
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Locale locale = req.getLocale();
        Date date = new Date();
        String dateStr = DateFormat.getDateInstance(DateFormat.DEFAULT,locale).format(date);
        String timeStr = DateFormat.getTimeInstance(DateFormat.DEFAULT,locale).format(date);
        resp.getWriter().println(dateStr + ' ' + timeStr);
    }
}
