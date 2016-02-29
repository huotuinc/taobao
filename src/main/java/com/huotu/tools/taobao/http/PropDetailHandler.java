package com.huotu.tools.taobao.http;

import com.huotu.tools.taobao.entity.DatabaseStorage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.util.Set;

/**
 * @author CJ
 */
public class PropDetailHandler implements ResponseHandler<Set<DatabaseStorage>> {
    @Override
    public Set<DatabaseStorage> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        if (response.getStatusLine().getStatusCode()!=200)
            throw new IOException("Bad Response:"+response.getStatusLine());
        return ResponseUtils.responseToPropDetail(response);
    }
}
