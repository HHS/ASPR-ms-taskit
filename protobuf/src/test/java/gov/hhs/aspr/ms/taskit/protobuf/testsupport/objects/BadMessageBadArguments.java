package gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects;

public class BadMessageBadArguments {

    private BadMessageBadArguments() {

    }

    private static class Builder {

    }

    public static Builder newBuilder(int badArgument) {
        return new Builder();
    }
}
