package me.willkroboth.configcommands.functions.executions;

import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.functions.Parameter;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.internalarguments.InternalVoidArgument;

import java.util.List;

public class InstanceExecution<Target, Return> extends Execution<InstanceExecution.Run<Target, Return>, Return> {
    // General interface for an instance execution
    @FunctionalInterface
    public interface Run<Target, Return> {
        InternalArgument<Return> run(InternalArgument<Target> target, List<InternalArgument<?>> parameters) throws CommandRunException;
    }

    protected InstanceExecution(Run<Target, Return> run, Class<? extends InternalArgument<Return>> returnClass, String returnMessage, Parameter<?>... parameters) {
        super(run, returnClass, returnMessage, parameters);
    }

    // We want to declare in order parameters, returns, executes
    //  May skip previous steps for no parameters/void return
    // And yes, all of these silly classes and interfaces are necessary because I want cool type inference

    ////////////////////////////
    // SECTION: No Parameters //
    ////////////////////////////

    public static NoParameter withParameters() {
        return new NoParameter();
    }

    // Some methods that skip to the point for more concise code in special cases
    public static NoParameter.VoidReturn returns() {
        return new NoParameter().returns();
    }

    public static <Return> NoParameter.ObjectReturn<Return> returns(Class<? extends InternalArgument<Return>> returnClass) {
        return new NoParameter().returns(returnClass);
    }

    public static <Return> NoParameter.ObjectReturn<Return> returns(Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
        return new NoParameter().returns(returnClass, returnMessage);
    }

    public static <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(NoParameter.VoidReturn.Executes<Target> executes) {
        return new NoParameter().returns().executes(executes);
    }

    public static class NoParameter {
        protected NoParameter() {
        }

        public VoidReturn returns() {
            return new VoidReturn();
        }

        public <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(VoidReturn.Executes<Target> executes) {
            return new VoidReturn().executes(executes);
        }

        public static class VoidReturn {
            protected VoidReturn() {
            }

            @FunctionalInterface
            public interface Executes<Target> {
                void run(InternalArgument<Target> target) throws CommandRunException;
            }

            public <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(Executes<Target> executes) {
                return SignatureParameterized.of(new InstanceExecution<>((t, p) -> {
                    executes.run(t);
                    return InternalVoidArgument.getInstance();
                }, InternalVoidArgument.class, null));
            }
        }

        public <Return> ObjectReturn<Return> returns(Class<? extends InternalArgument<Return>> returnClass) {
            return new ObjectReturn<>(returnClass, null);
        }

        public <Return> ObjectReturn<Return> returns(Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
            return new ObjectReturn<>(returnClass, returnMessage);
        }

        public static class ObjectReturn<Return> {
            private final Class<? extends InternalArgument<Return>> returnClass;
            private final String returnMessage;

            public ObjectReturn(Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
                this.returnClass = returnClass;
                this.returnMessage = returnMessage;
            }

            @FunctionalInterface
            public interface Executes<Target, Return> {
                InternalArgument<Return> run(InternalArgument<Target> target) throws CommandRunException;
            }

            public <Target> SignatureParameterized<InstanceExecution<Target, Return>> executes(Executes<Target, Return> executes) {
                return SignatureParameterized.of(new InstanceExecution<>((t, p) -> executes.run(t), returnClass, returnMessage));
            }
        }
    }

    ////////////////////////////
    // SECTION: One parameter //
    ////////////////////////////

    public static <P1> OneParameter<P1> withParameters(Parameter<P1> parameter1) {
        return new OneParameter<>(parameter1);
    }

    public static class OneParameter<P1> {
        private final Parameter<P1> parameter1;

        protected OneParameter(Parameter<P1> parameter1) {
            this.parameter1 = parameter1;
        }

        public VoidReturn<P1> returns() {
            return new VoidReturn<>(parameter1);
        }

        public <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(VoidReturn.Executes<Target, P1> executes) {
            return new VoidReturn<>(parameter1).executes(executes);
        }

