package mypackage;

import mypackage.mul.Mul;


public class Main {

    public static void main(String[] args) {
        System.out.println(new Add().add(1, 2));
        System.out.println(new Add().add(2, 2));
        System.out.println(new Add().sub(2, 2));
        System.out.println(new Mul().mul(2, 2));
    }

}
