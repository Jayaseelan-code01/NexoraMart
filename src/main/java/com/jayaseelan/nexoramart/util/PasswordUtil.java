package com.jayaseelan.nexoramart.util;
import org.mindrot.jbcrypt.BCrypt;
public final class PasswordUtil { private PasswordUtil(){} public static String hash(String p){return BCrypt.hashpw(p,BCrypt.gensalt(12));} public static boolean matches(String p,String h){return h!=null&&BCrypt.checkpw(p,h);} }
