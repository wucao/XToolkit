package com.xxg.xtoolkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.Map;

public class XmlUtil {

    private static XmlMapper xmlMapper = new XmlMapper();

    /**
     * Map 转 XML
     * @param root XML 根节点名称，如 person
     * @param map 如 {name=jack,age=18}
     * @return <person><name>jack</name><age>18</age></person>
     */
    public static String mapToXml(String root, Map<String, String> map) throws JsonProcessingException {
        String xml = xmlMapper.writer().withRootName(root).writeValueAsString(map);
        return xml;
    }

    /**
     * XML 转 Map
     * @param xml 如 <person><name>jack</name><age>18</age></person>
     * @return map {name=jack,age=18}
     */
    public static Map<String, String> xmlToMap(String xml) throws IOException {
        Map<String, String> map = xmlMapper.readValue(xml, Map.class);
        return map;
    }
}
