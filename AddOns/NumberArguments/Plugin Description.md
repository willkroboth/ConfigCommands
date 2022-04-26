# NumberArguments v 1.0.0
## Spigot api-version: 1.18
## Dependencies
[CommandAPI](https://commandapi.jorel.dev/) v 7.0.0 by [JorelAli](https://github.com/JorelAli) ([download](https://github.com/JorelAli/CommandAPI/releases/download/7.0.0/CommandAPI-7.0.0.jar))

[ConfigCommands](/../../) v 1.0.0

For server owners: Make sure to put both the CommandAPI.jar and ConfigCommand.jar files in your plugins folder.

## FunctionAdders provided by NumberArguments:
- [IntegerFunctionAdder](#integerfunctionadder)

## InternalArguments provided by NumberArguments:
- [InternalDoubleArgument](#internaldoubleargument)
- [InternalFloatArgument](#internalfloatargument)
- [InternalFloatRangeArgument](#internalfloatrangeargument)
- [InternalIntegerRangeArgument](#internalintegerrangeargument)
- [InternalLongArgument](#internallongargument)


## Function Interfaces
### [NumberFunctions](./src/me/willkroboth/NumberArguments/InternalArguments/NumberFunctions.java)
#### Known implementing classes:
- [IntegerFunctionAdder](#integerfunctionadder)
- [InternalDoubleArgument](#internaldoubleargument)
- [InternalFloatArgument](#internalfloatargument)
- [InternalLongArgument](#internallongargument)
#### Functions:

- < / lessThan

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: InternalBooleanArgument

Description: Returns true if the target number is less than the given number and false otherwise

- <= / lessThanOrEqualTo

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: InternalBooleanArgument

Description: Returns true if the target number is less than or equal to the given number and false otherwise

- \> / greaterThan

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: InternalBooleanArgument

Description: Returns true if the target number is greater than the given number and false otherwise

- \>= / greaterThanOrEqualTo

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: InternalBoolean

Description: Returns true if the target number is greater than or equal to the given number and false otherwise

- == / equalTo

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: InternalBooleanArgument

Description: Returns true if the target number is equal to the given number and false otherwise

- != / notEqualTo

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: InternalBooleanArgument

Description: Returns true if the target number is not equal to the given number and false otherwise

- \+ / add

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: Target class

Description: Returns the result of adding the target and given number

- \- / subtract

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: Target class

Description: Returns the result of subtracting the given number from the target number

- \* / multiply

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: Target class

Description: Returns the result of multiplying the target and given number
Result: Target class

Description: Returns the result of subtracting the given number from the target number

- / / divide

Parameters: InternalDoubleArgument / InternalFloatArgument / InternalIntegerArgument / InternalLongArgument

Result: Target class

Description: Returns the result of dividing the target number by the given number


## Function Adders
### [IntegerFunctionAdder](./src/me/willkroboth/NumberArguments/InternalArguments/IntegerFunctionAdder.java)
#### Class to add to: [InternalIntegerArgument](/Plugin%20Description.md#internalintegerargument)
#### Added functions:
[NumberFunctions](#numberfunctions)

#### Added static functions:

- maxValue

Parameters: None

Result: InternalIntegerArgument

Description: Returns the maximum value that can be stored in an int

- minValue

Parameters: None

Result: InternalIntegerArgument

Description: Returns the minimum value that can be stored in an int

## Internal Arguments
### [InternalDoubleArgument](./src/me/willkroboth/NumberArguments/InternalArguments/InternalDoubleArgument.java)
#### Adding to command:
type: Double
#### Java Class: [Double](https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html)
#### Functions:
[NumberFunctions](#numberfunctions)

- forCommand

Parameters: None

Result: InternalStringArgument

Description: Returns a string representation of the target double in decimal

#### Static Class Name: Double
#### Static Functions:

- maxValue

Parameters: None

Result: InternalDoubleArgument

Description: Returns a double contianing the value positive infinity

- minValue

Parameters: None

Result: InternalDoubleArgument

Description: Returns a double containing the value negative infinity

### [InternalFloatArgument](./src/me/willkroboth/NumberArguments/InternalArguments/InternalFloatArgument.java)
#### Adding to command:
type: Float

min - determines the minimum value that may be input to the command

max - determines the maximum value that may be input to the command

#### Java Class: [Float](https://docs.oracle.com/javase/7/docs/api/java/lang/Float.html)
#### Functions:
[NumberFunctions](#numberfunctions)

- forCommand

Parameters: None

Result: InternalStringArgument

Description: Returns a string representation of the target float in decimal.

#### Static Class Name: Float
#### Static Functions:

- maxValue

Parameters: None

Result: InternalFloatArgument

Description: Returns a float containing the value positive infinity

- minValue

Parameters: None

Result: InternalFloatArgument

Description: Returns a float containing the value negative infinity

### [InternalFloatRangeArgument](./src/me/willkroboth/NumberArguments/InternalArguments/InternalFloatRangeArgument.java)
#### Adding to command:
type: FloatRange

#### Java Class: [FloatRange](https://commandapi.jorel.dev/javadocs/html/classdev_1_1jorel_1_1commandapi_1_1wrappers_1_1_float_range.html)
#### Functions:

- getLowerBound

Parameters: None

Result: InternalFloatArgument

Description: Returns the lower bound of the target FloatRange

- getUpperBound

Parameters: None

Result: InternalFloatArgument

Description: Returns the upper bound of the target FloatRange

- isInRange

Parameters: InternalIntegerArgument / InternalDoubleArgument / InternalFloatArgument / InternalLongArgument

Result: InternalBooleanArgument

Description: Returns true if the given number is within the range of the target FloatRange

- setLowerBound

Parameters: InternalFloatArgument

Result: None

Description: Sets the lower bound of the target FloatRange to the given float

- setUpperBound

Parameters: InternalFloatArgument

Result: None

Description: Sets the upper bound of the target FloatRange to the given float

#### Static Class Name: FloatRange
#### Static Functions:

- "" / new

Parameters: InternalFloatArgument, InternalFloatArgument

Result: InternalFloatRangeArgument

Description: Creates a new FloatRange with the lower bound at the first float and the upper bound at the second float

- newGreaterThanOrEqual

Parameters: InternalFloatArgument

Result: InternalFloatRange

Description: Creates a new FloatRange with the lower bound at the given float and the upper bound at the greatest finite float

- newLessThanOrEqual

Parameters: InternalFloatArgument

Result: InternalFloatRange

Description: Creates a new FloatRange with the upper bound at the given float and the lower bound at the most negative finite float



### [InternalIntegerRangeArgument](./src/me/willkroboth/NumberArguments/InternalArguments/InternalIntegerRangeArgument.java)
#### Adding to command:
type: IntegerRange

#### Java Class: [IntegerRange](https://commandapi.jorel.dev/javadocs/html/classdev_1_1jorel_1_1commandapi_1_1wrappers_1_1_integer_range.html)
#### Functions:

- getLowerBound

Parameters: None

Result: InternalIntegerArgument

Description: Returns the lower bound of the target IntegerRange

- getUpperBound

Parameters: None

Result: InternalIntegerArgument

Description: Returns the upper bound of the target IntegerRange

- isInRange

Parameters: InternalIntegerArgument / InternalDoubleArgument / InternalFloatArgument / InternalLongArgument

Result: InternalBooleanArgument

Description: Returns true if the given number is within the range of the target IntegerRange

- setLowerBound

Parameters: InternalIntegerArgument

Result: None

Description: Sets the lower bound of the target IntegerRange to the given int

- setUpperBound

Parameters: InternalIntegerArgument

Result: None

Description: Sets the upper bound of the target IntegerRange to the given int

#### Static Class Name: IntegerRange
#### Static Functions:

- "" / new

Parameters: InternalIntegerArgument, InternalIntegerArgument

Result: InternalIntegerRangeArgument

Description: Creates a new IntegerRange with the lower bound at the first int and the upper bound at the second int

- newGreaterThanOrEqual

Parameters: InternalIntegerArgument

Result: InternalIntegerRange

Description: Creates a new IntegerRange with the lower bound at the given int and the upper bound at the greatest finite int

- newLessThanOrEqual

Parameters: InternalIntegerArgument

Result: InternalIntegerRange

Description: Creates a new IntegerRange with the upper bound at the given int and the lower bound at the most negative finite int

### [InternalLongArgument](./src/me/willkroboth/NumberArguments/InternalArguments/InternalLongArgument.java)
#### Adding to command:
type: Long
#### Java Class: [Long](https://docs.oracle.com/javase/8/docs/api/java/lang/Long.html)
#### Functions:
[NumberFunctions](#numberfunctions)

- forCommand

Parameters: None

Result: InternalStringArgument

Description: Returns a string representation of the target long in decimal

#### Static Class Name: Long
#### Static Functions:

- maxValue

Parameters: None

Result: InternalLongArgument

Description: Returns a long with the greatest value a long can store

- minValue

Parameters: None

Result: InternalLongArgument

Description: Returns a long with the largest negative value a long can store.
