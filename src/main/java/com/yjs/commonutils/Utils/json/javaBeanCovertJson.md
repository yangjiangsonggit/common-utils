##java bean 转换为JSON 格式化Date为时间字符串

    List<Pojo> list;
    String data = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteDateUseDateFormat);
    JSONArray jsonArray = JSON.parseArray(data);
