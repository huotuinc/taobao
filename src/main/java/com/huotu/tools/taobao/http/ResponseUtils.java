package com.huotu.tools.taobao.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.huotu.tools.taobao.entity.Category;
import com.huotu.tools.taobao.entity.DatabaseStorage;
import com.huotu.tools.taobao.entity.Property;
import com.huotu.tools.taobao.entity.PropertyValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author CJ
 */
public class ResponseUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Log log = LogFactory.getLog(ResponseUtils.class);

    public static List<Category> responseToPropList(String code) throws IOException {
        return jsonNodeToPropList(objectMapper.readTree(code));
    }

    public static List<Category> jsonNodeToPropList(JsonNode tree) {
        // 还需要确认所有的数据都已经得到了处理
        ArrayList<Category> list = new ArrayList<>();
        tree.fieldNames().forEachRemaining(treeName -> {
            if (!treeName.equalsIgnoreCase("itemcats_get_response")) {
                log.warn("catch unexpected field name:" + treeName + " in tree.");
            } else {
                JsonNode response = tree.get(treeName);

                response.fieldNames().forEachRemaining(responseName -> {
                    if (responseName.equalsIgnoreCase("request_id")) {
                        log.debug("Process request_id " + response.get(responseName).asText());
                    } else if (responseName.equalsIgnoreCase("item_cats")) {
                        JsonNode cats = response.get(responseName);
                        cats.fieldNames().forEachRemaining(catsName -> {
                            if (catsName.equalsIgnoreCase("item_cat")) {
                                JsonNode nodes = cats.get(catsName);

                                if (nodes.isArray()) {
                                    ArrayNode arrayNode = (ArrayNode) nodes;
                                    arrayNode.elements().forEachRemaining(node -> {
                                        list.add(ResponseUtils.jsonNodeToProp(node));
                                    });
                                } else
                                    throw new IllegalStateException("can not find JsonArray in response.");

                            } else
                                log.warn("catch unexpected field name:" + treeName + " in cats.");
                        });
                    } else {
                        log.warn("catch unexpected field name:" + treeName + " in response.");
                    }
                });
            }
        });

        return list;
    }

    public static Category jsonNodeToProp(JsonNode node) {

        try {
            return objectMapper.readValue(node.toString(), Category.class);
        } catch (IOException e) {
            return null;
        }
    }

    public static List<Category> responseToPropList(HttpResponse response) throws IOException {
        return jsonNodeToPropList(objectMapper.readTree(response.getEntity().getContent()));
    }

    private static ScriptEngineManager engineManager = new ScriptEngineManager();

    public static Set<DatabaseStorage> responseToPropDetail(HttpResponse response) throws IOException {
        ScriptEngine engine = engineManager.getEngineByExtension("js");

        try {
            engine.eval(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            @SuppressWarnings("unchecked") Map<?, Map> propertiesJs = (Map<?, Map>) engine.eval("props.itemprops_get_response.item_props.item_prop");
            @SuppressWarnings("unchecked") Map<?, Map> propertyValuesJs = (Map<?, Map>) engine.eval("propvalues.itempropvalues_get_response.prop_values.prop_value");

            HashSet<DatabaseStorage> set = new HashSet<>();
            propertiesJs.values().forEach(propertyJs -> {
                Property property = new Property();
                property.setCustomTemplate(readStringFromJS(propertyJs.get("child_template")));
                property.setEnumProperty(readBooleanFromJs(propertyJs.get("is_enum_prop")));
                property.setId(readLongFromJS(propertyJs.get("pid")));
                property.setInputProperty(readBooleanFromJs(propertyJs.get("is_input_prop")));
                property.setItemProperty(readBooleanFromJs(propertyJs.get("is_item_prop")));
                property.setKeyProperty(readBooleanFromJs(propertyJs.get("is_key_prop")));
                property.setMultiple(readBooleanFromJs(propertyJs.get("multi")));
                property.setName(readStringFromJS(propertyJs.get("name")));
                property.setRequired(readBooleanFromJs(propertyJs.get("must")));
                property.setSaleProperty(readBooleanFromJs(propertyJs.get("is_sale_prop")));
                //noinspection unchecked
                property.setSortOrder(readIntFromJS(propertyJs.getOrDefault("sort_order", 0)));
                property.setStatus(readStringFromJS(propertyJs.get("status")));
                property.setValidatedPropertyId(readLongFromJS(propertyJs.get("parent_pid")));
                if (0L == property.getValidatedPropertyId())
                    property.setValidatedPropertyId(null);
                property.setValidatedPropertyValueId(readLongFromJS(propertyJs.get("parent_vid")));
                if (0L == property.getValidatedPropertyValueId())
                    property.setValidatedPropertyValueId(null);

                set.add(property);
            });

            propertyValuesJs.values().forEach(propertyValueJs -> {
                PropertyValue propertyValue = new PropertyValue();
                propertyValue.setStatus(readStringFromJS(propertyValueJs.get("status")));
                //noinspection unchecked
                propertyValue.setSortOrder(readIntFromJS(propertyValueJs.getOrDefault("sort_order", 0)));
                propertyValue.setCategoryId(readLongFromJS(propertyValueJs.get("cid")));
                if (0L == propertyValue.getCategoryId())
                    propertyValue.setCategoryId(null);
                propertyValue.setId(readLongFromJS(propertyValueJs.get("vid")));
                propertyValue.setPropertyId(readLongFromJS(propertyValueJs.get("pid")));
                if (0L == propertyValue.getPropertyId())
                    propertyValue.setPropertyId(null);
                propertyValue.setName(readStringFromJS(propertyValueJs.get("name")));
                propertyValue.setNameAlias(readStringFromJS(propertyValueJs.get("name_alias")));

                set.add(propertyValue);
            });


            return set;
        } catch (ScriptException e) {
            //表示是空
            return null;
        }


        // props itemprops_get_response item_props item_prop
        // propvalues itempropvalues_get_response prop_values prop_value
//        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
//        String str = reader.readLine();
//        while (str!=null){
//            System.out.println(str);
//            str = reader.readLine();
//        }
//        reader.close();
//        JsonNode node = objectMapper.readTree(response.getEntity().getContent());
//        System.out.println(node);
    }

    private static int readIntFromJS(Object value) {
        if (value == null)
            return 0;
        if (value instanceof Number)
            return ((Number) value).intValue();

        return Integer.parseInt(value.toString());
    }

    private static String readStringFromJS(Object value) {
        if (value == null)
            return null;
        return value.toString();
    }

    private static Long readLongFromJS(Object value) {
        if (value == null)
            return null;
        if (value instanceof Number)
            return ((Number) value).longValue();

        return Long.parseLong(value.toString());
    }

    private static boolean readBooleanFromJs(Object value) {
        if (value == null)
            return false;
        if (value instanceof Boolean)
            return (boolean) value;
        return Boolean.parseBoolean(value.toString());
    }
}
