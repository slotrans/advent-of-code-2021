# Kotlin lessons

## equals/hashcode
In order for Kotlin == (which invokes Java .equals) to work in a value-oriented way with with `Set<T>`, `T` needs value-oriented equals & hashcode. A `data class` gives you that for free, but a regular `class` doesn't.

## constructors
Contrary to what I had read/interpreted earlier, classes need not have a default constructor defined in the class signature e.g. `class Foo(...)`. You can just skip that, and define any number of `constructor(...)` methods as desired. Member fields can be `val` (vs `var`) as long as _all_ such constructors cause them to be initialized.

## namespacing
In the simple structure I have (with one module, not that I know how to define more...), any classes defined at the outermost level appear to go into a global namespace. So for example the `Grid` class I made for #5 and the `Tile` class I made for #4 are now names I can't use elsewhere. I had hoped that moving those class definitions _inside_ the `object PuzzleXX` scope would keep them out of that namespace, but this appears to be false. Perhaps the only way to get proper namespacing is to move each puzzle into its own package.


# JUnit lessons

## argument order
For the basic `assertEquals()` call, the arguments are `assertEquals(expected, valuetoTest)`, not the other way around. It _works_ either way, but failed test reporting shows "expected" and "actual" values based on that argument order, so it's best to use it even though it's awkward.

## collections
Testing collections for equality using `assertEquals` seems to be fine, as long as equals/hashcode are taken care of as per the above.


# AoC lessons

Using a JUnit test class to work through the sample seems to be cleaner / less repetitive than writing e.g. `samplePart1()` functions.
