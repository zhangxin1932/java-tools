package com.zy.commons.did.buffer;

import java.util.List;

public interface BufferedDidProvider {
    List<Long> provide(long did);
}
