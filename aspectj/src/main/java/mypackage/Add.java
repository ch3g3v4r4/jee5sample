package mypackage;

import mypackage.mul.Mul;

public class Add {
    public int add(int a, int b) {
        return a + b;
    }

    public int sub(int i, int j) {
        new Mul().mul(i, j);
        return i - j;

    }
}
