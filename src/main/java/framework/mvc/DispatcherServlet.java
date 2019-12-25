package framework.mvc;

import framework.ioc.BeanFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Optional;

@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {
    private static final BeanFactory BEAN_FACTORY=new BeanFactory();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Inter DispatcherServlet");

        //1.获取Handler，获取Controller
        String requestURI = req.getRequestURI();
        Handler handler = HandlerMapping.getHandler(requestURI).get();
        Object controller = BEAN_FACTORY.getSingletonBean(handler.getClazz());

        //2.执行Handler、Controller
        Method method = handler.getMethod();
        String arg0 = method.getParameters()[0].getName();
        String arg1 = method.getParameters()[1].getName();
        String arg0Value = req.getParameter(arg0);
        Integer arg1Value = Integer.valueOf(req.getParameter(arg1));
        Object result=null;
        try {
            result = method.invoke(controller, arg0Value, arg1Value);
        }catch (Exception e){
            System.out.println(e.toString());
        }

        //3.返回结果
        //设定返回的内容的类型
        resp.setContentType("text/html;charset=utf-8");
        //输出动态内容，这个out对象输出的内容都是输出到浏览器
        PrintWriter out=resp.getWriter();
        if (null!=result){
            out.println(result);
        }else {
            out.println("null");
        }
        out.flush();
        out.close();

        System.out.println("Exit DispatcherServlet");
    }
}
