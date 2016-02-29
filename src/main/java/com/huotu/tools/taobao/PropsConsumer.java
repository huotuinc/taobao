package com.huotu.tools.taobao;

import com.huotu.tools.taobao.entity.Category;

import java.io.IOException;
import java.util.List;

/**
 * @author CJ
 */
@FunctionalInterface
public interface PropsConsumer {

    void accept(List<Category> categoryList) throws IOException;

}
