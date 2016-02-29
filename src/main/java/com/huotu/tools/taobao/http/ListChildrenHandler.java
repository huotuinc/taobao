package com.huotu.tools.taobao.http;

import com.huotu.tools.taobao.entity.Category;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.util.List;

/**
 * @author CJ
 */
public class ListChildrenHandler implements ResponseHandler<List<Category>> {
    @Override
    public List<Category> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        if (response.getStatusLine().getStatusCode()!=200)
            throw new IOException("Bad Response:"+response.getStatusLine());
        return ResponseUtils.responseToPropList(response);
    }
}
