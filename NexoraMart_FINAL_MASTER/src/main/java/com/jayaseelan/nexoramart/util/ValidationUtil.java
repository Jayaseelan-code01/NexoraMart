package com.jayaseelan.nexoramart.util;
import java.util.regex.Pattern;
public final class ValidationUtil { private static final Pattern EMAIL=Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"); private ValidationUtil(){} public static boolean email(String s){return s!=null&&EMAIL.matcher(s).matches();} public static boolean name(String s){return s!=null&&s.trim().length()>=2;} public static boolean password(String s){return s!=null&&s.length()>=6;} }
