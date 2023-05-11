
package io.apirun.jmeter.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.codec.digest.HmacUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HmacSHAEncodeFunction extends AbstractFunction {

    private static final Logger log = LoggerFactory.getLogger(HmacSHAEncodeFunction.class);

    /**
     *  HmacAlgorithms类所实现的方法，基本都来自于HmacUtils;
     */
    private static final List<String> desc = new ArrayList<>();
    private static final String KEY = "__Hmac";

    // Number of parameters expected - used to reject invalid calls
    private static final int MIN_PARAMETER_COUNT = 2;
    private static final int MAX_PARAMETER_COUNT = 3;

    static {
        desc.add(JMeterUtils.getResString("algorithm_string"));
        desc.add(JMeterUtils.getResString("sha_string"));
        desc.add(JMeterUtils.getResString("salt_string"));
    }

    private CompoundVariable[] values;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException {
        String digestAlgorithm = values[0].execute();
        String stringToEncode = values[1].execute();
        String salt = values.length > 2 ? values[2].execute() : null;
        String encodedString = null;
        try {
            HmacUtils utils = new HmacUtils(digestAlgorithm, salt);
            encodedString = utils.hmacHex(stringToEncode);
        } catch (Exception e) {
            log.error("Error calling {} function with value {}, digest algorithm {}, salt {}, ", KEY, stringToEncode,
                    digestAlgorithm, salt, e);
        }
        return encodedString;
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, MIN_PARAMETER_COUNT, MAX_PARAMETER_COUNT);
        values = parameters.toArray(new CompoundVariable[parameters.size()]);
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        return desc;
    }
}
