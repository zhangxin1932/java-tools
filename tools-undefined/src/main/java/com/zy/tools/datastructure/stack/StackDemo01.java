package com.zy.tools.datastructure.stack;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class StackDemo01 {

    private static Map<Character, Integer> operatorMap = new HashMap<>();

    static {
        operatorMap.put('*', 1);
        operatorMap.put('/', 1);
        operatorMap.put('+', 0);
        operatorMap.put('-', 0);
    }

    @Test
    public void fn01() {
        Stack<Integer> stack = new Stack<>(10);
        stack.push(5);
        stack.push(6);
        stack.push(2);
        System.out.println(stack.peek());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println("-----------");
        System.out.println("62 + 5 * 13 - 2".substring(0, 1).charAt(0));
    }

    @Test
    public void fn02() {
        // 定义一个表达式
        String expression = "65 + 5 / 3 - 2";
        System.out.println(String.format("%s = %s", expression, calInfixExpression(expression)));
    }

    @Test
    public void fn03() {
        String expression = "3 47 + 5 * 6 -";
        List<String> list = Arrays.stream(expression.split(" ")).collect(Collectors.toList());
        System.out.println(String.format("%s = %s", expression, calSuffixExpression(list)));
    }

    @Test
    public void fn04() {
        String expression = "1+((10+3)/3)-5";
        System.out.println(calSuffixExpression(convertIn2Suffix(expression)));
    }

    /**
     * 将前缀表达式转为后缀表达式的 list
     * @param expression
     * @return
     */
    public static List<String> convertIn2Suffix(String expression) {
        // 将中缀表达式转为 list
        List<String> list = Lists.newArrayList();
        int length = expression.length();
        StringBuilder multi = new StringBuilder();
        char ch;
        for (int i = 0; i < length; i++) {
            ch = expression.charAt(i);
            if (ch < 48 || ch > 57) {
                if (multi.length() > 0) {
                    list.add(multi.toString());
                    multi = new StringBuilder();
                }
                list.add(String.valueOf(ch));
            } else {
                multi.append(ch);
                if (i == length - 1) {
                    list.add(multi.toString());
                }
            }
        }

        // 将 转换后的 List 入栈 s1, s2(由于 s2 始终未有 pop 操作, 这里选用了 List 替代), 最终转换成一个后缀表达式
        Stack<String> s1 = new Stack<>();
        List<String> s2 = Lists.newArrayListWithCapacity(list.size());
        list.forEach(e -> {
            if (isNumberic(e)) {
                s2.add(e);
            } else if (Objects.equals(e, "(")) {
                s1.push(e);
            } else if (Objects.equals(e, ")")) {
                // 如果是右括号, 则依次弹出 s1 栈顶的运算符, 并压入 s2, 直到遇到左括号为止, 此时将这一对括号丢弃
                while (!Objects.equals(s1.peek(), "(")) {
                    s2.add(s1.pop());
                }
                // 将左括号从 s1 栈弹出
                s1.pop();
            } else if (isOperator(e)) {
                // 当当前操作符的优先级 <= 栈顶的优先级时, 将s1栈顶的运算符弹出并压入s2中, 再次转到新的栈顶运算符比较
                while (s1.size() > 0 && !Objects.equals(s1.peek(), "(") && getPriority(e.charAt(0)) <= getPriority(s1.peek().charAt(0))) {
                    s2.add(s1.pop());
                }
                // 将当前的操作符压入s1栈中
                s1.push(e);
            }
        });
        // 将 s1 中剩余的运算符压入 s2 中
        while (s1.size() > 0) {
            s2.add(s1.pop());
        }
        return s2;
    }

    /**
     * 后缀表达式运算
     *
     * @param list
     * @return
     */
    public static double calSuffixExpression(List<String> list) {
        Stack<Double> numberStack = new Stack<>();
        list.forEach(e -> {
            if (isNumberic(e)) {
                numberStack.push(Double.valueOf(e));
            } else if (isOperator(e)) {
                Double d1 = numberStack.pop();
                Double d2 = numberStack.pop();
                numberStack.push(cal(d1, d2, e.charAt(0)));
            } else {
                throw new RuntimeException("illegal str" + e);
            }
        });
        return numberStack.pop();
    }

    /**
     * 中缀表达式
     *
     * @param expression
     * @return
     */
    public static double calInfixExpression(String expression) {
        // 创建两个栈, 一个是数字栈, 一个是操作符栈
        Stack<Double> numberStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();
        // 定义需要的相关变量
        int index = 0; // 用于扫描
        double d1;
        double d2;
        double result = 0;
        char oper;
        char ch;
        StringBuilder multiNum = new StringBuilder(); // 用于拼接多位数
        while (index < expression.length()) {
            // 依次取 表达式 中的没一个字符
            ch = expression.substring(index, index + 1).charAt(0);
            // 如果是操作符
            if (isOperator(ch)) {
                // 如果当前符号栈为空, 则直接入栈即可
                if (operatorStack.size() == 0) {
                    operatorStack.push(ch);
                } else {
                    // 如果当前符号栈不为空
                    // 如果当前操作符的优先级小于等于栈中的操作符, 就从数字栈中 pop 出两个数, 再从符号栈中 pop 出一个运算符进行运算, 将结果入数字栈, 然后将当前的操作符入符号栈
                    if (getPriority(ch) <= getPriority(operatorStack.peek())) {
                        d1 = numberStack.pop();
                        d2 = numberStack.pop();
                        oper = operatorStack.pop();
                        result = cal(d1, d2, oper);
                        numberStack.push(result);
                        operatorStack.push(ch);
                    } else {
                        // 如果当前操作符的优先级大于栈中的操作符, 就直接入符号栈
                        operatorStack.push(ch);
                    }
                }
            } else if (isNumberic(String.valueOf(ch))) {
                // 如果是数字, 要考虑多位数的场景
                multiNum.append(ch);

                // 如果是最后一位, 就不再进行下述判断了
                if (index == expression.length() - 1) {
                    // 如果后续没有数字
                    numberStack.push(Double.valueOf(multiNum.toString()));
                    // 这里一定要清空
                    multiNum = new StringBuilder();
                } else {
                    if (isNumberic(String.valueOf(expression.substring(index + 1, index + 2).charAt(0)))) {
                        // skip
                    } else {
                        // 如果后续没有数字
                        numberStack.push(Double.valueOf(multiNum.toString()));
                        // 这里一定要清空
                        multiNum = new StringBuilder();
                    }
                }
            } else {
                // skip
            }
            index++;
        }

        while (operatorStack.size() > 0) {
            d1 = numberStack.pop();
            d2 = numberStack.pop();
            oper = operatorStack.pop();
            result = cal(d1, d2, oper);
            numberStack.push(result);
        }

        return numberStack.pop();
    }

    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumberic(String str) {
        return StringUtils.isNumeric(str);
    }

    /**
     * 是否是操作符
     *
     * @param str
     * @return
     */
    public static boolean isOperator(String str) {
        if (str == null || str.length() > 1) {
            return false;
        }
        char ch = str.charAt(0);
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    /**
     * 是否是操作符
     *
     * @param ch
     * @return
     */
    public static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    public static double cal(double d1, double d2, char operator) {
        double result;
        switch (operator) {
            case '+':
                result = d1 + d2;
                break;
            case '-':
                result = d2 - d1;
                break;
            case '*':
                result = d1 * d2;
                break;
            case '/':
                result = d2 / d1;
                break;
            default:
                throw new RuntimeException("operation is illegal.");
        }
        return result;
    }

    /**
     * 判断运算符的优先级
     *
     * @param operator
     * @return
     */
    public static int getPriority(char operator) {
        Integer priority = operatorMap.get(operator);
        if (Objects.isNull(priority)) {
            throw new RuntimeException("operation is illegal " + operator);
        }
        return priority;
    }

    private static class Stack<E> {
        transient int size;
        transient Object[] arr;
        private static final int DEFAULT_SIZE = 16;

        public Stack() {
            this(DEFAULT_SIZE);
        }

        public Stack(int size) {
            Assert.isTrue(size >= 0, "size must be positive");
            arr = new Object[size];
        }

        /**
         * 入栈
         *
         * @param e
         * @return
         */
        public E push(E e) {
            Object[] newArr = new Object[size + 1];
            System.arraycopy(arr, 0, newArr, 0, size);
            arr = newArr;
            arr[size] = e;
            size++;
            return e;
        }

        /**
         * 弹栈, 弹出栈顶元素
         *
         * @return
         */
        @SuppressWarnings("unchecked")
        public E pop() {
            Assert.isTrue(size > 0, "no element in the stack.");
            E item = (E) arr[size - 1];
            Object[] newArr = new Object[size - 1];
            System.arraycopy(arr, 0, newArr, 0, size - 1);
            arr = newArr;
            size--;
            return item;
        }

        /**
         * 返回栈顶的元素
         *
         * @return
         */
        @SuppressWarnings("unchecked")
        public E peek() {
            return (E) arr[size - 1];
        }

        private void ensureCapacityHelper(int minCapacity) {
            if (minCapacity - arr.length > 0) {
                int capacity = arr.length;
                if (arr.length > Integer.MAX_VALUE >> 1 && arr.length + 1 < Integer.MAX_VALUE) {
                    capacity++;
                } else {
                    capacity = capacity << 1;
                }
                Object[] newArr = new Object[capacity];
                System.arraycopy(arr, 0, newArr, 0, arr.length);
                arr = newArr;
            }
        }

        /**
         * 栈的大小
         *
         * @return
         */
        public int size() {
            return size;
        }
    }
}
