package bplustree;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Arrays;
import java.util.stream.Stream;

public class BPlusTreeOrderArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        BPlusTreeOrderTest annotation = context.getRequiredTestMethod()
                .getAnnotation(BPlusTreeOrderTest.class);

        return Arrays.stream(annotation.orders())
                .mapToObj(Arguments::of);
    }
}
