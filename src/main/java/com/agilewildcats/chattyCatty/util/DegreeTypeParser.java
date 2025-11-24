package com.agilewildcats.chattyCatty.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreeTypeParser {
    private static final Pattern degreeTypesPattern = Pattern.compile("(?i)\b(BS|MS|PhD)\b");
    private static final Pattern degreeTypesNamePattern = Pattern.compile("(?i)\b(Bachelor'?s?|Master'?s?|Doctorate)\b");

    public static String parse(String input) {
        input = input.toLowerCase();
        Matcher matcher = degreeTypesPattern.matcher(input);
        String degreeType = matcher.hasMatch() ? matcher.group(0) : null;
        if (degreeType == null || degreeType.isEmpty()) {
            matcher = degreeTypesNamePattern.matcher(input);
            String temp = matcher.hasMatch() ? matcher.group(0) : null;
            if (temp != null && !temp.isEmpty()) {
                switch (temp.charAt(0)) {
                    case 'b':
                        degreeType = "bs";
                        break;
                    case 'm':
                        degreeType = "ms";
                        break;
                    case 'd':
                        degreeType = "phd";
                        break;
                }
            }
        }
        if (degreeType == null || degreeType.isEmpty()) {
            degreeType = "any";
        }
        return degreeType;
    }
}