        public static class VoidReturn<P1> {
            Parameter<P1> parameter1;

            protected VoidReturn(Parameter<P1> parameter1) {
                this.parameter1 = parameter1;
            }

            @FunctionalInterface
            public interface Executes<Target, P1> {
                void run(InternalArgument<Target> target, InternalArgument<P1> parameter1) throws CommandRunException;
            }

            public <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(Executes<Target, P1> executes) {
                return SignatureParameterized.of(new InstanceExecution<>((t, p) -> {
                    executes.run(t, (InternalArgument<P1>) p.get(0));
                    return InternalVoidArgument.getInstance();
                }, InternalVoidArgument.class, null, parameter1));
            }
        }

        public <Return> ObjectReturn<P1, Return> returns(Class<? extends InternalArgument<Return>> returnClass) {
            return new ObjectReturn<>(parameter1, returnClass, null);
        }

        public <Return> ObjectReturn<P1, Return> returns(Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
            return new ObjectReturn<>(parameter1, returnClass, returnMessage);
        }

        public static class ObjectReturn<P1, Return> {
            private final Parameter<P1> parameter1;
            private final Class<? extends InternalArgument<Return>> returnClass;
            private final String returnMessage;

            protected ObjectReturn(Parameter<P1> parameter1, Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
                this.parameter1 = parameter1;
                this.returnClass = returnClass;
                this.returnMessage = returnMessage;
            }

            @FunctionalInterface
            public interface Executes<Target, P1, Return> {
                InternalArgument<Return> run(InternalArgument<Target> target, InternalArgument<P1> parameter1) throws CommandRunException;
            }

            public <Target> SignatureParameterized<InstanceExecution<Target, Return>> executes(Executes<Target, P1, Return> executes) {
                return SignatureParameterized.of(new InstanceExecution<>(
                        (t, p) -> executes.run(t, (InternalArgument<P1>) p.get(0)),
                        returnClass, returnMessage,
                        parameter1
                ));
            }
        }
    }

    /////////////////////////////
    // SECTION: Two Parameters //
    /////////////////////////////

    public static <P1, P2> TwoParameter<P1, P2> withParameters(Parameter<P1> parameter1, Parameter<P2> parameter2) {
        return new TwoParameter<>(parameter1, parameter2);
    }

    public static class TwoParameter<P1, P2> {
        private final Parameter<P1> parameter1;
        private final Parameter<P2> parameter2;

        public TwoParameter(Parameter<P1> parameter1, Parameter<P2> parameter2) {
            this.parameter1 = parameter1;
            this.parameter2 = parameter2;
        }

        public VoidReturn<P1, P2> returns() {
            return new VoidReturn<>(parameter1, parameter2);
        }

        public <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(VoidReturn.Executes<Target, P1, P2> executes) {
            return new VoidReturn<>(parameter1, parameter2).executes(executes);
        }

        public static class VoidReturn<P1, P2> {
            private final Parameter<P1> parameter1;
            private final Parameter<P2> parameter2;

            protected VoidReturn(Parameter<P1> parameter1, Parameter<P2> parameter2) {
                this.parameter1 = parameter1;
                this.parameter2 = parameter2;
            }

            @FunctionalInterface
            public interface Executes<Target, P1, P2> {
                void run(InternalArgument<Target> target, InternalArgument<P1> parameter1, InternalArgument<P2> parameter2) throws CommandRunException;
            }

            public <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(Executes<Target, P1, P2> executes) {
                return SignatureParameterized.of(new InstanceExecution<>((t, p) -> {
                    executes.run(t, (InternalArgument<P1>) p.get(0), (InternalArgument<P2>) p.get(1));
                    return InternalVoidArgument.getInstance();
                }, InternalVoidArgument.class, null, parameter1, parameter2));
            }
        }

        public <Return> ObjectReturn<P1, P2, Return> returns(Class<? extends InternalArgument<Return>> returnClass) {
            return new ObjectReturn<>(parameter1, parameter2, returnClass, null);
        }

