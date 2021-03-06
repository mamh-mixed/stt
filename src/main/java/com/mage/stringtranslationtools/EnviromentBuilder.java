/*
 *  Copyright (C) 2019.  mamh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mage.stringtranslationtools;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnviromentBuilder {
    public static String FILEPATH_RES;
    public static FilenameFilter mStringXMLFileFilter;

    static {
        FILEPATH_RES = File.separator + "res" + File.separator + "values";
        mStringXMLFileFilter = (dir, filename) -> filename.endsWith(".xml");
    }

    public EnviromentBuilder() {
    }

    public static boolean isValidArgsTwo(String[] args) {
        if (args.length < 2) {//需要2命令行个参数，一个是数字，一个是文件路径
            Utils.logerr("Please give 2 args on cmds, and one file path to check.");
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidArgsThree(String[] args) {
        if (args.length < 3) {
            Utils.logerr("Please give 3 args on cmds, and 2 file path to check.");
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidArgsFour(String[] args) {
        if (args.length < 4) {
            Utils.logerr("Please give 4 args on cmds, and 3 file path to check.");
            return true;
        } else {
            return false;
        }
    }

    public static Map<String, String> readStringValueFromDir(String dirPath, List<String> keys) {
        File[] files = (new File(dirPath)).listFiles(mStringXMLFileFilter);

        Map<String, String> xmlContentMap = new HashMap<>();

        Dom4jParser parser = new Dom4jParser();

        if (files != null) {
            for (File file : files) {
                Map<String, String> temp = parser.parseValidStringNames(file, keys);
                xmlContentMap.putAll(temp);
            }
        }

        return xmlContentMap;
    }

    public static Set<String> scanResDirPathList(String configFileName) {
        HashSet<String> set = new HashSet<>();

        try {
            FileInputStream f = new FileInputStream(new File(configFileName));
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(f));

            String file;
            try {
                while ((file = fileReader.readLine()) != null) {
                    file = file.replace("/", File.separator);

                    if (file.contains(FILEPATH_RES)) {
                        file = file.substring(1, file.indexOf(FILEPATH_RES));
                    } else if (file.contains(File.separator + "res1" + File.separator + "values")) {
                        file = file.substring(1, file.indexOf(File.separator + "res1" + File.separator + "values"));
                    }

                    Utils.logout("scanResDirPathList:" + file);

                    set.add(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return set;
    }

    public static Map<String, Boolean> scanFilterItems(String configFileName) {
        Map<String, Boolean> map = new HashMap<>();

        try {
            FileInputStream f = new FileInputStream(new File(configFileName));
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(f));

            String file;
            try {
                while ((file = fileReader.readLine()) != null) {
                    Utils.logout("scanFilterItems:" + file);
                    map.put(file, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static List<String> scanValuesList(String fileName) {
        List<String> set = new ArrayList<>();

        try {
            FileInputStream f = new FileInputStream(new File(fileName));
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(f));

            String file;
            try {
                while ((file = fileReader.readLine()) != null) {
                    set.add(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return set;
    }

    public static boolean isValidString(String key, String value) {
        if (!key.startsWith("A:") && !key.startsWith("P:")) {
            if (value.isEmpty()) {
                return false;
            } else if (value.startsWith("@string/")) {
                return false;
            } else if (value.startsWith("array/")) {
                return false;
            } else if (value.startsWith("drawable/")) {
                return false;
            } else if (value.startsWith("string/")) {
                return false;
            } else if (value.startsWith("@*android:string/")) {
                return false;
            } else if (value.startsWith("@drawable/")) {
                return false;
            } else if (value.trim().length() < 1) {
                return false;
            } else {
                try {
                    Float.parseFloat(value);
                    return false;
                } catch (Exception e) {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    public static boolean isValidKey(String key, Map<String, Boolean> filterMap) {
        return filterMap.get(key) == null || !filterMap.get(key);
    }

    public static boolean isNotTranslated(String key, List<String> valuesSet, Map<String, Map<String, String>> valuesResourceMap) {
        Iterator iterator = valuesSet.iterator();

        String temp;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            String str = (String) iterator.next();
            temp = null;
            if (valuesResourceMap.get(str) != null && ((Map) valuesResourceMap.get(str)).get(key) != null) {
                temp = (String) ((Map) valuesResourceMap.get(str)).get(key);
            }

            if (temp == null) {
                return true;
            }

            if (temp.startsWith("\"")) {
                temp = temp.substring(1);
            }

            if (temp.endsWith("\"")) {
                temp = temp.substring(0, temp.length() - 1);
            }
        } while (!temp.isEmpty());

        return true;
    }

    public static boolean isArrayNotTranslated(List<String> tempStringNames,
                                               Map<String, String> valuesResource,
                                               String resDir,
                                               Map<String, Boolean> filterMap,
                                               List<String> valuesSet,
                                               Map<String, Map<String, String>> valuesResourceMap) {
        for (String tempKey : tempStringNames) {
            String tempvalue =  valuesResource.get(tempKey);

            if (tempvalue.startsWith("\"")) {
                tempvalue = tempvalue.substring(1);
            }
            if (tempvalue.endsWith("\"")) {
                tempvalue = tempvalue.substring(0, tempvalue.length() - 1);
            }

            if ((isValidString("", tempvalue)) &&
                    (isValidKey(tempKey + "==" + resDir, filterMap)) &&
                    (isNotTranslated(tempKey, valuesSet, valuesResourceMap))
            ) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, String> getNotTranslatedMap(String key, List<String> valuesSet, Map<String, Map<String, String>> valuesResourceMap) {
        Map<String, String> map = new HashMap<>();
        for (String str : valuesSet) {
            String temp = null;
            if ((valuesResourceMap.get(str) != null) && (((Map) valuesResourceMap.get(str)).get(key) != null)) {
                temp = (String) ((Map) valuesResourceMap.get(str)).get(key);
            }
            if (temp == null) {
                map.put(str, null);
            } else {
                if (temp.startsWith("\"")) {
                    temp = temp.substring(1, temp.length());
                }
                if (temp.endsWith("\"")) {
                    temp = temp.substring(0, temp.length() - 1);
                }
                if (temp.isEmpty()) {
                    map.put(str, null);
                }
            }
        }
        return map;
    }


}

