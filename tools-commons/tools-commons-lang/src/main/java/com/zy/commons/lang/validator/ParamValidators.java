package com.zy.commons.lang.validator;

import java.util.function.Supplier;

/**
 *
 * 使用示例:
 *
 * 1) Validators.ifInvalid(true).postProcess(() -> {
 *      log.error("param is not illegal");
 *      throw new IllegalArgumentException("param is not illegal");
 * });
 * 2) Validators.ifInvalid(true).exception(new IllegalArgumentException("lll"));
 * 3) String fallbackData = Validators.ifInvalid(true, "123").get();
 *
 */
public final class ParamValidators {

    private static final Callback<Object> EMPTY_CALLBACK = new Callback<Object> () {};

    private ParamValidators() {
        throw new UnsupportedOperationException("Validators cannot be Instantiated.");
    }

    public static <Fallback> Callback<Fallback> ifInvalid(boolean invalid) {
        return ifInvalid(invalid, null);
    }

    @SuppressWarnings("unchecked")
    public static <Fallback> Callback<Fallback> ifInvalid(boolean invalid, Fallback fallbackData) {
        if (!invalid) {
            return (Callback<Fallback>) EMPTY_CALLBACK;
        }
        return new Callback<Fallback> () {
            @Override
            public Fallback get() {
                return fallbackData;
            }

            @Override
            public void postProcess(ValidatorHandler validatorHandler) {
                validatorHandler.handle();
            }

            @Override
            public void exception(RuntimeException e) {
                throw e;
            }
        };
    }

    public interface Callback<Fallback> extends Supplier<Fallback> {

        /**
         * 校验失败时, 抛出异常
         * @param e
         */
        default void exception(RuntimeException e) {}

        /**
         * 校验失败时, 返回指定值, 用于降级
         * @return
         */
        @Override
        default Fallback get() {
            return null;
        };

        /**
         * 校验失败时, 指定处理逻辑
         */
        default void postProcess(ValidatorHandler validatorHandler) {};
    }

    public interface ValidatorHandler {
        void handle();
    }

}
