package com.zy.commons.lang.validator;

import java.util.Objects;
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

    private ParamValidators() {
        throw new UnsupportedOperationException("ParamValidators cannot be Instantiated.");
    }

    public static <Fallback> Callback<Fallback> ifInvalid(boolean invalid) {
        return ifInvalid(invalid, null);
    }

    public static <Fallback> Callback<Fallback> ifInvalid(boolean invalid, Fallback fallbackData) {
        return new Callback<Fallback> () {
            @Override
            public Fallback get() {
                if (invalid) {
                    return fallbackData;
                }
                return null;
            }

            @Override
            public void postProcess(ValidatorHandler validatorHandler) {
                if (invalid) {
                    Objects.requireNonNull(validatorHandler, "ValidatorHandler cannot be null.");
                    validatorHandler.handle();
                }
            }

            @Override
            public void exception(RuntimeException e) {
                if (invalid) {
                    throw e;
                }
            }
        };
    }

    public interface Callback<Fallback> extends Supplier<Fallback> {

        /**
         * 校验失败时, 抛出异常
         * @param e
         */
        void exception(RuntimeException e);

        /**
         * 校验失败时, 返回指定值, 用于降级
         * @return
         */
        @Override
        Fallback get();

        /**
         * 校验失败时, 指定处理逻辑
         */
        void postProcess(ValidatorHandler validatorHandler);
    }

    public interface ValidatorHandler {
        void handle();
    }

}
