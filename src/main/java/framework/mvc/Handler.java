package framework.mvc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class Handler {
    private String uri;
    private Class clazz;
    private Method method;
}
