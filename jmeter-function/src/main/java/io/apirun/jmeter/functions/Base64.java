package io.apirun.jmeter.functions;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterVariables;
import sun.misc.BASE64Encoder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Base64 extends AbstractFunction {
    private static final List<String> desc = new LinkedList();
    private static final String KEY = "__Base64";
    private Object[] values;

    public synchronized String execute(SampleResult paramSampleResult, Sampler paramSampler)
            throws InvalidVariableException
    {
        JMeterVariables localJMeterVariables = getVariables();
        String str1 = ((CompoundVariable)this.values[0]).execute();

        String str2 = new BASE64Encoder().encode(str1.getBytes());

        if ((localJMeterVariables != null) && (this.values.length > 1)) {
            String str3 = ((CompoundVariable)this.values[1]).execute().trim();
            localJMeterVariables.put(str3, str2);
        }

        return str2;
    }

    public synchronized void setParameters(Collection<CompoundVariable> paramCollection)
            throws InvalidVariableException
    {
        checkMinParameterCount(paramCollection, 1);
        this.values = paramCollection.toArray();
    }

    public String getReferenceKey()
    {
        return KEY;
    }

    public List<String> getArgumentDesc()
    {
        return desc;
    }

    static
    {
        desc.add("String to calculate Base64 hash");
        desc.add("Name of variable in which to store the result (optional)");
    }
}
