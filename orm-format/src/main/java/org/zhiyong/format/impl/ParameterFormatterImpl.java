package org.zhiyong.format.impl;

import org.zhiyong.format.interfaces.ParameterFormatter;
import org.zhiyong.orm.util.StringUtil;

import java.util.*;
import java.util.function.Consumer;


public class ParameterFormatterImpl implements ParameterFormatter {
    private final String origin;
    private List<String> blocks;
    private Map<String, KeyDescription> keys;
    private Map<String, KeyDescription> options;
    private final String REG = "[\\||[|]|;|\\(|\\)|,|=|<|>|+|\\*]";
    private String separator = " ";

    public ParameterFormatterImpl(String origin) {
        this.origin = origin;
        blocks = new ArrayList<>();
        keys = new HashMap<>();
        options = new HashMap<>();
        init();
    }

    private void init(){
        int i = 0, stat = 0, l, p = 0;
        char chr;
        StringBuilder block = new StringBuilder();
        while (i < origin.length()){
            chr = origin.charAt(i);
            block.append(chr);
            l = block.length();
            p = blocks.size();
            if (stat == 1 || stat == 3){
                if (block.length() == 2 && (chr == ':' || chr == ' '))
                    throw new Error("语法有误 ::");
                if (chr == ':'){
                    stat = 3;
                    block.delete(l - 1, l);
                    i--;
                    continue;
                }  else if (String.valueOf(chr).matches(REG) || chr == ' '  || stat == 3){
                    blocks.add(block.substring(0, l - 1));
                    keys.put(blocks.get(p).substring(1),
                            KeyDescription.asKey(blocks.get(p), p));
                    block.delete(0, l - 1);
                    stat = 0;
                }
            } else if (stat == 2 || stat == 4) {
                if (chr == '{'){
                    block.delete(0, l);
                    stat = 4;
                }else if (chr == ',' || chr == '}'){
                    if (block.length() <= 0)
                        throw new Error("找不到可选参数关键字");
                    blocks.add(separator);
                    p++;
                    block.insert(0, "#");
                    blocks.add(block.substring(0, l)); // 已经在第0个位置插入, 无需 -1操作,
                    options.put(StringUtil.hump(blocks.get(p).split(" ")).substring(1),
                            KeyDescription.asOptionKey(blocks.get(p).substring(1), p));
                    block.delete(0, l + 1);
                    if (chr == '}'){
                        stat = 0;
                    }
                }else if (chr == ':' || String.valueOf(chr).matches(REG)){
                    throw new Error("可选参数中不能存在 ':'");
                }else if (chr == ' ' && block.length() == 1){
                    blocks.add(block.toString());
                    block.delete(0, l);
                }
            } else {
                if (chr == ':') {
                    blocks.add(block.substring(0, l - 1));
                    block.delete(0, l - 1);
                    stat = 1;
                } else if (chr == '$') {
                    blocks.add(block.substring(0, l - 1));
                    block.delete(0, l);
                    stat = 2;
                }
            }
            if (++i == origin.length()){
                if (stat == 2){
                    throw new Error("语法有误,缺少 }");
                }else {
                    blocks.add(block.toString());
                }
            }

        }

    }

    @Override
    public ParameterFormatter set(String key, Object value) {
        KeyDescription description =  keys.get(key);
        if (description == null)
            throw new Error("关键字 " + key + " 不存在!");
        description.value = value;
        return this;
    }

    @Override
    public ParameterFormatter set(int index, Object value) {
        if (index >= keys().length)
            throw new Error("索引超出界限");
        set(keys()[index], value);
        return this;
    }

    @Override
    public ParameterFormatter setOption(String key, boolean use) {
        KeyDescription description = options.get(key);
        if (description == null)
            throw new Error("关键字 " + key + " 不存在!");
        description.use = use;
        return this;
    }

    @Override
    public ParameterFormatter setOption(String key, String value) {
        KeyDescription description = options.get(key);
        if (description == null)
            throw new Error("关键字 " + key + " 不存在!");
        description.use = true;
        description.value = value;
        return this;
    }

    @Override
    public ParameterFormatter setOptionSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    @Override
    public Object get(String key) {
        return keys.get(key).value;
    }

    @Override
    public Object get(int index) {
        if (index >= keys().length)
            throw new Error("索引超出界限");
        return get(keys()[index]);
    }

    @Override
    public int count() {
        return keys.size() + options.size() ;
    }

    @Override
    public ParameterFormatter clear() {
        for (String key : keys()) {
            keys.get(key).value = null;
        }
        for (String option : options()) {
            options.get(option).use = false;
        }
        return this;
    }

    @Override
    public String getOrigin() {
        return origin;
    }

    @Override
    public String transform() {
       ArrayList<KeyDescription> descriptions = new ArrayList<>();
       descriptions.addAll(options.values());
       descriptions.addAll(keys.values());
        for (KeyDescription description : descriptions) {
            if (description.use){
                if (description.value != null){
                    blocks.set(description.index, description.value.toString());
                }else {
                    blocks.set(description.index, null);
                }
            }else {
                blocks.set(description.index, "");
            }
        }
        StringBuilder buff = new StringBuilder();
        blocks.forEach(buff::append);
        return buff.toString().replaceAll("[ ]{2,}", " ");
    }

    @Override
    public String[] keys() {
        return keys.keySet().toArray(String[]::new);
    }

    @Override
    public String[] options() {
        return options.keySet().toArray(String[]::new);
    }

    @Override
    public String toString() {
        return "ParameterFormatterImpl{origin=" + origin  + '}';
    }

    public static final class KeyDescription{
        public String origin;
        public Object value;
        public boolean use;
        public int index;

        public KeyDescription(String origin, boolean use, int index) {
            this.origin = origin;
            this.use = use;
            this.index = index;
        }

        public KeyDescription(String origin, Object value, boolean use, int index) {
            this.origin = origin;
            this.value = value;
            this.use = use;
            this.index = index;
        }

        public static KeyDescription asOptionKey(String origin, int index){
            return new KeyDescription(origin, origin, false, index);
        }

        public static KeyDescription asKey(String origin, int index){
            return new KeyDescription(origin, true, index);
        }
    }

}
