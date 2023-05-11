package io.apirun.log.utils.dff;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.EnumSet;

class CopyingApplyProcessor extends InPlaceApplyProcessor {

    CopyingApplyProcessor(JsonNode target) {
        this(target, CompatibilityFlags.defaults());
    }

    CopyingApplyProcessor(JsonNode target, EnumSet<CompatibilityFlags> flags) {
        super(target.deepCopy(), flags);
    }
}