        public <Return> ObjectReturn<P1, P2, Return> returns(Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
            return new ObjectReturn<>(parameter1, parameter2, returnClass, returnMessage);
        }

        public static class ObjectReturn<P1, P2, Return> {
            private final Parameter<P1> parameter1;
            private final Parameter<P2> parameter2;
            private final Class<? extends InternalArgument<Return>> returnClass;
            private final String returnMessage;

            protected ObjectReturn(Parameter<P1> parameter1, Parameter<P2> parameter2, Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
                this.parameter1 = parameter1;
                this.parameter2 = parameter2;
                this.returnClass = returnClass;
                this.returnMessage = returnMessage;
            }

            @FunctionalInterface
            public interface Executes<Target, P1, P2, Return> {
                InternalArgument<Return> run(InternalArgument<Target> target, InternalArgument<P1> parameter1, InternalArgument<P2> parameter2) throws CommandRunException;
            }

            public <Target> SignatureParameterized<InstanceExecution<Target, Return>> executes(Executes<Target, P1, P2, Return> executes) {
                return SignatureParameterized.of(new InstanceExecution<>(
                        (t, p) -> executes.run(t, (InternalArgument<P1>) p.get(0), (InternalArgument<P2>) p.get(1)),
                        returnClass, returnMessage,
                        parameter1, parameter2
                ));
            }
        }
    }

    /////////////////////////////
    // SECTION: Many Parameters //
    /////////////////////////////

    public static ManyParameter withParameters(Parameter<?>... parameters) {
        return new ManyParameter(parameters);
    }

    public static class ManyParameter {
        private final Parameter<?>[] parameters;

        protected ManyParameter(Parameter<?>... parameters) {
            this.parameters = parameters;
        }

        public VoidReturn returns() {
            return new VoidReturn(parameters);
        }

        public <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(VoidReturn.Executes<Target> executes) {
            return new VoidReturn(parameters).executes(executes);
        }

        public static class VoidReturn {
            private final Parameter<?>[] parameters;

            protected VoidReturn(Parameter<?>[] parameters) {
                this.parameters = parameters;
            }

            @FunctionalInterface
            public interface Executes<Target> {
                void run(InternalArgument<Target> target, List<InternalArgument<?>> parameters) throws CommandRunException;
            }

            public <Target> SignatureParameterized<InstanceExecution<Target, Void>> executes(Executes<Target> executes) {
                return SignatureParameterized.of(new InstanceExecution<>((t, p) -> {
                    executes.run(t, p);
                    return InternalVoidArgument.getInstance();
                }, InternalVoidArgument.class, null, parameters));
            }
        }

        public <Return> ObjectReturn<Return> returns(Class<? extends InternalArgument<Return>> returnClass) {
            return new ObjectReturn<>(parameters, returnClass, null);
        }

        public <Return> ObjectReturn<Return> returns(Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
            return new ObjectReturn<>(parameters, returnClass, returnMessage);
        }

        public static class ObjectReturn<Return> {
            private final Parameter<?>[] parameters;
            private final Class<? extends InternalArgument<Return>> returnClass;
            private final String returnMessage;

            protected ObjectReturn(Parameter<?>[] parameters, Class<? extends InternalArgument<Return>> returnClass, String returnMessage) {
                this.parameters = parameters;
                this.returnClass = returnClass;
                this.returnMessage = returnMessage;
            }

            @FunctionalInterface
            public interface Executes<Target, Return> {
                InternalArgument<Return> run(InternalArgument<Target> target, List<InternalArgument<?>> parameters) throws CommandRunException;
            }

            public <Target> SignatureParameterized<InstanceExecution<Target, Return>> executes(Executes<Target, Return> executes) {
                return SignatureParameterized.of(new InstanceExecution<>(executes::run, returnClass, returnMessage, parameters));
            }
        }
    }
}
