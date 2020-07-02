package org.zhiyong.format.base;

import org.zhiyong.format.impl.PreparedParameter;
import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.orm.util.ArrayUtil;
import org.zhiyong.orm.util.StringUtil;
;
import java.util.*;

public abstract class BaseParameter<T> implements Parameter<T> {
    protected  String origin;
    protected  List<String> blocks;
    protected  Map<String, PreparedParameter.KeyDescription> keys;
    protected  Map<String, PreparedParameter.KeyDescription> options;
    protected  Set<KeyDescription> byValues;
    protected  List<KeyDescription> listKeys;
    protected final String REG = "[\\||[|]|;|\\(|\\)|,|=|<|>|+|\\*]";
    protected String separator = " ";
    protected boolean available = true;
    protected int position = 0;
    protected int customerPosition = 0;

    public BaseParameter(String origin) {
        this.origin = origin;
        init();
    }

    protected void init(){
        blocks = new ArrayList<>();
        keys = new HashMap<>();
        options = new HashMap<>();
        byValues = new HashSet<>();
        listKeys = new ArrayList<>();

        int i = 0, stat = 0, l, p = 0;
        char chr;
        StringBuilder block = new StringBuilder();
        while (i < origin.length()){
            chr = origin.charAt(i);
            block.append(chr);
            l = block.length();
            p = blocks.size();
            if (i == origin.length() - 1 && stat == 1 && !String.valueOf(chr).matches(REG)){
                stat = 3; l++; // 维护最后一个字符
            }
            if (stat == 1 || stat == 3){
                if (block.length() == 2 && (chr == ':' || chr == ' '))
                    throw new Error("语法有误 ::");
                if (chr == ':'){
                    stat = 3;
                    block.delete(l - 1, l);
                    i--;
                    continue;
                }  else if (String.valueOf(chr).matches(REG) || chr == ' '  || stat == 3){
                    String key = block.substring(1, l - 1);
                    blocks.add(key);
                    KeyDescription keyDescription = KeyDescription.asKey(key, p);
                    listKeys.add(keyDescription);
                    keys.put(key, keyDescription);
                    if (stat == 3)l++;
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
    public BaseParameter<T> set(String key, Object value) {
        KeyDescription description =  keys.get(key);
        if (description == null)
            throw new Error("关键字 " + key + " 不存在!");
        description.value = value;
        return this;
    }

    @Override
    public BaseParameter<T> set(int index, Object value) {
        if (index >= keys().length)
            throw new Error("索引超出界限");
        set(keys()[index], value);
        return this;
    }

    @Override
    public BaseParameter<T> set(Object value) {
        return set(position++, value);
    }

    private KeyDescription getKeyDescription(String key){
        KeyDescription description =  keys.get(key);
        if (description == null)
            throw new Error("关键字 " + key + " 不存在!");
        return description;
    }


    @Override
    public BaseParameter<T> setByValue(String key, Object value) {
        KeyDescription description = getKeyDescription(key);
        description.value = value;
        byValues.add(description);
        return this;
    }

    @Override
    public BaseParameter<T> setByValue(int index, Object value) {
        if (index >= keys().length)
            throw new Error("索引超出界限");
        return setByValue(keys()[index], value);
    }

    @Override
    public BaseParameter<T> setByValue(Object value) {
        return setByValue(position++, value);
    }


    @Override
    public BaseParameter<T> setOption(String key, boolean use) {
        KeyDescription description = options.get(key);
        if (description == null)
            throw new Error("关键字 " + key + " 不存在!");
        description.use = use;
        return this;
    }

    @Override
    public BaseParameter<T> setOption(String key, String value) {
        KeyDescription description = options.get(key);
        if (description == null)
            throw new Error("关键字 " + key + " 不存在!");
        description.use = true;
        description.value = value;
        return this;
    }

    @Override
    public BaseParameter<T> setOptionSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    @Override
    public BaseParameter<T> setPosition(int position) {
        this.position = position;
        this.customerPosition = position;
        return this;
    }

    @Override
    public int getPosition() {
        return customerPosition;
    }

    @Override
    public int getCurPosition() {
        return position;
    }

    @Override
    public int resetPosition() {
        setPosition(customerPosition);
        return position;
    }

    @Override
    public Object get(String key) {
        KeyDescription description = keys.get(key);
        Objects.requireNonNull(description, "找不到关键字: " + key);
        return description.value;
    }

    @Override
    public Object get(int index) {
        if (index >= keys().length)
            throw new Error("索引超出界限");
        return get(keys()[index]);
    }

    @Override
    public int count() {
        return keys.size();
    }


    @Override
    public String getOrigin() {
        return origin;
    }


    @Override
    public String[] keys() {
        return listKeys.stream()
                .map(k->k.origin)
                .toArray(String[]::new);
    }

    @Override
    public String[] options() {
        return options.keySet().toArray(String[]::new);
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public BaseParameter<T> setAvailable(boolean a) {
        this.available = a;
        return this;
    }

    @Override
    public BaseParameter<T> clear() {
        for (KeyDescription value : keys.values()) {
            blocks.set(value.index, "?");
        }
        for (KeyDescription value : options.values()) {
            blocks.set(value.index, "");
        }
        return this;
    }

    @Override
    public String toString() {
        return "ParameterFormatterImpl{\norigin="
                + origin
                + "\nblocks="
                + ArrayUtil.toString(blocks, "")
                + "\nkeys="
                + ArrayUtil.toString(keys())
                + "\noptions="
                + ArrayUtil.toString(options())
                + "\ntransform="
                + transform()
                + "\n}";
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

    @Override
    public Parameter<T> replace() {
        ArrayList<KeyDescription> descriptions = new ArrayList<>();
        descriptions.addAll(keys.values());
        descriptions.addAll(options.values());
        for (KeyDescription description : descriptions) {
            if (description.use){
                blocks.set(description.index, description.value.toString());
            }else {
                blocks.set(description.index, "${" + description.value.toString() + "}");
            }
        }
        this.origin = ArrayUtil.toString(blocks, "");
        init();
        resetPosition();
        return this;
    }
}
