package Util;

public class Utility {

    public static final void print_error(String message) {
        System.out.println("\u001B[01;31m[-] " + message + "\u001B[0m");
    }

    public static final void print_good(String message) {
        System.out.println("\u001B[01;32m[+] " + message + "\u001B[0m");
    }

    public static final void print_info(String message) {
        System.out.println("\u001B[01;34m[*] " + message + "\u001B[0m");
    }

    public static final void print_warn(String message) {
        System.out.println("\u001B[01;33m[!] " + message + "\u001B[0m");
    }

    public static final void print_SQL_Query(String query) {
        System.out.println("\u001B[0;95m[SQL] " + query + "\u001B[0m");

    }

}