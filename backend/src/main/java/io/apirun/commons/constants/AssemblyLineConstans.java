package io.apirun.commons.constants;

public enum AssemblyLineConstans {
    SCENARIO("scenario"),CASE("case"),API("api");
    private String value;
    AssemblyLineConstans(String value) {
        this.value=value;
    }
    @Override
    public String toString() {
        return this.value;
    }
}
