package framework.mvc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
public class HandlerMapping {
    private static final List<Handler> HANDLER_LIST = new LinkedList<Handler>();

    /**
     * 获取Handler
     *
     * @param uri
     * @return
     */
    public static Optional<Handler> getHandler(final String uri) {
        return HANDLER_LIST.stream()
                .filter(handler -> handler.getUri().equals(uri))
                .findFirst();
    }

    /**
     * 添加Handler
     *
     * @param handler
     */
    public static void addHandler(Handler handler) {
        HANDLER_LIST.add(handler);
    }
}
