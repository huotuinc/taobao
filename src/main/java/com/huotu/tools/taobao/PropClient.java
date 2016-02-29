package com.huotu.tools.taobao;

import com.huotu.tools.taobao.entity.Category;
import com.huotu.tools.taobao.entity.DatabaseStorage;
import com.huotu.tools.taobao.http.ListChildrenHandler;
import com.huotu.tools.taobao.http.PropDetailHandler;
import com.huotu.tools.taobao.http.TopPropHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author CJ
 */
public class PropClient {

    private static PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(10, TimeUnit.SECONDS);

    public HttpClient requestClient() {
        return HttpClientBuilder
                .create()
                .setConnectionManager(manager)
//                .addInterceptorFirst(authorise)
//                .addInterceptorFirst((HttpRequest request, HttpContext context) -> {
//                    log.debug(request);
//                })
//                .addInterceptorFirst((HttpResponse response, HttpContext context) -> {
//                    log.debug(response);
//                })
                .build();
    }

    public List<Category> topProp(boolean sandbox) throws IOException {

        HttpGet topProp = new HttpGet("https://open.taobao.com/apitools/apiPropTools.htm");

        return requestClient().execute(topProp, new TopPropHandler(sandbox));
    }

    public List<Category> listChildren(Category category) throws IOException{
        HttpGet api = new HttpGet("https://open.taobao.com/apitools/ajax_props.do?cid="+ category.getId()+"&act=childCid&restBool=false");

        return requestClient().execute(api,new ListChildrenHandler());
    }

    public Set<DatabaseStorage> detailProp(Category category) throws IOException {
        //https://open.taobao.com/apitools/ajax_props.do?act=props&cid=121042001&restBool=false
        HttpGet api = new HttpGet("https://open.taobao.com/apitools/ajax_props.do?cid="+ category.getId()+"&act=props&restBool=false");
        return requestClient().execute(api,new PropDetailHandler());
    }

}
