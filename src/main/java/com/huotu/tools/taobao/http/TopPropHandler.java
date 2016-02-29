package com.huotu.tools.taobao.http;

import com.huotu.tools.taobao.entity.Category;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author CJ
 */
public class TopPropHandler implements ResponseHandler<List<Category>> {

    private final boolean sandbox;

    public TopPropHandler(boolean sandbox) {
        this.sandbox = sandbox;
    }

    @Override
    public List<Category> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        if (response.getStatusLine().getStatusCode()!=200)
            throw new IOException("Bad Response:"+response.getStatusLine());

        Charset charset = ContentType.parse(response.getEntity().getContentType().getValue()).getCharset();
        Document document = Jsoup.parse(response.getEntity().getContent(), charset.name(), "https://open.taobao.com/apitools/");

        Elements scripts = document.select("script");

        String varName;
        if (sandbox)
            varName = "cid1_sandbox";
        else
            varName = "cid1_api";

        for (Element script : scripts) {
            if (script.data() != null && script.data().length() > 0) {
                String data = script.data();
                if (data.contains(varName)) {
                    String[] strings = data.split("\r\n");

                    for (String s : strings) {
                        if (s.contains(varName)) {
                            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
                            ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("js");
                            try {
                                scriptEngine.eval(s);
                                Object o = scriptEngine.get(varName);
                                String[] codes = o.toString().split("\\|");
                                return ResponseUtils.responseToPropList(codes[1]);
                            } catch (ScriptException e) {
                                throw new IllegalStateException("https://open.taobao.com/apitools/ has updated, hark it again.", e);
                            }
                        }
                    }
                    break;
                }
            }
        }
        throw new IllegalStateException("https://open.taobao.com/apitools/ has updated, hark it again.");
    }
}
